package com.pointlessapps.trackr.repositories

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.pointlessapps.trackr.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.*

class AppPreferencesRepository(coroutineScope: CoroutineScope, private val context: Context) {

	companion object {
		const val KEY_FAVOURITES_SECTION_HIDDEN = "favourites_section_hidden"
		const val KEY_FAVOURITES_IDS = "favourites_ids"
	}

	private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
		name = "${
			context.getString(
				R.string.app_name
			).toLowerCase(Locale.getDefault())
		}_prefs",
		scope = coroutineScope
	)

	fun isFavouriteSectionHidden(defaultValue: Boolean = false) = context.dataStore.data.map {
		it[booleanPreferencesKey(KEY_FAVOURITES_SECTION_HIDDEN)] ?: defaultValue
	}

	suspend fun setFavouriteSectionHidden(hidden: Boolean) {
		context.dataStore.edit { it[booleanPreferencesKey(KEY_FAVOURITES_SECTION_HIDDEN)] = hidden }
	}

	suspend fun isActivityIdInFavourites(id: String) = context.dataStore.data.map {
		it[stringSetPreferencesKey(KEY_FAVOURITES_IDS)]?.contains(id) ?: false
	}.first()

	suspend fun getFavouritesIds() = context.dataStore.data.map {
		it[stringSetPreferencesKey(KEY_FAVOURITES_IDS)] ?: setOf()
	}.first()
}