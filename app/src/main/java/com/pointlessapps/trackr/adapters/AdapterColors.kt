package com.pointlessapps.trackr.adapters

import androidx.lifecycle.MutableLiveData
import androidx.viewbinding.ViewBinding
import com.pointlessapps.trackr.databinding.ItemColorBinding
import com.pointlessapps.trackr.utils.InflateMethod

class AdapterColors(list: List<Int>) :
	AdapterCore<Int>(MutableLiveData(list), viewTypeContainer) {

	init {
		setHasStableIds(true)
	}

	companion object {
		val viewTypeContainer = object : AdapterCore.ViewTypeContainer() {
			override fun getInflateMethodByNumber(viewType: Int): InflateMethod<ViewBinding> =
				ItemColorBinding::inflate
		}
	}

	override fun onBind(binding: ViewBinding, item: Int?) {
		if (binding !is ItemColorBinding || item == null) {
			return
		}

		binding.root.setColorFilter(item)
		binding.root.setOnClickListener {
			onClickListener?.invoke(item)
		}
	}
}