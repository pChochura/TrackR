package com.pointlessapps.trackr.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pointlessapps.trackr.models.*
import com.pointlessapps.trackr.utils.ResourcesUtils

class ViewModelAddActivity(application: Application) : AndroidViewModel(application) {

	val activityTypes = listOf(
		ActivityType.OneTime(),
		ActivityType.PeriodBased(),
		ActivityType.TimeBased(),
	)

	private val _activity = MutableLiveData(
		Activity(
			icon = ResourcesUtils.getRandomIcon(),
			color = ResourcesUtils.getRandomColor(application.applicationContext)
		)
	)
	val activity: LiveData<Activity>
		get() = _activity

	fun updateColorAndIcon(color: Int, icon: Int) {
		_activity.value = _activity.value?.also {
			it.color = color
			it.icon = icon
		}
	}

	fun updateWeekdayAvailability(weekdayAvailability: WeekdayAvailability) {
		_activity.value = _activity.value?.also {
			it.weekdayAvailability = weekdayAvailability
		}
	}

	fun updateSalary(salary: Salary?) {
		_activity.value = _activity.value?.also {
			it.salary = salary
		}
	}

	fun updateActivityType(activityType: ActivityType) {
		_activity.value = _activity.value?.also {
			it.type = activityType
		}
	}

	fun updateTimeRange(timeRange: TimeRange?) {
		_activity.value = _activity.value?.also {
			(it.type as? ActivityType.TimeBased)?.range = timeRange
		}
	}

	fun updateTimePeriod(timePeriod: TimePeriod?) {
		_activity.value = _activity.value?.also {
			(it.type as? ActivityType.PeriodBased)?.period = timePeriod
		}
	}

	fun updateName(name: String) {
		_activity.value = _activity.value?.also {
			it.name = name
		}
	}

	fun checkForErrors(): List<ErrorType>? {
		val errors = mutableListOf<ErrorType>()
		if (_activity.value!!.name.isBlank()) {
			errors.add(ErrorType.BLANK_NAME)
		}

		return errors.takeIf(List<ErrorType>::isNotEmpty)
	}

	enum class ErrorType {
		BLANK_NAME
	}
}
