package com.pointlessapps.trackr.models

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import java.util.concurrent.TimeUnit

@Serializable
@Parcelize
class TimePeriod(var hours: Int = 0, var minutes: Int = 0) : Parcelable {
	constructor(timePeriod: TimePeriod) : this(timePeriod.hours, timePeriod.minutes)

	@Exclude
	fun getCombined() =
		hours + minutes.toDouble() / TimeUnit.HOURS.toMinutes(1)
}