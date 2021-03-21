package com.pointlessapps.trackr.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
@Entity(tableName = "table_event")
data class Event(
	@PrimaryKey @ColumnInfo(name = "id") val id: String = UUID.randomUUID().toString(),
	@ColumnInfo(name = "date") var date: Date,
	@Embedded(prefix = "activity_") var activity: Activity,
) : Parcelable