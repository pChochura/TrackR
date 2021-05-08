package com.pointlessapps.trackr.utils

import android.content.res.Resources
import android.graphics.Point

object Utils {
	fun getScreenSize() =
		Point(
			Resources.getSystem().displayMetrics.widthPixels,
			Resources.getSystem().displayMetrics.heightPixels
		)
}