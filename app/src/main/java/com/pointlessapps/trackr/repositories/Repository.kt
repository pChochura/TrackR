package com.pointlessapps.trackr.repositories

import com.pointlessapps.trackr.models.Activity
import com.pointlessapps.trackr.models.Event
import com.pointlessapps.trackr.services.ServiceFirebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

class Repository {

	suspend fun insertActivity(activity: Activity) = ServiceFirebase.insertActivity(activity)
	suspend fun insertEvent(event: Event) = ServiceFirebase.insertEvent(event)
	fun removeEventById(eventId: String) = ServiceFirebase.removeEventById(eventId)

	fun getAllActivities(): Flow<List<Activity>> = flow {
		emit(emptyList())
		emitAll(ServiceFirebase.getAllActivities())
	}

	fun getEventsBetween(startTime: Long, endTime: Long): Flow<List<Event>> = flow {
		emit(emptyList())
		emitAll(ServiceFirebase.getEventsBetween(startTime, endTime))
	}
}