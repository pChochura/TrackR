package com.pointlessapps.trackr.repositories

import android.content.Context
import com.pointlessapps.trackr.db.AppDatabase
import com.pointlessapps.trackr.models.Activity
import com.pointlessapps.trackr.models.Event
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

class Repository(context: Context) {

	private val daoActivity = AppDatabase.init(context).daoActivity()
	private val daoEvent = AppDatabase.init(context).daoEvent()

	suspend fun insertActivity(activity: Activity) = daoActivity.insert(activity)
	suspend fun insertEvent(event: Event) = daoEvent.insert(event)
	suspend fun removeEvent(event: Event) = daoEvent.remove(event)
	suspend fun removeEventById(eventId: String) = daoEvent.removeById(eventId)

	fun getAllActivities(): Flow<List<Activity>> = flow {
		emitAll(daoActivity.getAll())
	}

	fun getEventsBetween(startTime: Long, endTime: Long): Flow<List<Event>> = flow {
		emitAll(daoEvent.getBetween(startTime, endTime))
	}
}