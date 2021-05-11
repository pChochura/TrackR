package com.pointlessapps.trackr.adapters

import android.content.Context
import android.content.res.ColorStateList
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import androidx.lifecycle.MutableLiveData
import androidx.viewbinding.ViewBinding
import com.pointlessapps.trackr.R
import com.pointlessapps.trackr.databinding.ItemActivityTimeSpentComparisonBinding
import com.pointlessapps.trackr.models.Activity
import com.pointlessapps.trackr.models.TimePeriod
import com.pointlessapps.trackr.utils.InflateMethod
import com.pointlessapps.trackr.utils.ifZero

class AdapterActivityTimeSpentComparison(list: List<Pair<Activity, Pair<TimePeriod, TimePeriod>>>) :
	AdapterCore<Pair<Activity, Pair<TimePeriod, TimePeriod>>>(
		MutableLiveData(list),
		viewTypeContainer
	) {

	init {
		setHasStableIds(true)
	}

	companion object {
		val viewTypeContainer = object : AdapterCore.ViewTypeContainer() {
			override fun getInflateMethodByNumber(viewType: Int): InflateMethod<ViewBinding> =
				ItemActivityTimeSpentComparisonBinding::inflate
		}
	}

	override fun onBind(binding: ViewBinding, item: Pair<Activity, Pair<TimePeriod, TimePeriod>>?) {
		if (binding !is ItemActivityTimeSpentComparisonBinding || item == null) {
			return
		}

		val currentTime = item.second.first.getCombined()
		val previousTime = item.second.second.getCombined()
		when (previousTime > currentTime) {
			true -> {
				val percent = (previousTime - currentTime) / previousTime.ifZero(1.0)
				updateTextPercentage(
					binding.textComparisonPercent,
					binding.textComparisonPercent.context,
					percent,
					R.color.text_red,
					R.drawable.ic_arrow_filled_down
				)
			}
			false -> {
				val percent = (currentTime - previousTime) / previousTime.ifZero(1.0)
				updateTextPercentage(
					binding.textComparisonPercent,
					binding.textComparisonPercent.context,
					percent,
					R.color.text_green,
					R.drawable.ic_arrow_filled_up
				)
			}
		}

		binding.imageIcon.backgroundTintList = ColorStateList.valueOf(item.first.color)
		binding.imageIcon.setImageResource(item.first.icon)
		binding.textTitle.text = item.first.name
		binding.textCurrentTime.text = binding.textCurrentTime.context.getString(
			R.string.period_formatted,
			item.second.first.hours,
			item.second.first.minutes
		)
		binding.textPreviousTime.text = binding.textPreviousTime.context.getString(
			R.string.period_formatted,
			item.second.second.hours,
			item.second.second.minutes
		)
	}

	private fun updateTextPercentage(
		textView: AppCompatTextView,
		context: Context,
		percent: Double,
		@ColorRes textColor: Int,
		@DrawableRes arrowDrawable: Int
	) {
		ContextCompat.getColor(context, textColor).also {
			textView.setTextColor(it)
			TextViewCompat.setCompoundDrawableTintList(
				textView,
				ColorStateList.valueOf(it)
			)
			textView.setCompoundDrawablesWithIntrinsicBounds(
				ContextCompat.getDrawable(context, arrowDrawable),
				null, null, null
			)
		}
		textView.text = context.getString(R.string.percent, percent * 100)
	}
}