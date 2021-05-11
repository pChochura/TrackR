package com.pointlessapps.trackr.charts

import android.content.Context
import android.graphics.Color
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.color.MaterialColors
import com.pointlessapps.trackr.R
import com.pointlessapps.trackr.adapters.AdapterActivityTimeSpentComparison
import com.pointlessapps.trackr.databinding.ChartTimeSpentThisMonthBinding
import com.pointlessapps.trackr.models.*
import com.pointlessapps.trackr.services.ServiceActivityCalculator
import com.pointlessapps.trackr.services.ServiceFirebase
import com.pointlessapps.trackr.utils.ItemSpacingDecoration
import com.pointlessapps.trackr.utils.setToBeginMonth
import com.pointlessapps.trackr.utils.setToEndMonth
import kotlinx.coroutines.*
import java.util.*

class ChartTimeSpentThisMonth(context: Context) :
	ChartCore<ChartTimeSpentThisMonthBinding>(context, ChartTimeSpentThisMonthBinding::inflate) {

	private val coroutineScope = CoroutineScope(Dispatchers.Default + Job())

	init {
		coroutineScope.launch {
			val eventsByMonth = getEventsByActivity()
			val calendar = Calendar.getInstance()
			val currentMonth = calendar.get(Calendar.MONTH)
			val eventsTimeSpentComparison =
				getEventsTimeSpentComparison(eventsByMonth, calendar, currentMonth)
			withContext(Dispatchers.Main) {
				updateUI(eventsTimeSpentComparison)
			}
		}
	}

	private fun getEventsTimeSpentComparison(
		eventsByMonth: Map<Activity, List<Event>>,
		calendar: Calendar,
		currentMonth: Int
	) = eventsByMonth
		.filterKeys { it.type !is ActivityType.OneTime }
		.mapValues {
			it.value.partition { event ->
				calendar.timeInMillis = event.date.time
				calendar.get(Calendar.MONTH) == currentMonth
			}
		}.mapValues {
			ServiceActivityCalculator.getTimeSummary(
				it.key,
				it.value.first
			) to ServiceActivityCalculator.getTimeSummary(
				it.key,
				it.value.second
			)
		}.toList()

	private suspend fun getEventsByActivity(): Map<Activity, List<Event>> {
		val calendar = Calendar.getInstance()
		val startMonthTime = calendar.setToBeginMonth(-1).timeInMillis
		val endMonthTime = calendar.setToEndMonth(1).timeInMillis

		val events = ServiceFirebase.getEventsBetween(context, startMonthTime, endMonthTime)
		return events.groupBy { it.activity.id }.mapKeys { it.value.first().activity }
	}

	private fun updateUI(eventsTimeSpentComparison: List<Pair<Activity, Pair<TimePeriod, TimePeriod>>>) {
		with(binding.listItems) {
			layoutManager = LinearLayoutManager(context)
			adapter = AdapterActivityTimeSpentComparison(eventsTimeSpentComparison)
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
		onFinishedLoadingListener?.invoke(this@ChartTimeSpentThisMonth)
	}
}