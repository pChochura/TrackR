package com.pointlessapps.trackr.dialogs

import android.app.Activity
import com.pointlessapps.trackr.R
import com.pointlessapps.trackr.adapters.AdapterSalaryUnits
import com.pointlessapps.trackr.databinding.DialogPickFromListBinding
import com.pointlessapps.trackr.models.Salary

class DialogPickSalaryUnit(activity: Activity) :
	DialogCore<DialogPickFromListBinding>(activity, DialogPickFromListBinding::inflate) {

	private val salaryUnits = Salary.Unit.values().toList()
	private var onPickedListener: ((Salary.Unit) -> Unit)? = null

	override fun show(): DialogPickSalaryUnit {
		makeDialog { binding, dialog ->
			binding.textTitle.setText(R.string.select_salary_unit)
			binding.buttonCancel.setOnClickListener { dialog.dismiss() }
			with(binding.listItems) {
				adapter = AdapterSalaryUnits(salaryUnits).apply {
					onClickListener = {
						onPickedListener?.invoke(it)
						dialog.dismiss()
					}
				}
			}
		}

		return this
	}

	fun setOnPickedListener(onPickedListener: (Salary.Unit) -> Unit): DialogPickSalaryUnit {
		this.onPickedListener = onPickedListener

		return this
	}
}