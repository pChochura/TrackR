package com.pointlessapps.trackr.daos

import androidx.room.*
import com.pointlessapps.trackr.models.Event
import kotlinx.coroutines.flow.Flow

@Dao
interface DaoEvent {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(vararg item: Event)

	@Delete
	suspend fun remove(event: Event)

	@Query("DELETE FROM table_event WHERE id = :id")
	suspend fun removeById(id: String)

	@Query("SELECT * FROM table_event")
	fun getAll(): Flow<List<Event>>

	@Query("SELECT * FROM table_event WHERE date >= :startTime AND date <= :endTime")
	fun getBetween(startTime: Long, endTime: Long): Flow<List<Event>>
}