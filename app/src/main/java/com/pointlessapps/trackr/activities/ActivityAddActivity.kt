package com.pointlessapps.trackr.activities

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.color.MaterialColors
import com.pointlessapps.trackr.R
import com.pointlessapps.trackr.databinding.ActivityAddActivityBinding
import com.pointlessapps.trackr.dialogs.*
import com.pointlessapps.trackr.models.*
import com.pointlessapps.trackr.viewModels.ViewModelAddActivity
import java.io.Serializable

class ActivityAddActivity : AppCompatActivity() {

	companion object {
		const val KEY_DATA = "data"
	}

	private val viewModel by viewModels<ViewModelAddActivity>()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		val binding = ActivityAddActivityBinding.inflate(layoutInflater)
		setContentView(binding.root)
		setResult(RESULT_CANCELED)

		intent.getParcelableExtra<Activity?>(KEY_DATA)?.also { activity ->
			viewModel.setActivity(activity)
			binding.inputName.setText(activity.name)
		}

		viewModel.activity.observe(this) {
			prepareImageIcon(binding, it)
			prepareWeekdayAvailabilityComponent(binding, it)
			prepareSalaryComponent(binding, it)
			prepareTypeComponent(binding, it)
			preparePeriodComponent(binding, it)
			prepareTimeRangeComponent(binding, it)
		}

		binding.buttonSave.setOnClickListener {
			viewModel.updateName(binding.inputName.text.toString())
			val errors = viewModel.checkForErrors()
			if (errors != null) {
				displayErrors(binding, errors)

				return@setOnClickListener
			}

			setResult(
				RESULT_OK,
				Intent().putExtra(KEY_DATA, viewModel.activity.value)
			)
			finish()
		}
		binding.buttonBack.setOnClickListener { onBackPressed() }
	}

	private fun displayErrors(
		binding: ActivityAddActivityBinding,
		errors: List<ViewModelAddActivity.ErrorType>
	) {
		val defaultColor = ColorStateList.valueOf(
			MaterialColors.getColor(
				binding.inputName,
				R.attr.colorSecondaryVariant
			)
		)
		binding.inputName.backgroundTintList = defaultColor
		binding.buttonSalary.strokeColor = defaultColor
		binding.inputNameErrorLabel.isVisible = false
		binding.buttonSalaryErrorLabel.isVisible = false
		errors.forEach {
			when (it) {
				ViewModelAddActivity.ErrorType.BLANK_NAME -> {
					binding.inputName.backgroundTintList =
						ColorStateList.valueOf(getColor(R.color.text_red))
					binding.inputNameErrorLabel.isVisible = true
				}
				ViewModelAddActivity.ErrorType.ONE_TIME_WITH_TIME_BASED_SALARY -> {
					binding.buttonSalary.strokeColor =
						ColorStateList.valueOf(getColor(R.color.text_red))
					binding.buttonSalaryErrorLabel.isVisible = true
				}
			}
		}
	}

	private fun displayDiscardDialog(callback: (Boolean) -> Unit) {
		DialogMessage(
			this,
			R.string.discard_all_changes,
			R.string.discard_add_activity,
			R.string.confirm,
			R.string.cancel
		).show()
			.setOnConfirmClicked { callback(true) }
			.setOnCanceledClicked { callback(false) }
	}

	private fun prepareImageIcon(binding: ActivityAddActivityBinding, activity: Activity) {
		binding.imageIcon.clipToOutline = true
		binding.imageIcon.backgroundTintList = ColorStateList.valueOf(activity.color)
		binding.imageIcon.setImageResource(activity.icon)
		binding.imageIcon.setOnClickListener {
			DialogPickColorAndIcon(this, activity.color, activity.icon).show()
				.setOnSavedListener(viewModel::updateColorAndIcon)
		}
	}

	private fun prepareWeekdayAvailabilityComponent(
		binding: ActivityAddActivityBinding,
		activity: Activity
	) {
		binding.buttonWeekdayAvailability.text =
			activity.weekdayAvailability.getRanges(applicationContext)

		binding.buttonWeekdayAvailability.setOnClickListener {
			DialogPickWeekdayAvailability(this, activity.weekdayAvailability).show()
				.setOnSavedListener(viewModel::updateWeekdayAvailability)
		}
	}

	private fun prepareSalaryComponent(binding: ActivityAddActivityBinding, activity: Activity) {
		binding.buttonSalary.setOnClickListener {
			DialogSpecifySalary(this, activity.salary ?: Salary()).show()
				.setOnSavedListener(viewModel::updateSalary)
		}

		if (activity.salary == null) {
			binding.buttonSalary.setText(R.string.unset)

			return
		}

		val salary = activity.salary!!
		binding.buttonSalary.text = getString(
			R.string.salary_formatted,
			salary.amount,
			salary.unit,
			salary.type.getAbbreviation(applicationContext)
		)
	}

	private fun prepareTypeComponent(binding: ActivityAddActivityBinding, activity: Activity) {
		binding.buttonType.text = activity.type.getName(this)
		binding.buttonType.setOnClickListener {
			DialogPickActivityType(this, viewModel.activityTypes).show()
				.setOnPickedListener(viewModel::updateActivityType)
		}
	}

	private fun preparePeriodComponent(binding: ActivityAddActivityBinding, activity: Activity) {
		binding.containerPeriod.isVisible = activity.type is ActivityType.PeriodBased
		if (activity.type !is ActivityType.PeriodBased) {
			return
		}

		binding.buttonPeriod.setOnClickListener {
			DialogPickTime(
				this,
				(activity.type as ActivityType.PeriodBased).period ?: TimePeriod()
			).show().setOnPickedListener(viewModel::updateTimePeriod)
		}

		val period = (activity.type as ActivityType.PeriodBased).period
		if (period == null) {
			binding.buttonPeriod.setText(R.string.ask_me_every_time)

			return
		}

		binding.buttonPeriod.text =
			getString(R.string.period_formatted, period.hours, period.minutes)
	}

	private fun prepareTimeRangeComponent(binding: ActivityAddActivityBinding, activity: Activity) {
		binding.containerTimeRange.isVisible = activity.type is ActivityType.TimeBased
		if (activity.type !is ActivityType.TimeBased) {
			return
		}

		binding.buttonTimeRange.setOnClickListener {
			DialogPickTimeRange(
				this,
				(activity.type as ActivityType.TimeBased).range ?: TimeRange()
			).show().setOnPickedListener(viewModel::updateTimeRange)
		}

		val range = (activity.type as ActivityType.TimeBased).range
		if (range == null) {
			binding.buttonTimeRange.setText(R.string.ask_me_every_time)

			return
		}

		binding.buttonTimeRange.text = getString(
			R.string.time_range_formatted,
			range.startTime.hours,
			range.startTime.minutes,
			range.endTime.hours,
			range.endTime.minutes
		)
	}

	override fun onBackPressed() {
		displayDiscardDialog { discard ->
			if (discard) {
				super.onBackPressed()
			}
		}
	}
}