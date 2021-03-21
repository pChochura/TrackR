package com.pointlessapps.trackr.adapters

import androidx.lifecycle.MutableLiveData
import androidx.viewbinding.ViewBinding
import com.pointlessapps.trackr.databinding.ItemSimpleBinding
import com.pointlessapps.trackr.models.Salary
import com.pointlessapps.trackr.utils.InflateMethod

class AdapterSalaryTypes(list: List<Salary.Type>) :
	AdapterCore<Salary.Type>(MutableLiveData(list), viewTypeContainer) {

	init {
		setHasStableIds(true)
	}

	companion object {
		val viewTypeContainer = object : AdapterCore.ViewTypeContainer() {
			override fun getInflateMethodByNumber(viewType: Int): InflateMethod<ViewBinding> =
				ItemSimpleBinding::inflate
		}
	}

	override fun onBind(binding: ViewBinding, item: Salary.Type?) {
		if (binding !is ItemSimpleBinding || item == null) {
			return
		}

		binding.text.text = item.getName(binding.text.context)
		binding.root.setOnClickListener { onClickListener?.invoke(item) }
	}
}