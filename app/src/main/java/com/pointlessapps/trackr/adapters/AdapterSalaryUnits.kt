package com.pointlessapps.trackr.adapters

import androidx.lifecycle.MutableLiveData
import androidx.viewbinding.ViewBinding
import com.pointlessapps.trackr.R
import com.pointlessapps.trackr.databinding.ItemSimpleBinding
import com.pointlessapps.trackr.models.Salary
import com.pointlessapps.trackr.utils.InflateMethod

class AdapterSalaryUnits(list: List<Salary.Unit>) :
	AdapterCore<Salary.Unit>(MutableLiveData(list), viewTypeContainer) {

	init {
		setHasStableIds(true)
	}

	companion object {
		val viewTypeContainer = object : AdapterCore.ViewTypeContainer() {
			override fun getInflateMethodByNumber(viewType: Int): InflateMethod<ViewBinding> =
				ItemSimpleBinding::inflate
		}
	}

	override fun onBind(binding: ViewBinding, item: Salary.Unit?) {
		if (binding !is ItemSimpleBinding || item == null) {
			return
		}

		binding.text.text = binding.text.context.getString(
			R.string.salary_unit,
			item.getName(binding.text.context),
			item.unit
		)
		binding.root.setOnClickListener { onClickListener?.invoke(item) }
	}
}