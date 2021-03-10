package com.pointlessapps.trackr.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
class TimeRange(var startTime: TimePeriod = TimePeriod(), var endTime: TimePeriod = TimePeriod()) :
	Parcelable