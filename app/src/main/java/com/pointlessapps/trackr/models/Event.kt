package com.pointlessapps.trackr.models

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import java.util.*

data class Event(
	val id: String = UUID.randomUUID().toString(),
	var date: Date,
	var activity: Activity,
) {

	fun toMap() = mapOf(
		"id" to id,
		"date" to date.time,
		"activity" to mapOf(
			"id" to activity.id,
			"name" to activity.name,
			"color" to activity.color,
			"icon" to activity.icon,
			"type" to mapOf(
				"className" to activity.type.javaClass.simpleName,
				"period" to (activity.type as? ActivityType.PeriodBased)?.period,
				"range" to (activity.type as? ActivityType.TimeBased)?.range
			),
			"salary" to activity.salary,
			"weekdayAvailability" to activity.weekdayAvailability.getAll().toList(),
		)
	)

	companion object {
		fun fromDocument(document: DocumentSnapshot) = Event(
			id = document["id", String::class.java] ?: UUID.randomUUID().toString(),
			date = Date(document["date", Long::class.java] ?: 0),
			activity = Activity.fromDocument(document, "activity")
		)
	}
}