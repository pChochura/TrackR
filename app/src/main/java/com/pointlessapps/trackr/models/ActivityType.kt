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

	companion object {
		fun copy(activityType: ActivityType) = when (activityType) {
			is OneTime -> OneTime()
			is PeriodBased -> PeriodBased(activityType.period?.let { TimePeriod(it) })
			is TimeBased -> TimeBased(activityType.range?.let { TimeRange(it) })
		}
	}

	@Serializable
	@Parcelize
	@SerialName("OneTime")
	class OneTime : ActivityType(R.string.one_time, R.string.one_time_description) {
		override fun isComplete() = true
		override fun getSubtitle(context: Context) = context.getString(R.string.one_time)
	}

	@Serializable
	@Parcelize
	@SerialName("PeriodBased")
	class PeriodBased(var period: TimePeriod? = null) :
		ActivityType(R.string.period_based, R.string.period_based_description) {
		override fun isComplete() = period != null
		override fun getSubtitle(context: Context) = when (period) {
			null -> context.getString(R.string.ask_me_every_time)
			else -> context.getString(
				R.string.period_formatted,
				period!!.hours,
				period!!.minutes,
			)
		}
	}

	@Serializable
	@Parcelize
	@SerialName("TimeBased")
	class TimeBased(var range: TimeRange? = null) :
		ActivityType(R.string.time_based, R.string.time_based_description) {
		override fun isComplete() = range != null
		override fun getSubtitle(context: Context) = when (range) {
			null -> context.getString(R.string.ask_me_every_time)
			else -> context.getString(
				R.string.time_range_formatted,
				range!!.startTime.hours,
				range!!.startTime.minutes,
				range!!.endTime.hours,
				range!!.endTime.minutes,
			)
		}
	}

	fun getName(context: Context) = context.getString(nameResId)
	fun getDescription(context: Context) = context.getString(descriptionResId)
	abstract fun getSubtitle(context: Context): String
	abstract fun isComplete(): Boolean
}
