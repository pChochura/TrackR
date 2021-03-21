package com.pointlessapps.trackr.converters

import androidx.room.TypeConverter
import com.pointlessapps.trackr.models.Activity
import com.pointlessapps.trackr.models.ActivityType
import com.pointlessapps.trackr.models.Salary
import com.pointlessapps.trackr.models.WeekdayAvailability
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*

class ConverterAny {

	@TypeConverter
	fun fromSalary(input: Salary?): String = Json.encodeToString(input)

	@TypeConverter
	fun fromActivityType(input: ActivityType?): String = Json.encodeToString(input)

	@TypeConverter
	fun fromWeekdayAvailability(input: WeekdayAvailability?): String = Json.encodeToString(input)

	@TypeConverter
	fun fromActivity(input: Activity?): String = Json.encodeToString(input)

	@TypeConverter
	fun fromDate(input: Date?): Long? = input?.time

	@TypeConverter
	fun toSalary(input: String): Salary? = Json.decodeFromString(input)

	@TypeConverter
	fun toActivityType(input: String): ActivityType? = Json.decodeFromString(input)

	@TypeConverter
	fun toWeekdayAvailability(input: String): WeekdayAvailability? = Json.decodeFromString(input)

	@TypeConverter
	fun toActivity(input: String): Activity? = Json.decodeFromString(input)

	@TypeConverter
	fun toDate(input: Long?): Date? = input?.let(::Date)
}