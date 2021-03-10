package com.pointlessapps.trackr.adapters

import androidx.lifecycle.MutableLiveData
import com.pointlessapps.trackr.databinding.ItemActivityTypeBinding
import com.pointlessapps.trackr.models.ActivityType

class AdapterActivityTypes(list: List<ActivityType>) :
	AdapterSimple<ActivityType, ItemActivityTypeBinding>(
		MutableLiveData(list),
		ItemActivityTypeBinding::inflate
	) {

	override fun onBind(root: ItemActivityTypeBinding, item: ActivityType) {
		root.textTitle.text = item.getName(root.textTitle.context)
		root.textSubtitle.text = item.getDescription(root.textSubtitle.context)
		root.root.setOnClickListener { onClickListener?.invoke(item) }
	}
}