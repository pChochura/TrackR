package com.pointlessapps.trackr.dialogs

import androidx.activity.ComponentActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.MutableLiveData
import com.pointlessapps.trackr.databinding.DialogSpecifySalaryBinding
import com.pointlessapps.trackr.models.Salary

class DialogSpecifySalary(
	private val activity: ComponentActivity,
	salary: Salary,
	private val clearable: Boolean = true
) : DialogCore<DialogSpecifySalaryBinding>(activity, DialogSpecifySalaryBinding::inflate) {

	private val salary = MutableLiveData(Salary(salary))
	private var onSavedListener: ((Salary?) -> Unit)? = null

	override fun show(): DialogSpecifySalary {
		makeDialog { binding, dialog ->
			binding.buttonClear.isVisible = clearable
			binding.buttonClear.setOnClickListener {
				onSavedListener?.invoke(null)
				dialog.dismiss()
			}
			binding.buttonSave.setOnClickListener {
				onSavedListener?.invoke(salary.value)
				dialog.dismiss()
			}

			binding.inputAmount.addTextChangedListener {
				updateSalaryAmount(it.toString().toDoubleOrNull() ?: 0.0)
			}

			binding.inputAmount.setText(salary.value!!.amount.toString())
			salary.observe(activity) {
				binding.buttonUnit.text = it.unit.unit
				binding.buttonType.text = it.type.getName(activity)
			}

			binding.buttonUnit.setOnClickListener {
				DialogPickSalaryUnit(activity).show()
					.setOnPickedListener(::updateSalaryUnit)
			}
			binding.buttonType.setOnClickListener {
				DialogPickSalaryType(activity).show()
					.setOnPickedListener(::updateSalaryType)
			}
		}

		return this
	}

	private fun updateSalaryAmount(amount: Double) {
		salary.value = salary.value?.also {
			it.amount = amount
		}
	}

	private fun updateSalaryUnit(unit: Salary.Unit) {
		salary.value = salary.value?.also {
			it.unit = unit
		}
	}

	private fun updateSalaryType(type: Salary.Type) {
		salary.value = salary.value?.also {
			it.type = type
		}
	}

	fun setOnSavedListener(onSavedListener: (Salary?) -> Unit): DialogSpecifySalary {
		this.onSavedListener = onSavedListener

		return this
	}
}