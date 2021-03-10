package com.pointlessapps.trackr.dialogs

import androidx.activity.ComponentActivity
import androidx.appcompat.widget.AppCompatToggleButton
import androidx.lifecycle.MutableLiveData
import com.pointlessapps.trackr.databinding.DialogPickWeekdayAvailabilityBinding
import com.pointlessapps.trackr.models.WeekdayAvailability

class DialogPickWeekdayAvailability(
	private val activity: ComponentActivity,
	weekdayAvailability: WeekdayAvailability
) : DialogCore<DialogPickWeekdayAvailabilityBinding>(
	activity,
	DialogPickWeekdayAvailabilityBinding::inflate
) {

	private val weekdayAvailability = MutableLiveData(weekdayAvailability)
	private var onSavedListener: ((WeekdayAvailability) -> Unit)? = null

	override fun show(): DialogPickWeekdayAvailability {
		makeDialog { binding, dialog ->
			binding.buttonSave.setOnClickListener {
				onSavedListener?.invoke(weekdayAvailability.value!!)
				dialog.dismiss()
			}

			prepareWeekdayButtons(binding)
			weekdayAvailability.observe(activity) {
				onWeekdayButtons(binding) { index, view ->
					view.isChecked = it.getAt(index)
				}
			}
		}

		return this
	}

	private fun prepareWeekdayButtons(binding: DialogPickWeekdayAvailabilityBinding) {
		onWeekdayButtons(binding) { index, view ->
			view.clipToOutline = true
			view.setOnClickListener {
				updateWeekdayAvailabilityAt(index, !weekdayAvailability.value!!.getAt(index))
			}
		}
	}

	private fun updateWeekdayAvailabilityAt(index: Int, value: Boolean) {
		weekdayAvailability.value = weekdayAvailability.value?.also {
			it.setAt(index, value)
		}
	}

	private fun onWeekdayButtons(
		binding: DialogPickWeekdayAvailabilityBinding,
		callback: (Int, AppCompatToggleButton) -> Unit
	) {
		val context = binding.root.context
		repeat(weekdayAvailability.value!!.getAll().size) { index ->
			val id =
				context.resources.getIdentifier("buttonWeekday$index", "id", context.packageName)
			callback(index, binding.root.findViewById(id))
		}
	}

	fun setOnSavedListener(onSavedListener: (WeekdayAvailability) -> Unit): DialogPickWeekdayAvailability {
		this.onSavedListener = onSavedListener

		return this
	}
}