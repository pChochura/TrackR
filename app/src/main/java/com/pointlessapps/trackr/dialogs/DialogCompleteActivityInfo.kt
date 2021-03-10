package com.pointlessapps.trackr.dialogs

import android.content.res.ColorStateList
import androidx.activity.ComponentActivity
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import com.pointlessapps.trackr.R
import com.pointlessapps.trackr.databinding.DialogCompleteActivityInfoBinding
import com.pointlessapps.trackr.models.*

class DialogCompleteActivityInfo(private val activity: ComponentActivity, activityInfo: Activity) :
	DialogCore<DialogCompleteActivityInfoBinding>(
		activity,
		DialogCompleteActivityInfoBinding::inflate
	) {

	private val activityInfo = MutableLiveData(Activity(activityInfo))
	private var onAddedListener: ((Activity) -> Unit)? = null

	override fun show(): DialogCompleteActivityInfo {
		makeDialog { binding, dialog ->
			binding.buttonCancel.setOnClickListener { dialog.dismiss() }
			binding.buttonAdd.setOnClickListener {
				val errors = checkForErrors()
				if (errors != null) {
					displayErrors(binding, errors)

					return@setOnClickListener
				}

				onAddedListener?.invoke(activityInfo.value!!)
				dialog.dismiss()
			}

			activityInfo.observe(activity) {
				binding.textTitle.text = it.name
				prepareSalaryComponent(binding, it)
				preparePeriodComponent(binding, it)
				prepareTimeRangeComponent(binding, it)
			}
		}

		return this
	}

	private fun checkForErrors(): List<ErrorType>? {
		val errors = mutableListOf<ErrorType>()
		if (activityInfo.value!!.type is ActivityType.PeriodBased &&
			(activityInfo.value!!.type as ActivityType.PeriodBased).period == null
		) {
			errors.add(ErrorType.EMPTY_PERIOD)
		}
		if (activityInfo.value!!.type is ActivityType.TimeBased &&
			(activityInfo.value!!.type as ActivityType.TimeBased).range == null
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
		activityInfo: Activity
	) {
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

	private fun prepareTimeRangeComponent(
		binding: DialogCompleteActivityInfoBinding,
		activityInfo: Activity
	) {
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
			R.string.time_range,
			range.startTime.hours,
			range.startTime.minutes,
			range.endTime.hours,
			range.endTime.minutes,
		)
	}

	private fun preparePeriodComponent(
		binding: DialogCompleteActivityInfoBinding,
		activityInfo: Activity
	) {
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

	private fun updateSalary(salary: Salary?) {
		activityInfo.value = activityInfo.value?.also {
			it.salary = salary
		}
	}

	private fun updateTimePeriod(timePeriod: TimePeriod?) {
		activityInfo.value = activityInfo.value?.also {
			(it.type as ActivityType.PeriodBased).period = timePeriod
		}
	}

	private fun updateTimeRange(timeRange: TimeRange?) {
		activityInfo.value = activityInfo.value?.also {
			(it.type as ActivityType.TimeBased).range = timeRange
		}
	}

	fun setOnAddedListener(onAddedListener: (Activity) -> Unit): DialogCompleteActivityInfo {
		this.onAddedListener = onAddedListener

		return this
	}

	private enum class ErrorType {
		EMPTY_PERIOD, EMPTY_TIME_RANGE
	}
}