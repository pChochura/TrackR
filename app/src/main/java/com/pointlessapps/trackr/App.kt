package com.pointlessapps.trackr

import android.app.Application
import com.pointlessapps.trackr.repositories.AppPreferencesRepository
import kotlinx.coroutines.GlobalScope

class App : Application() {

	val appPreferencesRepository by lazy { AppPreferencesRepository(GlobalScope, this) }
}