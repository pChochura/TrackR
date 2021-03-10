package com.pointlessapps.trackr.dialogs

import android.app.Activity
import androidx.core.view.isVisible
import com.pointlessapps.trackr.databinding.DialogPickTimeBinding
import com.pointlessapps.trackr.models.TimePeriod

class DialogPickTime(
	activity: Activity,
	private val timePeriod: TimePeriod,
	private val clearable: Boolean = true
) : DialogCore<DialogPickTimeBinding>(activity, DialogPickTimeBinding::inflate) {

	private var onPickedListener: ((TimePeriod?) -> Unit)? = null

	override fun show(): DialogPickTime {
		makeDialog { binding, dialog ->
			binding.timePicker.setIs24HourView(true)
			binding.timePicker.hour = timePeriod.hours
			binding.timePicker.minute = timePeriod.minutes
			binding.buttonClear.isVisible = clearable
			binding.buttonClear.setOnClickListener {
				onPickedListener?.invoke(null)
				dialog.dismiss()
			}
			binding.buttonSave.setOnClickListener {
				onPickedListener?.invoke(
					TimePeriod(
						binding.timePicker.hour,
						binding.timePicker.minute
					)
				)
				dialog.dismiss()
			}
		}

		return this
	}

	fun setOnPickedListener(onPickedListener: (TimePeriod?) -> Unit): DialogPickTime {
		this.onPickedListener = onPickedListener

		return this
	}
}