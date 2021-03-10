package com.pointlessapps.trackr.adapters

import androidx.lifecycle.MutableLiveData
import com.pointlessapps.trackr.R
import com.pointlessapps.trackr.databinding.ItemSimpleBinding
import com.pointlessapps.trackr.models.Salary

class AdapterSalaryUnits(list: List<Salary.Unit>) :
	AdapterSimple<Salary.Unit, ItemSimpleBinding>(
		MutableLiveData(list),
		ItemSimpleBinding::inflate
	) {

	override fun onBind(root: ItemSimpleBinding, item: Salary.Unit) {
		root.text.text = root.text.context.getString(
			R.string.salary_unit,
			item.getName(root.text.context),
			item.unit
		)
		root.root.setOnClickListener { onClickListener?.invoke(item) }
	}
}