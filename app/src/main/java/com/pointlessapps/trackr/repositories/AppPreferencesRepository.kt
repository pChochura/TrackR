package com.pointlessapps.trackr.repositories

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.pointlessapps.trackr.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.*

class AppPreferencesRepository(coroutineScope: CoroutineScope, private val context: Context) {

	companion object {
		const val KEY_FAVOURITES_SECTION_HIDDEN = "favourites_section_hidden"
		const val KEY_FAVOURITES_IDS_COUNT = "favourites_ids_count"
		const val KEY_FAVOURITES_ID_ = "favourites_id_"
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

	suspend fun getFavouritesIds(): List<String> = context.dataStore.data.map { prefs ->
		val count = prefs[intPreferencesKey(KEY_FAVOURITES_IDS_COUNT)] ?: 0
		val list = mutableListOf<String>()
		(0 until count).forEach {
			prefs[stringPreferencesKey("${KEY_FAVOURITES_ID_}$it")]?.apply {
				list.add(this)
			}
		}
		return@map list
	}.first()

	suspend fun setFavouritesIds(ids: List<String>) {
		context.dataStore.edit { prefs ->
			val count = prefs[intPreferencesKey(KEY_FAVOURITES_IDS_COUNT)] ?: 0
			(0 until count).forEach { prefs.remove(stringPreferencesKey("${KEY_FAVOURITES_ID_}$it")) }
			ids.forEachIndexed { i, id ->
				prefs[stringPreferencesKey("${KEY_FAVOURITES_ID_}$i")] = id
			}
			prefs[intPreferencesKey(KEY_FAVOURITES_IDS_COUNT)] = ids.size
		}
	}
}