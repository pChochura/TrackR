package com.pointlessapps.trackr.adapters

import androidx.core.view.isGone
import androidx.lifecycle.LiveData
import com.pointlessapps.trackr.databinding.ItemActivityCardRectangleAdderBinding
import com.pointlessapps.trackr.databinding.ItemActivityCardRectangleBinding
import com.pointlessapps.trackr.models.Activity
import com.pointlessapps.trackr.models.ActivityType

class AdapterAllActivities(list: LiveData<List<Activity>>) :
	AdapterCore<Activity, ItemActivityCardRectangleBinding, ItemActivityCardRectangleAdderBinding>(
		list,
		ItemActivityCardRectangleBinding::inflate,
		ItemActivityCardRectangleAdderBinding::inflate,
	) {

	var onMoreClickListener: ((Activity) -> Unit)? = null

	override fun onBindAdder(root: ItemActivityCardRectangleAdderBinding) {
		root.root.clipToOutline = true
		root.containerClickable.setOnClickListener {
			onAddClickListener?.invoke()
		}
	}

	override fun onBind(root: ItemActivityCardRectangleBinding, item: Activity) {
		root.containerClickable.setOnClickListener { onClickListener?.invoke(item) }
		root.buttonMore.setOnClickListener { onMoreClickListener?.invoke(item) }

		root.buttonMore.isGone = item.type is ActivityType.OneTime && item.salary == null

		root.root.clipToOutline = true
		root.textTitle.text = item.name
		root.textSubtitle.text = item.type.getSubtitle(root.textSubtitle.context)
		root.imageIcon.setImageResource(item.icon)

		with(item.color) {
			root.textTitle.setTextColor(this)
			root.textSubtitle.setTextColor(this)
			root.imageIcon.setColorFilter(this)
			root.buttonMore.setColorFilter(this)
		}
	}
}