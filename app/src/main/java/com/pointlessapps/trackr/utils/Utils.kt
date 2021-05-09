package com.pointlessapps.trackr.utils

import android.content.Context
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.Point
import android.os.Build
import androidx.core.content.res.ResourcesCompat

object Utils {
	fun getScreenSize() =
		Point(
			Resources.getSystem().displayMetrics.widthPixels,
			Resources.getSystem().displayMetrics.heightPixels
		)

	fun TypedArray.getFont(context: Context, index: Int, defaultFont: Int) = when {
		Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> getFont(index)
		else -> ResourcesCompat.getFont(context, getResourceId(index, defaultFont))
	}
}