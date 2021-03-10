package com.pointlessapps.trackr.dialogs

import android.app.Activity
import com.pointlessapps.trackr.databinding.DialogProfileBinding

class DialogProfile(activity: Activity) :
	DialogCore<DialogProfileBinding>(activity, DialogProfileBinding::inflate) {

	private var onLogoutClickListener: (() -> Unit)? = null

	override fun show(): DialogProfile {
		makeDialog { binding, dialog ->

		}

		return this
	}

	fun setOnLogoutClickListener(onLogoutClickListener: () -> Unit): DialogProfile {
		this.onLogoutClickListener = onLogoutClickListener

		return this
	}
}