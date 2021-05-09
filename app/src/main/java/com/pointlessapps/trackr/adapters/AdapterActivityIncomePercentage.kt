package com.pointlessapps.trackr.adapters

import android.content.res.ColorStateList
import androidx.lifecycle.MutableLiveData
import androidx.viewbinding.ViewBinding
import com.pointlessapps.trackr.R
import com.pointlessapps.trackr.databinding.ItemActivityIncomePercentageBinding
import com.pointlessapps.trackr.models.Activity
import com.pointlessapps.trackr.utils.InflateMethod

class AdapterActivityIncomePercentage(list: List<Pair<Activity, Double>>) :
	AdapterCore<Pair<Activity, Double>>(MutableLiveData(list), viewTypeContainer) {

	init {
		setHasStableIds(true)
	}

	companion object {
		val viewTypeContainer = object : AdapterCore.ViewTypeContainer() {
			override fun getInflateMethodByNumber(viewType: Int): InflateMethod<ViewBinding> =
				ItemActivityIncomePercentageBinding::inflate
		}
	}

	override fun onBind(binding: ViewBinding, item: Pair<Activity, Double>?) {
		if (binding !is ItemActivityIncomePercentageBinding || item == null) {
			return
		}

		binding.imageIcon.backgroundTintList = ColorStateList.valueOf(item.first.color)
		binding.imageIcon.setImageResource(item.first.icon)
		binding.textTitle.text = item.first.name
		binding.textPercentage.text =
			binding.textPercentage.context.getString(R.string.percent, item.second)
		binding.progress.setProgress(item.second.toFloat() / 100f)
		binding.progress.color = item.first.color
	}
}