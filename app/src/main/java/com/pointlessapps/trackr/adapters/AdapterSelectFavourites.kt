package com.pointlessapps.trackr.adapters

import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.viewbinding.ViewBinding
import com.google.android.material.color.MaterialColors
import com.pointlessapps.trackr.R
import com.pointlessapps.trackr.databinding.ItemSelectFavouriteBinding
import com.pointlessapps.trackr.dialogs.DialogSelectFavourites
import com.pointlessapps.trackr.utils.InflateMethod

class AdapterSelectFavourites(list: LiveData<List<DialogSelectFavourites.SelectedActivity>>) :
	AdapterCore<DialogSelectFavourites.SelectedActivity>(
		list as MutableLiveData<List<DialogSelectFavourites.SelectedActivity>>,
		viewTypeContainer
	) {

	init {
		setHasStableIds(true)
	}

	companion object {
		val viewTypeContainer = object : AdapterCore.ViewTypeContainer() {
			override fun getInflateMethodByNumber(viewType: Int): InflateMethod<ViewBinding> =
				ItemSelectFavouriteBinding::inflate
		}
	}

	var onItemSelected: ((DialogSelectFavourites.SelectedActivity) -> Unit)? = null

	override fun onBind(binding: ViewBinding, item: DialogSelectFavourites.SelectedActivity?) {
		if (binding !is ItemSelectFavouriteBinding || item == null) {
			return
		}

		binding.textTitle.text = item.activity.name
		binding.imageStar.setColorFilter(
			when (item.selected) {
				true -> ContextCompat.getColor(binding.imageStar.context, R.color.yellow)
				false -> MaterialColors.getColor(binding.imageStar, R.attr.colorSecondaryVariant)
			}
		)
		binding.root.setOnClickListener { onItemSelected?.invoke(item) }
	}

	override fun getId(item: DialogSelectFavourites.SelectedActivity?) =
		item?.activity?.id?.hashCode()?.toLong() ?: 0
}