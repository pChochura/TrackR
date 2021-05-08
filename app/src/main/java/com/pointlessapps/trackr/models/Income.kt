package com.pointlessapps.trackr.models

import android.content.Context
import com.pointlessapps.trackr.R
import kotlinx.serialization.Serializable

@Serializable
class Income(private val amount: Float, private val unit: Salary.Unit) {

	fun toString(context: Context) = context.getString(R.string.income_formatted, amount, unit.unit)
}