package com.pointlessapps.trackr.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pointlessapps.trackr.models.Activity
import kotlinx.coroutines.flow.Flow

@Dao
interface DaoActivity {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(vararg item: Activity)

	@Query("SELECT * FROM table_activity")
	fun getAll(): Flow<List<Activity>>
}