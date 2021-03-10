package com.pointlessapps.trackr.adapters

import androidx.lifecycle.LiveData
import com.pointlessapps.trackr.databinding.ItemActivityCardSquareBinding
import com.pointlessapps.trackr.models.Activity

class AdapterFavourites(list: LiveData<List<Activity>>) :
	AdapterSimple<Activity, ItemActivityCardSquareBinding>(
		list,
		ItemActivityCardSquareBinding::inflate
	) {

	override fun onBind(root: ItemActivityCardSquareBinding, item: Activity) {
		root.root.clipToOutline = true
		root.textTitle.text = item.name
		root.textSubtitle.text = "One time" // TODO add subtitle
		root.imageIcon.setImageResource(item.icon)

		with(item.color) {
			root.textTitle.setTextColor(this)
			root.textSubtitle.setTextColor(this)
			root.imageIcon.setColorFilter(this)
			root.buttonMore.setColorFilter(this)
		}
	}
}