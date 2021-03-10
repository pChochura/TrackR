package com.pointlessapps.trackr.adapters

import androidx.core.view.isGone
import androidx.lifecycle.LiveData
import com.pointlessapps.trackr.databinding.ItemActivityCardSquareBinding
import com.pointlessapps.trackr.models.Activity
import com.pointlessapps.trackr.models.ActivityType

class AdapterFavourites(list: LiveData<List<Activity>>) :
	AdapterSimple<Activity, ItemActivityCardSquareBinding>(
		list,
		ItemActivityCardSquareBinding::inflate
	) {

	var onMoreClickListener: ((Activity) -> Unit)? = null

	override fun onBind(root: ItemActivityCardSquareBinding, item: Activity) {
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