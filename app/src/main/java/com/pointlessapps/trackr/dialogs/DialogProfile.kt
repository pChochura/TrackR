package com.pointlessapps.trackr.dialogs

import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.pointlessapps.trackr.R
import com.pointlessapps.trackr.databinding.DialogProfileBinding

class DialogProfile(
	private val activity: ComponentActivity,
	private val googleUser: LiveData<GoogleSignInAccount?>
) : DialogCore<DialogProfileBinding>(activity, DialogProfileBinding::inflate) {

	private var onLoginClickListener: (() -> Unit)? = null
	private var onLogoutClickListener: (() -> Unit)? = null

	override fun show(): DialogProfile {
		makeDialog { binding, _ ->
			binding.buttonAutomaticallySynchronize.clipToOutline = true

			googleUser.observe(activity) { user ->
				binding.containerSettings.isVisible = user != null
				binding.imageIcon.isVisible = user != null
				binding.buttonPrimary.setText(if (user != null) R.string.log_out else R.string.log_in_with_google)
				binding.buttonPrimary.icon = if (user == null) ContextCompat.getDrawable(
					activity,
					R.mipmap.ic_google
				) else null
				binding.textSubtitle.text =
					if (user != null) user.email else activity.getString(R.string.unlock_features_like_cloud_synchronization)
				binding.textTitle.text =
					if (user != null) user.displayName else activity.getString(R.string.log_in)
			}

			binding.buttonPrimary.setOnClickListener {
				if (googleUser.value == null) {
					onLoginClickListener?.invoke()
				} else {
					onLogoutClickListener?.invoke()
				}
			}
		}

		return this
	}

	fun setOnLoginClickListener(onLoginClickListener: () -> Unit): DialogProfile {
		this.onLoginClickListener = onLoginClickListener

		return this
	}

	fun setOnLogoutClickListener(onLogoutClickListener: () -> Unit): DialogProfile {
		this.onLogoutClickListener = onLogoutClickListener

		return this
	}
}