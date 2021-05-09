package com.pointlessapps.trackr.charts

import android.content.Context
import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.pointlessapps.trackr.R
import com.pointlessapps.trackr.adapters.AdapterActivityIncomePercentage
import com.pointlessapps.trackr.databinding.ChartTotalEarningsBinding
import com.pointlessapps.trackr.models.Salary
import com.pointlessapps.trackr.services.ServiceActivityCalculator
import com.pointlessapps.trackr.services.ServiceFirebase
import com.pointlessapps.trackr.utils.ItemSpacingDecoration
import com.pointlessapps.trackr.utils.setToBeginMonth
import com.pointlessapps.trackr.utils.setToEndMonth
import kotlinx.coroutines.*
import java.util.*

class ChartTotalEarnings(context: Context) :
	ChartCore<ChartTotalEarningsBinding>(context, ChartTotalEarningsBinding::inflate) {

	private val coroutineScope = CoroutineScope(Dispatchers.Default + Job())

	init {
		val calendar = Calendar.getInstance()
		val startMonthTime = calendar.setToBeginMonth(-1).timeInMillis
		val endMonthTime = calendar.setToEndMonth(1).timeInMillis

		coroutineScope.launch {
			val events = ServiceFirebase.getEventsBetween(startMonthTime, endMonthTime)
			val currentMonth = calendar.get(Calendar.MONTH)
			val eventsByMonth = events.partition {
				calendar.timeInMillis = it.date.time
				calendar.get(Calendar.MONTH) == currentMonth
			}
			val unit = eventsByMonth.first
				.groupBy { it.activity.salary?.unit }
				.maxByOrNull { it.value.size }?.key ?: Salary.Unit.DOLLAR

			// TODO: Add converting units
			val previousIncome = eventsByMonth.second.sumOf {
				ServiceActivityCalculator.calculateIncomeForEvent(it)?.amount ?: 0.0
			}
			val currentIncome = eventsByMonth.first.sumOf {
				ServiceActivityCalculator.calculateIncomeForEvent(it)?.amount ?: 0.0
			}

			val eventsThisMonth =
				eventsByMonth.first.groupBy { it.activity.id }
					.mapKeys { it.value.first().activity }
					.mapValues {
						it.value.sumOf { event ->
							ServiceActivityCalculator.calculateIncomeForEvent(event)?.amount ?: 0.0
						} / currentIncome * 100
					}.toList()

			withContext(Dispatchers.Main) {
				with(binding.listItems) {
					layoutManager = LinearLayoutManager(context)
					adapter = AdapterActivityIncomePercentage(eventsThisMonth)
					addItemDecoration(ItemSpacingDecoration(RecyclerView.VERTICAL))
				}

				binding.textIncome.text =
					context.getString(R.string.income_formatted, currentIncome, unit.unit)
				binding.textComparison.text =
					context.getString(R.string.compared_to_last_month, previousIncome, unit.unit)
				when (previousIncome > currentIncome) {
					true -> {
						val percent = (previousIncome - currentIncome) / currentIncome
						ContextCompat.getColor(context, R.color.text_red).also {
							binding.textComparisonPercent.setTextColor(it)
							TextViewCompat.setCompoundDrawableTintList(
								binding.textComparisonPercent,
								ColorStateList.valueOf(it)
							)
							binding.textComparisonPercent.setCompoundDrawablesWithIntrinsicBounds(
								ContextCompat.getDrawable(context, R.drawable.ic_arrow_filled_down),
								null, null, null
							)
						}
						binding.textComparisonPercent.text =
							context.getString(R.string.percent, percent * 100)
					}
					false -> {
						val percent = (currentIncome - previousIncome) / currentIncome
						ContextCompat.getColor(context, R.color.text_green).also {
							binding.textComparisonPercent.setTextColor(it)
							TextViewCompat.setCompoundDrawableTintList(
								binding.textComparisonPercent,
								ColorStateList.valueOf(it)
							)
							binding.textComparisonPercent.setCompoundDrawablesWithIntrinsicBounds(
								ContextCompat.getDrawable(context, R.drawable.ic_arrow_filled_up),
								null, null, null
							)
						}
						binding.textComparisonPercent.text =
							context.getString(R.string.percent, percent * 100)
					}
				}
				binding.container.isVisible = true
				binding.listItems.isVisible = true
				TransitionManager.beginDelayedTransition(binding.root, AutoTransition())
			}
		}
	}
}