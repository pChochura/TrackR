package com.pointlessapps.trackr.charts

import android.content.Context
import com.pointlessapps.trackr.databinding.ChartMostFrequentBinding
import com.pointlessapps.trackr.models.*
import com.pointlessapps.trackr.services.ServiceActivityCalculator
import com.pointlessapps.trackr.services.ServiceFirebase
import com.pointlessapps.trackr.utils.setToBeginMonth
import com.pointlessapps.trackr.utils.setToEndMonth
import kotlinx.coroutines.*
import java.util.*

class ChartMostFrequent(context: Context) :
	ChartCore<ChartMostFrequentBinding>(context, ChartMostFrequentBinding::inflate) {

	private val coroutineScope = CoroutineScope(Dispatchers.Default + Job())

	init {
		coroutineScope.launch {
			val mostFrequent = getMostFrequentActivity() ?: return@launch
			val activity = mostFrequent.first
			val events = mostFrequent.second
			withContext(Dispatchers.Main) {
				updateUI(activity, events)
			}
		}
	}

	private suspend fun getMostFrequentActivity(): Pair<Activity, List<Event>>? {
		val calendar = Calendar.getInstance()
		val startMonthTime = calendar.setToBeginMonth().timeInMillis
		val endMonthTime = calendar.setToEndMonth().timeInMillis

		val eventsThisMonth =
			ServiceFirebase.getEventsBetween(context, startMonthTime, endMonthTime)
		val eventsByActivity = eventsThisMonth.groupBy { it.activity.id }

		return eventsByActivity
			.mapKeys { it.value.first().activity }
			.maxByOrNull { it.value.size }?.toPair()
	}

	private fun updateUI(activity: Activity, events: List<Event>) {
		binding.imageIcon.setImageResource(activity.icon)
		binding.imageIcon.setColorFilter(activity.color)
		binding.progress.color = activity.color
		binding.progress.setProgress(
			ServiceActivityCalculator.getPercentageThisMonth(
				activity,
				events
			)
		)
		binding.textTitle.text = activity.name
		binding.textContent.text =
			ServiceActivityCalculator.getSummary(context, activity, events)
		onFinishedLoadingListener?.invoke(this@ChartMostFrequent)
	}
}