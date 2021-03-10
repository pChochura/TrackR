package com.pointlessapps.trackr.adapters

import androidx.lifecycle.MutableLiveData
import com.pointlessapps.trackr.databinding.ItemIconBinding

class AdapterIcons(list: List<Int>) :
	AdapterSimple<Int, ItemIconBinding>(MutableLiveData(list), ItemIconBinding::inflate) {

	override fun onBind(root: ItemIconBinding, item: Int) {
		root.root.setImageResource(item)
		root.root.setOnClickListener {
			onClickListener?.invoke(item)
		}
	}
}