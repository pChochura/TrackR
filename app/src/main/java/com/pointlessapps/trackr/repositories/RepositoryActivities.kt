package com.pointlessapps.trackr.repositories

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.pointlessapps.trackr.db.AppDatabase
import com.pointlessapps.trackr.models.Activity

class RepositoryActivities(context: Context) {

	private val daoActivity = AppDatabase.init(context).daoActivity()

	suspend fun insert(activity: Activity) {
		daoActivity.insert(activity)
	}

	fun getAll(): LiveData<List<Activity>> = liveData {
		emit(daoActivity.getAll())
	}
}