package com.pointlessapps.trackr.dialogs

import androidx.activity.ComponentActivity
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import com.pointlessapps.trackr.R
import com.pointlessapps.trackr.databinding.DialogPickTimeRangeBinding
import com.pointlessapps.trackr.models.TimePeriod
import com.pointlessapps.trackr.models.TimeRange

class DialogPickTimeRange(
	private val activity: ComponentActivity,
	timeRange: TimeRange,
	private val clearable: Boolean = true
) : DialogCore<DialogPickTimeRangeBinding>(activity, DialogPickTimeRangeBinding::inflate) {

	private val timeRange = MutableLiveData(TimeRange(timeRange))
	private var onPickedListener: ((TimeRange?) -> Unit)? = null

	override fun show(): DialogPickTimeRange {
		makeDialog { binding, dialog ->
			binding.buttonClear.isVisible = clearable
			binding.buttonClear.setOnClickListener {
				onPickedListener?.invoke(null)
				dialog.dismiss()
			}
			binding.buttonSave.setOnClickListener {
				onPickedListener?.invoke(timeRange.value!!)
				dialog.dismiss()
			}

			timeRange.observe(activity) {
				binding.buttonStartTime.text =
					activity.getString(
						R.string.time_formatted,
						it.startTime.hours,
						it.startTime.minutes
					)
				binding.buttonEndTime.text =
					activity.getString(
						R.string.time_formatted,
						it.endTime.hours,
						it.endTime.minutes
					)
			}

			binding.buttonStartTime.setOnClickListener {
				DialogPickTime(activity, timeRange.value!!.startTime, clearable = false).show()
					.setOnPickedListener(::updateStartTime)
			}
			binding.buttonEndTime.setOnClickListener {
				DialogPickTime(activity, timeRange.value!!.endTime, clearable = false).show()
					.setOnPickedListener(::updateEndTime)
			}
		}

		return this
	}

	private fun updateStartTime(startTime: TimePeriod?) {
		timeRange.value = timeRange.value?.also {
			it.startTime = startTime!!
		}
	}

	private fun updateEndTime(endTime: TimePeriod?) {
		timeRange.value = timeRange.value?.also {
			it.endTime = endTime!!
		}
	}

	fun setOnPickedListener(onPickedListener: (TimeRange?) -> Unit): DialogPickTimeRange {
		this.onPickedListener = onPickedListener

		return this
	}
}