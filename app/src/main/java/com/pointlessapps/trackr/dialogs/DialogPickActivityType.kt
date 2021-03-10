package com.pointlessapps.trackr.dialogs

import android.app.Activity
import com.pointlessapps.trackr.R
import com.pointlessapps.trackr.adapters.AdapterActivityTypes
import com.pointlessapps.trackr.databinding.DialogPickFromListBinding
import com.pointlessapps.trackr.models.ActivityType

class DialogPickActivityType(activity: Activity, private val activityTypes: List<ActivityType>) :
	DialogCore<DialogPickFromListBinding>(activity, DialogPickFromListBinding::inflate) {

	private var onPickedListener: ((ActivityType) -> Unit)? = null

	override fun show(): DialogPickActivityType {
		makeDialog { binding, dialog ->
			binding.textTitle.setText(R.string.select_activity_type)
			binding.buttonCancel.setOnClickListener { dialog.dismiss() }
			with(binding.listItems) {
				adapter = AdapterActivityTypes(activityTypes).apply {
					onClickListener = {
						onPickedListener?.invoke(it)
						dialog.dismiss()
					}
				}
			}
		}

		return this
	}

	fun setOnPickedListener(onPickedListener: (ActivityType) -> Unit): DialogPickActivityType {
		this.onPickedListener = onPickedListener

		return this
	}
}