package com.pointlessapps.trackr.models

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
class TimeRange(var startTime: TimePeriod = TimePeriod(), var endTime: TimePeriod = TimePeriod()) : Parcelable {
	constructor(timeRange: TimeRange) : this(timeRange.startTime, timeRange.endTime)

	@Exclude
	fun getCombined() = endTime.getCombined() - startTime.getCombined()
}