package com.pointlessapps.trackr.dialogs

import android.app.Activity
import androidx.core.view.isVisible
import com.pointlessapps.trackr.R
import com.pointlessapps.trackr.databinding.DialogShowEventBinding
import com.pointlessapps.trackr.models.ActivityType
import com.pointlessapps.trackr.models.Event
import com.pointlessapps.trackr.utils.getIncome

class DialogShowEvent(private val activity: Activity, private val event: Event) :
	DialogCore<DialogShowEventBinding>(activity, DialogShowEventBinding::inflate) {

	private var onDeleteListener: ((Event) -> Unit)? = null

	override fun show(): DialogShowEvent {
		makeDialog { binding, dialog ->
			binding.textTitle.text = event.activity.name

			val income = event.getIncome()
			binding.containerIncome.isVisible = income != null
			binding.textIncome.text = income?.toString(activity)

			binding.containerPeriod.isVisible = event.activity.type is ActivityType.PeriodBased
			binding.textPeriod.text = event.activity.type.getSubtitle(activity)

			binding.containerTimeRange.isVisible = event.activity.type is ActivityType.TimeBased
			binding.textTimeRange.text = event.activity.type.getSubtitle(activity)

			binding.textDate.text = activity.getString(R.string.date_formatted, event.date)

			binding.buttonDelete.setOnClickListener {
				onDeleteListener?.invoke(event)
				dialog.dismiss()
			}
			binding.buttonOk.setOnClickListener { dialog.dismiss() }
		}

		return this
	}

	fun setOnDeleteListener(onDeleteListener: (Event) -> Unit): DialogShowEvent {
		this.onDeleteListener = onDeleteListener

		return this
	}
}