package com.pointlessapps.trackr.services

import com.pointlessapps.trackr.models.ActivityType
import com.pointlessapps.trackr.models.Event
import com.pointlessapps.trackr.models.Income
import com.pointlessapps.trackr.models.Salary
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.ceil

object ServiceIncomeCalculator {

	fun calculateIncomeForEvent(event: Event): Income? {
		val salary = event.activity.salary ?: return null

		val amount = when (val activityType = event.activity.type) {
			is ActivityType.OneTime -> calculateSalaryFor(
				event,
				salary,
				TimeUnit.DAYS.toHours(1).toFloat()
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

	private fun calculateSalaryFor(event: Event, salary: Salary, time: Float): Float {
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
			Salary.Type.PER_MONTH -> time / availableDaysInMonth * salary.amount
			Salary.Type.PER_OCCURRENCE -> salary.amount
		}
	}
}