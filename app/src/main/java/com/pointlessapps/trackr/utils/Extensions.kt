package com.pointlessapps.trackr.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.util.TypedValue
import com.pointlessapps.trackr.models.Event
import com.pointlessapps.trackr.services.ServiceIncomeCalculator

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

fun Event.getIncome() = ServiceIncomeCalculator.calculateIncomeForEvent(this)