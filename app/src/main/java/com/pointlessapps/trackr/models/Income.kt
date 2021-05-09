package com.pointlessapps.trackr.models

import android.content.Context
import com.pointlessapps.trackr.R
import kotlinx.serialization.Serializable

@Serializable
class Income(val amount: Double, val unit: Salary.Unit) {

	fun toString(context: Context) = context.getString(R.string.income_formatted, amount, unit.unit)
}