package com.pointlessapps.trackr.charts

import android.content.Context
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
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
		val calendar = Calendar.getInstance()
		val startMonthTime = calendar.setToBeginMonth().timeInMillis
		val endMonthTime = calendar.setToEndMonth().timeInMillis

		coroutineScope.launch {
			val eventsThisMonth = ServiceFirebase.getEventsBetween(startMonthTime, endMonthTime)
			val eventsByActivity = eventsThisMonth.groupBy { it.activity.id }
			val mostFrequent = eventsByActivity.maxByOrNull { it.value.size } ?: return@launch

			val activity = mostFrequent.value.firstOrNull()?.activity!!
			val events = mostFrequent.value

			withContext(Dispatchers.Main) {
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
				binding.progress.alpha = 1f
				binding.imageIcon.alpha = 1f
				TransitionManager.beginDelayedTransition(binding.root, AutoTransition())
			}
		}
	}
}