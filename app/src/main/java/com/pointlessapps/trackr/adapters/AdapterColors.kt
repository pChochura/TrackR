package com.pointlessapps.trackr.adapters

import androidx.lifecycle.MutableLiveData
import com.pointlessapps.trackr.databinding.ItemColorBinding

class AdapterColors(list: List<Int>) :
	AdapterSimple<Int, ItemColorBinding>(MutableLiveData(list), ItemColorBinding::inflate) {

	override fun onBind(root: ItemColorBinding, item: Int) {
		root.root.setColorFilter(item)
		root.root.setOnClickListener {
			onClickListener?.invoke(item)
		}
	}
}