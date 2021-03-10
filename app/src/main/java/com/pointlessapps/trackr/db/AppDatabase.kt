package com.pointlessapps.trackr.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.pointlessapps.trackr.R
import com.pointlessapps.trackr.converters.ConverterAny
import com.pointlessapps.trackr.daos.DaoActivity
import com.pointlessapps.trackr.models.Activity
import com.pointlessapps.trackr.utils.Utils
import java.util.*

@Database(
	entities = [Activity::class],
	version = 1
)
@TypeConverters(ConverterAny::class)
abstract class AppDatabase : RoomDatabase() {

	abstract fun daoActivity(): DaoActivity

	companion object : Utils.SingletonHolder<AppDatabase, Context>({
		Room.databaseBuilder(
			it!!,
			AppDatabase::class.java,
			"${it.getString(R.string.app_name).toLowerCase(Locale.getDefault())}_db"
		).fallbackToDestructiveMigration().build()
	})
}