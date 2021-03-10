package com.pointlessapps.trackr.utils

import android.content.Context
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.pointlessapps.trackr.R
import kotlin.random.Random

object ResourcesUtils {

	fun getColors(context: Context) = listOf(
		R.color.orange,
		R.color.blue,
		R.color.yellow,
		R.color.pink,
		R.color.green,
		R.color.teal,
		R.color.red,
		R.color.dark_blue,
	).map { ContextCompat.getColor(context, it) }

	fun getIcons() = listOf(
		R.drawable.ic_accessibility,
		R.drawable.ic_accessible,
		R.drawable.ic_account,
		R.drawable.ic_add_shopping_cart,
		R.drawable.ic_agriculture,
		R.drawable.ic_alarm,
		R.drawable.ic_all_inbox,
		R.drawable.ic_announcement,
		R.drawable.ic_article,
		R.drawable.ic_assignment,
		R.drawable.ic_book,
		R.drawable.ic_bookmark,
		R.drawable.ic_bus_alert,
		R.drawable.ic_business,
		R.drawable.ic_card_giftcard,
		R.drawable.ic_commute,
		R.drawable.ic_computer,
		R.drawable.ic_contactless,
		R.drawable.ic_credit_card,
		R.drawable.ic_date_range,
		R.drawable.ic_directions_bike,
		R.drawable.ic_dns,
		R.drawable.ic_eco,
		R.drawable.ic_explore,
		R.drawable.ic_favorite,
		R.drawable.ic_flight_takeoff,
		R.drawable.ic_format_paint,
		R.drawable.ic_grade,
		R.drawable.ic_headset,
		R.drawable.ic_hotel,
		R.drawable.ic_important_devices,
		R.drawable.ic_keyboard,
		R.drawable.ic_language,
		R.drawable.ic_local_dining,
		R.drawable.ic_local_gas_station,
		R.drawable.ic_lock_clock,
		R.drawable.ic_notification_important,
		R.drawable.ic_question_answer,
		R.drawable.ic_rowing,
		R.drawable.ic_sailing,
		R.drawable.ic_savings,
	)

	@DrawableRes
	fun getRandomIcon() = getIcons().let { it[Random.nextInt(it.size)] }

	@ColorInt
	fun getRandomColor(context: Context) = getColors(context).let { it[Random.nextInt(it.size)] }
}