package com.pointlessapps.trackr.dialogs

import android.app.Activity
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import com.pointlessapps.trackr.R
import com.pointlessapps.trackr.databinding.DialogMessageBinding

class DialogMessage(
	activity: Activity,
	@StringRes private val titleRes: Int,
	@StringRes private val contentRes: Int,
	@StringRes private val confirmButtonRes: Int = R.string.confirm,
	@StringRes private val cancelButtonRes: Int? = null,
) : DialogCore<DialogMessageBinding>(activity, DialogMessageBinding::inflate) {

	private var onConfirmListener: (() -> Unit)? = null
	private var onCancelListener: (() -> Unit)? = null

	override fun show(): DialogMessage {
		makeDialog { binding, dialog ->
			binding.textTitle.setText(titleRes)
			binding.textContent.setText(contentRes)
			binding.buttonConfirm.setText(confirmButtonRes)
			cancelButtonRes?.let {
				binding.buttonCancel.setText(it)
				binding.buttonCancel.isVisible = true
			}
			binding.buttonCancel.setOnClickListener {
				onCancelListener?.invoke()
				dialog.dismiss()
			}
			binding.buttonConfirm.setOnClickListener {
				onConfirmListener?.invoke()
				dialog.dismiss()
			}
		}

		return this
	}

	fun setOnConfirmClicked(onConfirmListener: () -> Unit): DialogMessage {
		this.onConfirmListener = onConfirmListener

		return this
	}

	fun setOnCanceledClicked(onCancelListener: () -> Unit): DialogMessage {
		this.onCancelListener = onCancelListener

		return this
	}
}