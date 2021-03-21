package com.pointlessapps.trackr.dialogs

import android.app.Activity
import com.google.android.material.color.MaterialColors
import com.pointlessapps.trackr.R
import com.pointlessapps.trackr.adapters.AdapterSelectFavourites
import com.pointlessapps.trackr.databinding.DialogPickFromListBinding

class DialogSelectFavourites(
	activity: Activity,
	activities: List<com.pointlessapps.trackr.models.Activity>
) : DialogCore<DialogPickFromListBinding>(activity, DialogPickFromListBinding::inflate) {

	private var onSavedListener: ((Map<String, Boolean>) -> Unit)? = null
	private val favourites = activities.associateWith { false }.toMutableMap()

	override fun show(): DialogSelectFavourites {
		makeDialog { binding, dialog ->
			binding.textTitle.setText(R.string.select_activity_type)
			binding.buttonCancel.setText(R.string.save)
			binding.buttonCancel.setTextColor(
				MaterialColors.getColor(
					binding.buttonCancel,
					android.R.attr.textColorPrimary
				)
			)
			binding.buttonCancel.setOnClickListener {
				onSavedListener?.invoke(favourites.mapKeys { it.key.id })
				dialog.dismiss()
			}
			with(binding.listItems) {
				adapter = AdapterSelectFavourites(favourites).apply {
					onItemSelected = { activity, selected ->
						favourites[activity] = selected
						notifyDataSetChanged()
					}
				}
			}
		}

		return this
	}

	fun setOnPickedListener(onSavedListener: (Map<String, Boolean>) -> Unit): DialogSelectFavourites {
		this.onSavedListener = onSavedListener

		return this
	}
}