package com.pointlessapps.trackr.models

import android.os.Parcelable
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import java.util.*

@Parcelize
@Entity(tableName = "table_activity")
@Serializable
class Activity(
	@PrimaryKey @ColumnInfo(name = "id") val id: String = UUID.randomUUID().toString(),
	@ColumnInfo(name = "name") var name: String = "",
	@ColumnInfo(name = "color") @ColorInt var color: Int = 0,
	@ColumnInfo(name = "icon") @DrawableRes var icon: Int = 0,
	@ColumnInfo(name = "salary") var salary: Salary? = null,
	@ColumnInfo(name = "type") var type: ActivityType = ActivityType.OneTime(),
	@ColumnInfo(name = "weekday_availability") var weekdayAvailability: WeekdayAvailability = WeekdayAvailability()
) : Parcelable {
	constructor(activity: Activity) : this(
		activity.id,
		activity.name,
		activity.color,
		activity.icon,
		activity.salary?.let { Salary(it) },
		when (activity.type) {
			is ActivityType.OneTime -> ActivityType.OneTime()
			is ActivityType.PeriodBased -> ActivityType.PeriodBased(
				(activity.type as ActivityType.PeriodBased).period?.let { TimePeriod(it) }
			)
			is ActivityType.TimeBased -> ActivityType.TimeBased(
				(activity.type as ActivityType.TimeBased).range?.let { TimeRange(it) }
			)
		},
		WeekdayAvailability(activity.weekdayAvailability)
	)
}
