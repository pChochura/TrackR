package com.pointlessapps.trackr.adapters

import androidx.lifecycle.MutableLiveData
import androidx.viewbinding.ViewBinding
import com.pointlessapps.trackr.databinding.ItemActivityTypeBinding
import com.pointlessapps.trackr.models.ActivityType
import com.pointlessapps.trackr.utils.InflateMethod

class AdapterActivityTypes(list: List<ActivityType>) :
	AdapterCore<ActivityType>(MutableLiveData(list), viewTypeContainer) {

	init {
		setHasStableIds(true)
	}

	companion object {
		val viewTypeContainer = object : AdapterCore.ViewTypeContainer() {
			override fun getInflateMethodByNumber(viewType: Int): InflateMethod<ViewBinding> =
				ItemActivityTypeBinding::inflate
		}
	}

	override fun onBind(binding: ViewBinding, item: ActivityType?) {
		if (binding !is ItemActivityTypeBinding || item == null) {
			return
		}

		binding.textTitle.text = item.getName(binding.textTitle.context)
		binding.textSubtitle.text = item.getDescription(binding.textSubtitle.context)
		binding.root.setOnClickListener { onClickListener?.invoke(item) }
	}
}