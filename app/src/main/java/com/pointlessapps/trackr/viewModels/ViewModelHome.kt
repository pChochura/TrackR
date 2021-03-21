package com.pointlessapps.trackr.viewModels

import android.app.Application
import androidx.lifecycle.*
import com.pointlessapps.trackr.models.Activity
import com.pointlessapps.trackr.models.Event
import com.pointlessapps.trackr.repositories.AppPreferencesRepository
import com.pointlessapps.trackr.repositories.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ViewModelHome(application: Application) : AndroidViewModel(application) {

	private val repository = Repository(application)
	private val prefs = AppPreferencesRepository(viewModelScope, application)
	private val _isLoading = MutableLiveData(true)
	val isLoading: LiveData<Boolean>
		get() = _isLoading

	val isFavouriteSectionHidden = prefs.isFavouriteSectionHidden().asLiveData()

	fun getAllActivities() = liveData {
		_isLoading.postValue(true)
		emitSource(repository.getAllActivities().asLiveData())
		_isLoading.postValue(false)
	}

	fun getFavourites() = liveData {
		_isLoading.postValue(true)
		emitSource(
			repository.getAllActivities().map { list ->
				list.filter { prefs.isActivityIdInFavourites(it.id) }
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
			repository.removeEvent(event)
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

	fun getActivitiesWithFavouriteState() {
		viewModelScope.launch(Dispatchers.IO) {
			prefs.getFavouritesIds()//TODO finish this!
		}
	}
}