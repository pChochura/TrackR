package com.pointlessapps.trackr.dialogs

import android.app.Activity
import androidx.core.view.isVisible
import com.pointlessapps.trackr.databinding.DialogPickDateBinding
import java.util.*

class DialogPickDate(
	activity: Activity,
	private val date: Date,
	private val clearable: Boolean = true
) : DialogCore<DialogPickDateBinding>(activity, DialogPickDateBinding::inflate) {

	private var onPickedListener: ((Date?) -> Unit)? = null

	override fun show(): DialogPickDate {
		makeDialog { binding, dialog ->
			val calendar = Calendar.getInstance().apply {
				timeInMillis = date.time
			}
			binding.datePicker.init(
				calendar.get(Calendar.YEAR),
				calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH)
			) { _, year, month, day ->
				calendar.set(Calendar.YEAR, year)
				calendar.set(Calendar.MONTH, month)
				calendar.set(Calendar.DAY_OF_MONTH, day)
			}
			binding.datePicker.firstDayOfWeek = Calendar.MONDAY
			binding.buttonClear.isVisible = clearable
			binding.buttonClear.setOnClickListener {
				onPickedListener?.invoke(null)
				dialog.dismiss()
			}
			binding.buttonSave.setOnClickListener {
				binding.datePicker.month
				onPickedListener?.invoke(calendar.time)
				dialog.dismiss()
			}
		}

		return this
	}

	fun setOnPickedListener(onPickedListener: (Date?) -> Unit): DialogPickDate {
		this.onPickedListener = onPickedListener

		return this
	}
}