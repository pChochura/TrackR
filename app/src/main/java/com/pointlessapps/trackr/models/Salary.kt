package com.pointlessapps.trackr.models

import android.content.Context
import android.os.Parcelable
import androidx.annotation.StringRes
import com.pointlessapps.trackr.R
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
class Salary(var amount: Float = 0f, var unit: Unit = Unit.DOLLAR, var type: Type = Type.PER_HOUR) :
	Parcelable {
	constructor(salary: Salary) : this(salary.amount, salary.unit, salary.type)

	enum class Unit(@StringRes val nameResId: Int, val unit: String) {
		DOLLAR(R.string.dollar, "$"),
		EURO(R.string.euro, "€"),
		PLN(R.string.pln, "zł"),
		POUND(R.string.pound, "£");

		fun getName(context: Context) = context.getString(nameResId)
		override fun toString() = unit
	}

	enum class Type(@StringRes val nameResId: Int, @StringRes val abbrevResId: Int) {
		PER_HOUR(R.string.per_hour, R.string.per_hour_abbrev),
//		PER_DAY(R.string.per_day, R.string.per_day_abbrev),
//		PER_WEEK(R.string.per_week, R.string.per_week_abbrev),
		PER_MONTH(R.string.per_month, R.string.per_month_abbrev),
		PER_OCCURRENCE(R.string.per_occurrence, R.string.per_occurrence_abbrev);

		fun getName(context: Context) = context.getString(nameResId)
		fun getAbbreviation(context: Context) = context.getString(abbrevResId)
	}
}