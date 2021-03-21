package com.pointlessapps.trackr.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import java.util.concurrent.TimeUnit

@Parcelize
@Serializable
class TimePeriod(var hours: Int = 0, var minutes: Int = 0) : Parcelable {
	constructor(timePeriod: TimePeriod) : this(timePeriod.hours, timePeriod.minutes)

	fun getCombined() =
		hours + minutes.toFloat() / TimeUnit.HOURS.toMinutes(1)
}