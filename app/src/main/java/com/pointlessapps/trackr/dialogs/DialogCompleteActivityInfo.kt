package com.pointlessapps.trackr.dialogs

import android.content.res.ColorStateList
import androidx.activity.ComponentActivity
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import com.pointlessapps.trackr.R
import com.pointlessapps.trackr.databinding.DialogCompleteActivityInfoBinding
import com.pointlessapps.trackr.models.*
import java.util.*

class DialogCompleteActivityInfo(
	private val activity: ComponentActivity,
	activityInfo: Activity,
	private val showOnlyRequired: Boolean = false
) : DialogCore<DialogCompleteActivityInfoBinding>(
	activity,
	DialogCompleteActivityInfoBinding::inflate
) {

	private val event = MutableLiveData(
		Event(
			date = Date(),
			activity = Activity(activityInfo)
		)
	)
	private var onAddedListener: ((Event) -> Unit)? = null

	override fun show(): DialogCompleteActivityInfo {
		if (showOnlyRequired) {
			val errors = checkForErrors()
			if (errors?.size == 1) {
				when (errors.first()) {
					ErrorType.EMPTY_PERIOD -> {
						DialogPickTime(activity, TimePeriod(), clearable = false)
							.setOnPickedListener {
								updateTimePeriod(it)
								onAddedListener?.invoke(event.value!!)
							}.show()
					}
					ErrorType.EMPTY_TIME_RANGE -> {
						DialogPickTimeRange(activity, TimeRange(), clearable = false)
							.setOnPickedListener {
								updateTimeRange(it)
								onAddedListener?.invoke(event.value!!)
							}.show()
					}
				}

				return this
			}
		}

		makeDialog { binding, dialog ->
			binding.buttonCancel.setOnClickListener { dialog.dismiss() }
			binding.buttonAdd.setOnClickListener {
				val errors = checkForErrors()
				if (errors != null) {
					displayErrors(binding, errors)

					return@setOnClickListener
				}

				onAddedListener?.invoke(event.value!!)
				dialog.dismiss()
			}

			event.observe(activity) { event ->
				binding.textTitle.text = event.activity.name
				prepareSalaryComponent(binding, event)
				prepareTimePeriodComponent(binding, event)
				prepareTimeRangeComponent(binding, event)
				prepareDateComponent(binding, event)
			}
		}

		return this
	}

	private fun checkForErrors(): List<ErrorType>? {
		val errors = mutableListOf<ErrorType>()
		if (event.value!!.activity.type is ActivityType.PeriodBased &&
			(event.value!!.activity.type as ActivityType.PeriodBased).period == null
		) {
			errors.add(ErrorType.EMPTY_PERIOD)
		}
		if (event.value!!.activity.type is ActivityType.TimeBased &&
			(event.value!!.activity.type as ActivityType.TimeBased).range == null
		) {
			errors.add(ErrorType.EMPTY_TIME_RANGE)
		}

		return errors.takeIf(List<ErrorType>::isNotEmpty)
	}

	private fun displayErrors(binding: DialogCompleteActivityInfoBinding, errors: List<ErrorType>) {
		errors.forEach {
			when (it) {
				ErrorType.EMPTY_PERIOD -> {
					binding.buttonPeriod.strokeColor =
						ColorStateList.valueOf(activity.getColor(R.color.red))
					binding.buttonPeriodErrorLabel.isVisible = true
				}
				ErrorType.EMPTY_TIME_RANGE -> {
					binding.buttonTimeRange.strokeColor =
						ColorStateList.valueOf(activity.getColor(R.color.red))
					binding.buttonTimeRangeErrorLabel.isVisible = true
				}
			}
		}
	}

	private fun prepareSalaryComponent(
		binding: DialogCompleteActivityInfoBinding,
		event: Event
	) {
		val activityInfo = event.activity
		binding.containerSalary.isVisible = activityInfo.salary != null
		if (activityInfo.salary == null) {
			return
		}

		binding.buttonSalary.setOnClickListener {
			DialogSpecifySalary(activity, activityInfo.salary!!, clearable = false).show()
				.setOnSavedListener(::updateSalary)
		}

		val salary = activityInfo.salary!!
		binding.buttonSalary.text = activity.getString(
			R.string.salary_formatted,
			salary.amount,
			salary.unit,
			salary.type.getAbbreviation(activity)
		)
	}

	private fun prepareTimePeriodComponent(
		binding: DialogCompleteActivityInfoBinding,
		event: Event
	) {
		val activityInfo = event.activity
		binding.containerPeriod.isVisible = activityInfo.type is ActivityType.PeriodBased
		if (activityInfo.type !is ActivityType.PeriodBased) {
			return
		}

		val period = (activityInfo.type as ActivityType.PeriodBased).period
		binding.buttonPeriod.setOnClickListener {
			DialogPickTime(activity, period ?: TimePeriod(), clearable = false).show()
				.setOnPickedListener(::updateTimePeriod)
		}

		if (period == null) {
			binding.buttonPeriod.setText(R.string.unset)

			return
		}

		binding.buttonPeriod.text = activity.getString(
			R.string.period_formatted,
			period.hours,
			period.minutes,
		)
	}

	private fun prepareTimeRangeComponent(
		binding: DialogCompleteActivityInfoBinding,
		event: Event
	) {
		val activityInfo = event.activity
		binding.containerTimeRange.isVisible = activityInfo.type is ActivityType.TimeBased
		if (activityInfo.type !is ActivityType.TimeBased) {
			return
		}

		val range = (activityInfo.type as ActivityType.TimeBased).range
		binding.buttonTimeRange.setOnClickListener {
			DialogPickTimeRange(activity, range ?: TimeRange(), clearable = false).show()
				.setOnPickedListener(::updateTimeRange)
		}

		if (range == null) {
			binding.buttonTimeRange.setText(R.string.unset)

			return
		}

		binding.buttonTimeRange.text = activity.getString(
			R.string.time_range_formatted,
			range.startTime.hours,
			range.startTime.minutes,
			range.endTime.hours,
			range.endTime.minutes,
		)
	}

	private fun prepareDateComponent(
		binding: DialogCompleteActivityInfoBinding,
		event: Event
	) {
		binding.buttonDate.setOnClickListener {
			DialogPickDate(activity, event.date, clearable = false).show()
				.setOnPickedListener(::updateDate)
		}

		binding.buttonDate.text = activity.getString(R.string.date_formatted, event.date)
	}

	private fun updateSalary(salary: Salary?) {
		event.value = event.value?.also {
			it.activity.salary = salary
		}
	}

	private fun updateTimePeriod(timePeriod: TimePeriod?) {
		event.value = event.value?.also {
			(it.activity.type as ActivityType.PeriodBased).period = timePeriod
		}
	}

	private fun updateTimeRange(timeRange: TimeRange?) {
		event.value = event.value?.also {
			(it.activity.type as ActivityType.TimeBased).range = timeRange
		}
	}

	private fun updateDate(date: Date?) {
		if (date == null) {
			return
		}

		event.value = event.value?.also {
			it.date.time = date.time
		}
	}

	fun setOnAddedListener(onAddedListener: (Event) -> Unit): DialogCompleteActivityInfo {
		this.onAddedListener = onAddedListener

		return this
	}

	private enum class ErrorType {
		EMPTY_PERIOD, EMPTY_TIME_RANGE
	}
}