package com.pointlessapps.trackr.models

import android.os.Parcelable
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
@Entity(tableName = "table_activity")
data class Activity(
	@PrimaryKey @ColumnInfo(name = "id") val id: Int = UUID.randomUUID().hashCode(),
	@ColumnInfo(name = "name") var name: String = "",
	@ColumnInfo(name = "color") @ColorInt var color: Int = 0,
	@ColumnInfo(name = "icon") @DrawableRes var icon: Int = 0,
	@ColumnInfo(name = "salary") var salary: Salary? = null,
	@ColumnInfo(name = "type") var type: ActivityType = ActivityType.OneTime(),
	@ColumnInfo(name = "weekday_availability") var weekdayAvailability: WeekdayAvailability = WeekdayAvailability()
) : Parcelable
