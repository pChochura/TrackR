package com.pointlessapps.trackr.converters

import androidx.room.TypeConverter
import com.pointlessapps.trackr.models.ActivityType
import com.pointlessapps.trackr.models.Salary
import com.pointlessapps.trackr.models.WeekdayAvailability
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ConverterAny {

	@TypeConverter
	fun fromSalary(input: Salary?): String = Json.encodeToString(input)

	@TypeConverter
	fun fromActivityType(input: ActivityType?): String = Json.encodeToString(input)

	@TypeConverter
	fun fromWeekdayAvailability(input: WeekdayAvailability?): String = Json.encodeToString(input)

	@TypeConverter
	fun toSalary(input: String): Salary? = Json.decodeFromString(input)

	@TypeConverter
	fun toActivityType(input: String): ActivityType? = Json.decodeFromString(input)

	@TypeConverter
	fun toWeekdayAvailability(input: String): WeekdayAvailability? = Json.decodeFromString(input)
}