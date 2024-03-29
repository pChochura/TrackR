package com.pointlessapps.trackr.viewModels

import android.app.Application
import androidx.lifecycle.*
import com.pointlessapps.trackr.App
import com.pointlessapps.trackr.models.Activity
import com.pointlessapps.trackr.models.Event
import com.pointlessapps.trackr.repositories.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ViewModelHome(application: Application) : AndroidViewModel(application) {

	private val repository = Repository(application)
	private val prefs = (application as App).appPreferencesRepository
	private val _isLoading = MutableLiveData(true)
	val isLoading: LiveData<Boolean>
		get() = _isLoading

	fun getAllActivities() = liveData {
		_isLoading.postValue(true)
		emitSource(repository.getAllActivities().asLiveData())
		_isLoading.postValue(false)
	}

	suspend fun hasActivities() = repository.getAllActivities().first().isNotEmpty()

	fun getFavourites() = liveData {
		_isLoading.postValue(true)
		emitSource(
			repository.getAllActivities().map { list ->
				prefs.getFavouritesIds().mapNotNull { id ->
					list.find { it.id == id }
				}
			}.asLiveData()
		)
		_isLoading.postValue(false)
	}

	fun addOrUpdateActivity(activity: Activity) {
		viewModelScope.launch(Dispatchers.IO) {
			repository.insertActivity(activity)
		}
	}

	fun addEventToCalendar(event: Event) {
		viewModelScope.launch(Dispatchers.IO) {
			repository.insertEvent(event)
		}
	}

	fun removeEventFromCalendar(event: Event) {
		viewModelScope.launch(Dispatchers.IO) {
			repository.removeEventById(event.id)
		}
	}

	fun setActivitiesWithFavouriteState(favourites: List<Pair<String, Boolean>>) {
		viewModelScope.launch(Dispatchers.IO) {
			prefs.setFavouritesIds(favourites.filter { it.second }.map { it.first })
		}
	}
}