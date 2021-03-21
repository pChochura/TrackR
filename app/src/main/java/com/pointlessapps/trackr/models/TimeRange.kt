package com.pointlessapps.trackr.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
class TimeRange(var startTime: TimePeriod = TimePeriod(), var endTime: TimePeriod = TimePeriod()) :
	Parcelable {
	constructor(timeRange: TimeRange) : this(timeRange.startTime, timeRange.endTime)

	fun getCombined() = endTime.getCombined() - startTime.getCombined()
}