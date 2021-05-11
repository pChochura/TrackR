package com.pointlessapps.trackr.charts

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.color.MaterialColors
import com.pointlessapps.trackr.R
import com.pointlessapps.trackr.adapters.AdapterActivityIncomePercentage
import com.pointlessapps.trackr.databinding.ChartTotalEarningsBinding
import com.pointlessapps.trackr.models.Activity
import com.pointlessapps.trackr.models.Event
import com.pointlessapps.trackr.models.Salary
import com.pointlessapps.trackr.services.ServiceActivityCalculator
import com.pointlessapps.trackr.services.ServiceFirebase
import com.pointlessapps.trackr.utils.ItemSpacingDecoration
import com.pointlessapps.trackr.utils.ifZero
import com.pointlessapps.trackr.utils.setToBeginMonth
import com.pointlessapps.trackr.utils.setToEndMonth
import kotlinx.coroutines.*
import java.util.*

class ChartTotalEarnings(context: Context) :
	ChartCore<ChartTotalEarningsBinding>(context, ChartTotalEarningsBinding::inflate) {

	private val coroutineScope = CoroutineScope(Dispatchers.Default + Job())

	init {
		coroutineScope.launch {
			val eventsByMonth = getEventsByMonth()
			val unit = getCurrentMonthSalaryUnit(eventsByMonth.first)

			// TODO: Add converting units
			val previousIncome = eventsByMonth.second.sumOf {
				ServiceActivityCalculator.calculateIncomeForEvent(it)?.amount ?: 0.0
			}
			val currentIncome = eventsByMonth.first.sumOf {
				ServiceActivityCalculator.calculateIncomeForEvent(it)?.amount ?: 0.0
			}

			val eventsIncomePercentage = getEventIncomePercentage(eventsByMonth, currentIncome)

			withContext(Dispatchers.Main) {
				updateUI(eventsIncomePercentage, currentIncome, previousIncome, unit)
			}
		}
	}

	private fun getEventIncomePercentage(
		eventsByMonth: Pair<List<Event>, List<Event>>,
		currentIncome: Double
	) = eventsByMonth.first.groupBy { it.activity.id }
		.mapKeys { it.value.first().activity }
		.mapValues {
			it.value.sumOf { event ->
				ServiceActivityCalculator.calculateIncomeForEvent(event)?.amount ?: 0.0
			} / currentIncome * 100
		}
		.filterValues { it > 0.0 }
		.toList()

	private fun getCurrentMonthSalaryUnit(events: List<Event>) = events
		.filter { it.activity.salary != null }
		.groupBy { it.activity.salary?.unit }
		.maxByOrNull { it.value.size }?.key ?: Salary.Unit.DOLLAR

	private suspend fun getEventsByMonth(): Pair<List<Event>, List<Event>> {
		val calendar = Calendar.getInstance()
		val startMonthTime = calendar.setToBeginMonth(-1).timeInMillis
		val endMonthTime = calendar.setToEndMonth(1).timeInMillis

		val events = ServiceFirebase.getEventsBetween(context, startMonthTime, endMonthTime)
		val currentMonth = calendar.get(Calendar.MONTH)
		return events.partition {
			calendar.timeInMillis = it.date.time
			calendar.get(Calendar.MONTH) == currentMonth
		}
	}

	private fun updateUI(
		eventsIncomePercentage: List<Pair<Activity, Double>>,
		currentIncome: Double,
		previousIncome: Double,
		unit: Salary.Unit
	) {
		with(binding.listItems) {
			layoutManager = LinearLayoutManager(context)
			adapter = AdapterActivityIncomePercentage(eventsIncomePercentage)
			addItemDecoration(
				ItemSpacingDecoration(
					RecyclerView.VERTICAL,
					color = MaterialColors.getColor(
						context,
						R.attr.colorSecondaryVariant,
						Color.TRANSPARENT
					)
				)
			)
		}

		binding.textIncome.text =
			context.getString(R.string.income_formatted, currentIncome, unit.unit)
		binding.textComparison.text =
			context.getString(R.string.compared_to_last_month, previousIncome, unit.unit)
		when (previousIncome > currentIncome) {
			true -> {
				val percent = (previousIncome - currentIncome) / previousIncome.ifZero(1.0)
				updateTextPercentage(percent, R.color.text_red, R.drawable.ic_arrow_filled_down)
			}
			false -> {
				val percent = (currentIncome - previousIncome) / previousIncome.ifZero(1.0)
				updateTextPercentage(percent, R.color.text_green, R.drawable.ic_arrow_filled_up)
			}
		}
		onFinishedLoadingListener?.invoke(this@ChartTotalEarnings)
	}

	private fun updateTextPercentage(
		percent: Double,
		@ColorRes textColor: Int,
		@DrawableRes arrowDrawable: Int
	) {
		ContextCompat.getColor(context, textColor).also {
			binding.textComparisonPercent.setTextColor(it)
			TextViewCompat.setCompoundDrawableTintList(
				binding.textComparisonPercent,
				ColorStateList.valueOf(it)
			)
			binding.textComparisonPercent.setCompoundDrawablesWithIntrinsicBounds(
				ContextCompat.getDrawable(context, arrowDrawable),
				null, null, null
			)
		}
		binding.textComparisonPercent.text =
			context.getString(R.string.percent, percent * 100)
	}
}