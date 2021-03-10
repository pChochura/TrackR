package com.pointlessapps.trackr.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
class TimePeriod(var hours: Int = 0, var minutes: Int = 0) : Parcelable