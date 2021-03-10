package com.pointlessapps.trackr.models

import android.content.Context
import android.os.Parcelable
import androidx.annotation.StringRes
import com.pointlessapps.trackr.R
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class ActivityType(
	@StringRes private val nameResId: Int = 0,
	@StringRes private val descriptionResId: Int = 0
) : Parcelable {

	@Parcelize
	@Serializable
	@SerialName("OneTime")
	class OneTime : ActivityType(R.string.one_time, R.string.one_time_description)

	@Parcelize
	@Serializable
	@SerialName("PeriodBased")
	class PeriodBased(var period: TimePeriod? = null) :
		ActivityType(R.string.period_based, R.string.period_based_description)

	@Parcelize
	@Serializable
	@SerialName("TimeBased")
	class TimeBased(var range: TimeRange? = null) :
		ActivityType(R.string.time_based, R.string.time_based_description)

	fun getName(context: Context) = context.getString(nameResId)
	fun getDescription(context: Context) = context.getString(descriptionResId)
}
