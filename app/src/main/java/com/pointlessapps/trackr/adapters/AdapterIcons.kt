package com.pointlessapps.trackr.adapters

import androidx.lifecycle.MutableLiveData
import androidx.viewbinding.ViewBinding
import com.pointlessapps.trackr.databinding.ItemIconBinding
import com.pointlessapps.trackr.utils.InflateMethod

class AdapterIcons(list: List<Int>) :
	AdapterCore<Int>(MutableLiveData(list), viewTypeContainer) {

	init {
		setHasStableIds(true)
	}

	companion object {
		val viewTypeContainer = object : AdapterCore.ViewTypeContainer() {
			override fun getInflateMethodByNumber(viewType: Int): InflateMethod<ViewBinding> =
				ItemIconBinding::inflate
		}
	}

	override fun onBind(binding: ViewBinding, item: Int?) {
		if (binding !is ItemIconBinding || item == null) {
			return
		}

		binding.root.setImageResource(item)
		binding.root.setOnClickListener {
			onClickListener?.invoke(item)
		}
	}
}