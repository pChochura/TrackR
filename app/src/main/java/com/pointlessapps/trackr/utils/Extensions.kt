package com.pointlessapps.trackr.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.util.TypedValue
import com.pointlessapps.trackr.models.Event
import com.pointlessapps.trackr.services.ServiceActivityCalculator
import java.util.*

fun Int.toDp() = this.toFloat().toDp().toInt()

fun Float.toDp() =
	TypedValue.applyDimension(
		TypedValue.COMPLEX_UNIT_DIP,
		this,
		Resources.getSystem().displayMetrics
	)

fun Int.toSp() = this.toFloat().toSp().toInt()

fun Float.toSp() =
	TypedValue.applyDimension(
		TypedValue.COMPLEX_UNIT_SP,
		this,
		Resources.getSystem().displayMetrics
	)

fun Context.isNightMode() =
	resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK != Configuration.UI_MODE_NIGHT_NO

fun Event.getIncome() = ServiceActivityCalculator.calculateIncomeForEvent(this)

fun Calendar.setToBeginMonth(monthOffset: Int = 0): Calendar {
	set(Calendar.DAY_OF_MONTH, 1)
	set(Calendar.HOUR, 0)
	set(Calendar.MINUTE, 0)
	set(Calendar.SECOND, 0)
	set(Calendar.MILLISECOND, 0)
	add(Calendar.MONTH, monthOffset)

	return this
}

fun Calendar.setToEndMonth(monthOffset: Int = 0): Calendar {
	add(Calendar.MONTH, monthOffset)
	set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
	set(Calendar.HOUR_OF_DAY, 23)
	set(Calendar.MINUTE, 59)
	set(Calendar.SECOND, 59)
	set(Calendar.MILLISECOND, 999)

	return this
}

inline fun <reified T : Number> T.ifZero(number: T): T = if (this.toDouble() == 0.0) number else this