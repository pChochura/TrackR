package com.pointlessapps.trackr.services

import android.content.Context
import android.text.Spanned
import androidx.core.text.parseAsHtml
import com.pointlessapps.trackr.R
import com.pointlessapps.trackr.models.*
import com.pointlessapps.trackr.utils.setToBeginMonth
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.ceil

object ServiceActivityCalculator {

	fun calculateIncomeForEvent(event: Event): Income? {
		val salary = event.activity.salary ?: return null

		val amount = when (val activityType = event.activity.type) {
			is ActivityType.OneTime -> calculateSalaryFor(
				event,
				salary,
				TimeUnit.DAYS.toHours(1).toDouble()
			)
			is ActivityType.PeriodBased -> calculateSalaryFor(
				event,
				salary,
				activityType.period!!.getCombined()
			)
			is ActivityType.TimeBased -> calculateSalaryFor(
				event,
				salary,
				activityType.range!!.getCombined()
			)
		}

		return Income(amount, salary.unit)
	}

	private fun calculateSalaryFor(event: Event, salary: Salary, time: Double): Double {
		var availableDaysInMonth = 0
		val calendar = Calendar.getInstance().apply {
			timeInMillis = event.date.time
			set(Calendar.DAY_OF_MONTH, 1)
		}
		val startingMonth = calendar.get(Calendar.MONTH)
		do {
			if (event.activity.weekdayAvailability.getAt(
					(calendar.get(Calendar.DAY_OF_WEEK) + 7 - Calendar.MONDAY) % 7
				)
			) {
				availableDaysInMonth++
			}

			calendar.add(Calendar.DAY_OF_MONTH, 1)
		} while (calendar.get(Calendar.MONTH) == startingMonth)

		return when (salary.type) {
			Salary.Type.PER_HOUR -> time * salary.amount
			Salary.Type.PER_MONTH -> ceil(time / TimeUnit.DAYS.toHours(1)) / availableDaysInMonth * salary.amount
			Salary.Type.PER_OCCURRENCE -> salary.amount
		}
	}

	fun getTimeSummary(activity: Activity, events: List<Event>) =
		when (activity.type) {
			is ActivityType.OneTime -> TimePeriod()
			is ActivityType.PeriodBased -> {
				val sum = events.sumOf { event ->
					(event.activity.type as ActivityType.PeriodBased).period?.getCombined() ?: 0.0
				}
				val hours = sum.toInt()
				val minutes = ((sum - hours) * TimeUnit.HOURS.toMinutes(1)).toInt()
				TimePeriod(hours, minutes)
			}
			is ActivityType.TimeBased -> {
				val sum = events.sumOf { event ->
					(event.activity.type as ActivityType.TimeBased).range?.getCombined() ?: 0.0
				}
				val hours = sum.toInt()
				val minutes = ((sum - hours) * TimeUnit.HOURS.toMinutes(1)).toInt()
				TimePeriod(hours, minutes)
			}
		}

	fun getSummary(context: Context, activity: Activity, events: List<Event>): Spanned {
		val timePeriod = getTimeSummary(activity, events)
		return when (activity.type) {
			is ActivityType.OneTime -> context.resources.getQuantityString(
				R.plurals.times_this_month,
				events.size,
				events.size
			)
			is ActivityType.PeriodBased -> {
				context.resources.getQuantityString(
					R.plurals.hours_times_this_month,
					events.size,
					timePeriod.hours,
					timePeriod.minutes,
					events.size
				)
			}
			is ActivityType.TimeBased -> {
				context.resources.getQuantityString(
					R.plurals.hours_times_this_month,
					events.size,
					timePeriod.hours,
					timePeriod.minutes,
					events.size
				)
			}
		}.parseAsHtml()
	}

	fun getPercentageThisMonth(activity: Activity, events: List<Event>): Float {
		var availableDaysInMonth = 0f
		val calendar = Calendar.getInstance().setToBeginMonth()
		val startingMonth = calendar.get(Calendar.MONTH)
		do {
			if (activity.weekdayAvailability.getAt(
					(calendar.get(Calendar.DAY_OF_WEEK) + 7 - Calendar.MONDAY) % 7
				)
			) {
				availableDaysInMonth++
			}

			calendar.add(Calendar.DAY_OF_MONTH, 1)
		} while (calendar.get(Calendar.MONTH) == startingMonth)

		return events.size / availableDaysInMonth
	}
}