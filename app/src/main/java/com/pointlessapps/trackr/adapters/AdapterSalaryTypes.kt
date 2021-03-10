package com.pointlessapps.trackr.adapters

import androidx.lifecycle.MutableLiveData
import com.pointlessapps.trackr.databinding.ItemSimpleBinding
import com.pointlessapps.trackr.models.Salary

class AdapterSalaryTypes(list: List<Salary.Type>) :
	AdapterSimple<Salary.Type, ItemSimpleBinding>(
		MutableLiveData(list),
		ItemSimpleBinding::inflate
	) {

	override fun onBind(root: ItemSimpleBinding, item: Salary.Type) {
		root.text.text = item.getName(root.text.context)
		root.root.setOnClickListener { onClickListener?.invoke(item) }
	}
}