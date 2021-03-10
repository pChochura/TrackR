package com.pointlessapps.trackr.viewModels

import android.app.Application
import androidx.lifecycle.*
import com.pointlessapps.trackr.models.Activity
import com.pointlessapps.trackr.repositories.RepositoryActivities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ViewModelHome(application: Application) : AndroidViewModel(application) {

	private val activityRepository = RepositoryActivities(application)

	private val _activities: MutableLiveData<MutableList<Activity>> =
		Transformations.map(
			activityRepository.getAll(),
			List<Activity>::toMutableList
		) as MutableLiveData<MutableList<Activity>>
	private val activities: LiveData<List<Activity>>
		get() = Transformations.map(_activities) { it.toList() }

	fun getFavourites() = activities
	fun getAllActivities() = activities
	fun addActivity(activity: Activity?) {
		activity ?: return
		_activities.value = _activities.value?.also {
			it.add(activity)
		}
		viewModelScope.launch(Dispatchers.IO) {
			activityRepository.insert(activity)
		}
	}
}