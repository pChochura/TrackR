package com.pointlessapps.trackr.viewModels

import android.app.Application
import androidx.lifecycle.*
import com.pointlessapps.trackr.repositories.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.YearMonth
import java.time.ZoneId
import java.util.*
import java.util.concurrent.TimeUnit

class ViewModelCalendar(application: Application) : AndroidViewModel(application) {

	private val repository = Repository(application)

	val displayedMonth = MutableLiveData<YearMonth>()
	val selectedDay = MutableLiveData<Calendar>()

	private val _isLoading = MutableLiveData(true)
	val isLoading: LiveData<Boolean>
		get() = _isLoading

	val events = Transformations.switchMap(displayedMonth) { month ->
		liveData {
			_isLoading.postValue(true)
			emitSource(
				repository.getEventsBetween(
					month.minusMonths(1).atDay(1).atStartOfDay()
						.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
					month.plusMonths(1).atEndOfMonth().atStartOfDay()
						.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
				).asLiveData()
			)
			_isLoading.postValue(false)
		}
	}

	val selectedEvents = Transformations.switchMap(selectedDay) {
		val startOfDay = it.apply {
			set(Calendar.HOUR_OF_DAY, 0)
			set(Calendar.MINUTE, 0)
			set(Calendar.SECOND, 0)
		}
		liveData {
			_isLoading.postValue(true)
			emitSource(
				repository.getEventsBetween(
					startOfDay.timeInMillis,
					startOfDay.timeInMillis + TimeUnit.DAYS.toMillis(1)
				).asLiveData()
			)
			_isLoading.postValue(false)
		}
	}

	fun removeEventFromCalendar(eventId: String) {
		viewModelScope.launch(Dispatchers.IO) {
			repository.removeEventById(eventId)
		}
	}
}