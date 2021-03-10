package com.pointlessapps.trackr.dialogs

import android.app.Activity
import com.pointlessapps.trackr.R
import com.pointlessapps.trackr.adapters.AdapterSalaryTypes
import com.pointlessapps.trackr.databinding.DialogPickFromListBinding
import com.pointlessapps.trackr.models.Salary

class DialogPickSalaryType(activity: Activity) :
	DialogCore<DialogPickFromListBinding>(activity, DialogPickFromListBinding::inflate) {

	private val salaryTypes = Salary.Type.values().toList()
	private var onPickedListener: ((Salary.Type) -> Unit)? = null

	override fun show(): DialogPickSalaryType {
		makeDialog { binding, dialog ->
			binding.textTitle.setText(R.string.select_salary_type)
			binding.buttonCancel.setOnClickListener { dialog.dismiss() }
			with(binding.listItems) {
				adapter = AdapterSalaryTypes(salaryTypes).apply {
					onClickListener = {
						onPickedListener?.invoke(it)
						dialog.dismiss()
					}
				}
			}
		}

		return this
	}

	fun setOnPickedListener(onPickedListener: (Salary.Type) -> Unit): DialogPickSalaryType {
		this.onPickedListener = onPickedListener

		return this
	}
}