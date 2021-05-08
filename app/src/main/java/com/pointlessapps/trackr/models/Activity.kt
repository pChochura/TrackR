package com.pointlessapps.trackr.models

import android.os.Parcelable
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
@Parcelize
class Activity(
	val id: String = UUID.randomUUID().toString(),
	var name: String = "",
	@ColorInt var color: Int = 0,
	@DrawableRes var icon: Int = 0,
	var salary: Salary? = null,
	var type: ActivityType = ActivityType.OneTime(),
	var weekdayAvailability: WeekdayAvailability = WeekdayAvailability()
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

	fun toMap() = mapOf(
		"id" to id,
		"name" to name,
		"color" to color,
		"icon" to icon,
		"type" to mapOf(
			"className" to type.javaClass.simpleName,
			"period" to (type as? ActivityType.PeriodBased)?.period,
			"range" to (type as? ActivityType.TimeBased)?.range
		),
		"salary" to salary,
		"weekdayAvailability" to weekdayAvailability.getAll().toList(),
	)

	companion object {
		fun fromDocument(document: DocumentSnapshot, prefix: String? = null): Activity {
			fun fieldOf(vararg field: String) =
				prefix?.let { FieldPath.of(prefix, *field) } ?: FieldPath.of(*field)

			return Activity(
				id = document[fieldOf("id"), String::class.java]
					?: UUID.randomUUID().toString(),
				name = document[fieldOf("name"), String::class.java] ?: "",
				color = document[fieldOf("color"), Int::class.java] ?: 0,
				icon = document[fieldOf("icon"), Int::class.java] ?: 0,
				salary = document[fieldOf("salary"), Salary::class.java],
				type = when (document[fieldOf("type", "className")]) {
					"PeriodBased" -> ActivityType.PeriodBased(
						document[fieldOf("type", "period"), TimePeriod::class.java]
					)
					"TimeBased" -> ActivityType.TimeBased(
						document[fieldOf("type", "range"), TimeRange::class.java]
					)
					else -> ActivityType.OneTime()
				},
				weekdayAvailability = WeekdayAvailability(
					(document[fieldOf("weekdayAvailability")] as List<*>).map { it == true }
						.toBooleanArray()
				)
			)
		}
	}
}
