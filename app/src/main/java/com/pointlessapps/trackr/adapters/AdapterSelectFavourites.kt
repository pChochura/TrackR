package com.pointlessapps.trackr.adapters

import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.viewbinding.ViewBinding
import com.google.android.material.color.MaterialColors
import com.pointlessapps.trackr.R
import com.pointlessapps.trackr.databinding.ItemSelectFavouriteBinding
import com.pointlessapps.trackr.models.Activity
import com.pointlessapps.trackr.utils.InflateMethod

class AdapterSelectFavourites(map: Map<Activity, Boolean>) :
	AdapterCore<Pair<Activity, Boolean>>(MutableLiveData(map.toList()), viewTypeContainer) {

	init {
		setHasStableIds(true)
	}

	companion object {
		val viewTypeContainer = object : AdapterCore.ViewTypeContainer() {
			override fun getInflateMethodByNumber(viewType: Int): InflateMethod<ViewBinding> =
				ItemSelectFavouriteBinding::inflate
		}
	}

	var onItemSelected: ((Activity, Boolean) -> Unit)? = null

	override fun onBind(binding: ViewBinding, item: Pair<Activity, Boolean>?) {
		if (binding !is ItemSelectFavouriteBinding || item == null) {
			return
		}

		binding.textTitle.text = item.first.name
		binding.imageStar.setColorFilter(
			when (item.second) {
				true -> ContextCompat.getColor(binding.imageStar.context, R.color.yellow)
				false -> MaterialColors.getColor(binding.imageStar, R.attr.colorSecondaryVariant)
			}
		)
		binding.root.setOnClickListener {
			onItemSelected?.invoke(item.first, !item.second)
		}
	}
}