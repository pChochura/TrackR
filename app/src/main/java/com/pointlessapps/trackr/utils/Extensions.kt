package com.pointlessapps.trackr.utils

import android.content.res.Resources
import android.util.TypedValue

fun Int.toDp() = this.toFloat().toDp().toInt()

fun Float.toDp() =
	TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, Resources.getSystem().displayMetrics)