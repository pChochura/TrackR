package com.pointlessapps.trackr.models

import android.content.Context
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import java.util.*

data class Event(
	val id: String = UUID.randomUUID().toString(),
	var date: Date,
	var activity: Activity,
) {

	fun toMap(context: Context) = mapOf(
		"id" to id,
		"date" to date.time,
		"activity" to activity.toMap(context)
	)

	companion object {
		fun fromDocument(context: Context, document: DocumentSnapshot) = Event(
			id = document["id", String::class.java] ?: UUID.randomUUID().toString(),
			date = Date(document["date", Long::class.java] ?: 0),
			activity = Activity.fromDocument(context, document, "activity")
		)
	}
}