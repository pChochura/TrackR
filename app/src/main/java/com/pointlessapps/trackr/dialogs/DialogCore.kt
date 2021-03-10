package com.pointlessapps.trackr.dialogs

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.viewbinding.ViewBinding
import com.pointlessapps.trackr.R
import com.pointlessapps.trackr.utils.InflateMethod
import com.pointlessapps.trackr.utils.Utils
import com.pointlessapps.trackr.utils.toDp
import kotlin.math.min

abstract class DialogCore<Binding : ViewBinding>(
	private val activity: Activity,
	private val inflateMethod: InflateMethod<Binding>,
	private val windowSize: IntArray = intArrayOf(
		UNDEFINED_WINDOW_SIZE,
		ViewGroup.LayoutParams.WRAP_CONTENT
	)
) {

	companion object {
		const val UNDEFINED_WINDOW_SIZE = Integer.MAX_VALUE
	}

	abstract fun show(): DialogCore<Binding>

	protected fun makeDialog(callback: (Binding, Dialog) -> Unit) {
		val dialog = Dialog(activity)
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
		dialog.window?.also {
			it.setWindowAnimations(R.style.DialogAnimations)
			it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
			it.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
			it.attributes?.dimAmount = 0.5f
		}
		val size = Utils.getScreenSize()
		val width = min(
			if (windowSize.isNotEmpty() && windowSize.first() != UNDEFINED_WINDOW_SIZE) windowSize[0] else 350.toDp(),
			size.x - 150
		)
		val height = min(
			if (windowSize.size > 1 && windowSize[1] != UNDEFINED_WINDOW_SIZE) windowSize[1] else 500.toDp(),
			size.y - 150
		)

		val binding = inflateMethod(LayoutInflater.from(activity), null, false)

		dialog.setContentView(
			binding.root,
			ViewGroup.LayoutParams(width, height)
		)
		callback.invoke(binding, dialog)
		if (!dialog.isShowing)
			dialog.show()
	}
}
