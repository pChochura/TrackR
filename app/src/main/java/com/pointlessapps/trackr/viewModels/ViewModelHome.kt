package com.pointlessapps.trackr.viewModels

import android.app.Application
import androidx.lifecycle.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.pointlessapps.trackr.App
import com.pointlessapps.trackr.models.Activity
import com.pointlessapps.trackr.models.Event
import com.pointlessapps.trackr.repositories.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ViewModelHome(application: Application) : AndroidViewModel(application) {

	private val repository = Repository()
	private val prefs = (application as App).appPreferencesRepository
	private val _isLoading = MutableLiveData(true)
	val isLoading: LiveData<Boolean>
		get() = _isLoading

	val isFavouriteSectionHidden = prefs.isFavouriteSectionHidden().asLiveData()

	val allActivities by lazy {
		liveData {
			_isLoading.postValue(true)
			emitSource(repository.getAllActivities().asLiveData())
			_isLoading.postValue(false)
		}
	}

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

	fun addActivity(activity: Activity) {
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

	fun hideFavouriteSection() {
		viewModelScope.launch(Dispatchers.IO) {
			prefs.setFavouriteSectionHidden(true)
		}
	}

	fun showFavouriteSection() {
		viewModelScope.launch(Dispatchers.IO) {
			prefs.setFavouriteSectionHidden(false)
		}
	}

	fun setActivitiesWithFavouriteState(favourites: List<Pair<String, Boolean>>) {
		viewModelScope.launch(Dispatchers.IO) {
			prefs.setFavouritesIds(favourites.filter { it.second }.map { it.first })
		}
	}
}