package com.pointlessapps.trackr.models

import android.content.Context
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import java.util.*

@Parcelize
@Serializable
class WeekdayAvailability(
	private val availability: BooleanArray =
		(0 until Calendar.getInstance().getMaximum(Calendar.DAY_OF_WEEK)).map { true }
			.toBooleanArray()
) : Parcelable {
	constructor(weekdayAvailability: WeekdayAvailability) : this(weekdayAvailability.availability)

	fun getAll() = availability
	fun getAt(index: Int) = availability[index]
	fun setAt(index: Int, value: Boolean) {
		availability[index] = value
	}

	fun getRanges(context: Context): String {
		val abbreviations: Array<String> = availability
			.mapIndexed { index, _ ->
				val id = context.resources.getIdentifier(
					"weekday_${index}_abbrev",
					"string",
					context.packageName
				)
				context.getString(id).toLowerCase(Locale.getDefault())
			}.toTypedArray()

		val ranges = mutableListOf<Pair<Int, Int>>()
		availability.forEachIndexed { index, available ->
			if ((ranges.isEmpty() || !availability[ranges.last().second]) && available) {
				ranges.add(index to index)

				return@forEachIndexed
			}

			if (available || availability[ranges.last().second]) {
				ranges[ranges.lastIndex] = ranges.last().copy(second = index)
			}
		}

		ranges.forEachIndexed { index, pair ->
			if (!availability[pair.second]) {
				ranges[index] = ranges[index].copy(second = ranges[index].second - 1)
			}
		}

		if (ranges.size > 1 && ranges.first().first == 0 && ranges.last().second == availability.lastIndex) {
			ranges[ranges.lastIndex] = ranges.last().first to ranges.first().second
			ranges.removeAt(0)
		}

		return ranges.joinToString {
			when (it.first) {
				it.second -> abbreviations[it.first]
				else -> "${abbreviations[it.first]} - ${abbreviations[it.second]}"
			}
		}
	}
}
