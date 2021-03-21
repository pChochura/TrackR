package com.pointlessapps.trackr.models

import android.content.Context
import android.os.Parcelable
import com.pointlessapps.trackr.R
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
class Income(private val amount: Float, private val unit: Salary.Unit) : Parcelable {

	fun toString(context: Context) = context.getString(R.string.income_formatted, amount, unit.unit)
}