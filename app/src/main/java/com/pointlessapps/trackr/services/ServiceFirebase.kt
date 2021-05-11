package com.pointlessapps.trackr.services

import android.content.Context
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.pointlessapps.trackr.models.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
object ServiceFirebase {

	private const val COLLECTION_USERS = "users"
	private const val COLLECTION_ACTIVITIES = "activities"
	private const val COLLECTION_EVENTS = "events"

	fun observeAllActivities(context: Context) = callbackFlow {
		val uid = Firebase.auth.currentUser?.uid ?: return@callbackFlow
		val listener = Firebase.firestore.collection(COLLECTION_USERS)
			.document(uid).collection(COLLECTION_ACTIVITIES).addSnapshotListener { value, _ ->
				if (value?.isEmpty == false) {
					offer(value.documents.map { Activity.fromDocument(context, it) })
				}
			}

		awaitClose { listener.remove() }
	}

	fun observeEventsBetween(context: Context, startTime: Long, endTime: Long) = callbackFlow {
		val uid = Firebase.auth.currentUser?.uid ?: return@callbackFlow
		val listener = Firebase.firestore.collection(COLLECTION_USERS)
			.document(uid).collection(COLLECTION_EVENTS)
			.whereGreaterThan("date", startTime)
			.whereLessThan("date", endTime)
			.addSnapshotListener { value, _ ->
				if (value?.isEmpty == false) {
					offer(value.documents.map { Event.fromDocument(context, it) })
				}
			}

		awaitClose { listener.remove() }
	}

	suspend fun getEventsBetween(context: Context, startTime: Long, endTime: Long): List<Event> {
		val uid = Firebase.auth.currentUser?.uid ?: return emptyList()
		return Firebase.firestore.collection(COLLECTION_USERS)
			.document(uid).collection(COLLECTION_EVENTS)
			.whereGreaterThan("date", startTime)
			.whereLessThan("date", endTime)
			.get().await().documents.map { Event.fromDocument(context, it) }
	}

	suspend fun insertActivity(context: Context, activity: Activity) {
		val uid = Firebase.auth.currentUser?.uid ?: return
		Firebase.firestore.collection(COLLECTION_USERS)
			.document(uid).collection(COLLECTION_ACTIVITIES)
			.document(activity.id)
			.set(activity.toMap(context), SetOptions.merge())
			.await()
	}

	suspend fun insertEvent(context: Context, event: Event) {
		val uid = Firebase.auth.currentUser?.uid ?: return
		Firebase.firestore.collection(COLLECTION_USERS)
			.document(uid).collection(COLLECTION_EVENTS)
			.document(event.id)
			.set(event.toMap(context))
			.await()
	}

	fun removeEventById(eventId: String) {
		val uid = Firebase.auth.currentUser?.uid ?: return
		Firebase.firestore.collection(COLLECTION_USERS)
			.document(uid).collection(COLLECTION_EVENTS)
			.document(eventId).delete()
	}
}