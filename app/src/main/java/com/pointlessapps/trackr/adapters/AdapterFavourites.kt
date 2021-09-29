package com.pointlessapps.trackr.adapters

import android.content.res.ColorStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.viewbinding.ViewBinding
import com.google.android.material.color.MaterialColors
import com.pointlessapps.trackr.R
import com.pointlessapps.trackr.databinding.ItemActivityCardSquareBinding
import com.pointlessapps.trackr.databinding.ItemEmptyFavouritesBinding
import com.pointlessapps.trackr.models.Activity
import com.pointlessapps.trackr.utils.InflateMethod
import com.pointlessapps.trackr.utils.isNightMode

class AdapterFavourites(list: LiveData<List<Activity>>) :
	AdapterCore<Activity>(list as MutableLiveData<List<Activity>>, viewTypeContainer) {

	init {
		setHasStableIds(true)
	}

	companion object {
		val viewTypeContainer = object : AdapterCore.ViewTypeContainer() {
			override fun getInflateMethodByNumber(viewType: Int): InflateMethod<ViewBinding> =
				when (viewType) {
					ViewType.SIMPLE.ordinal -> ItemActivityCardSquareBinding::inflate
					else -> ItemEmptyFavouritesBinding::inflate
				}
		}
	}

	enum class ViewType {
		SIMPLE, EMPTY
	}

	var onMoreClickListener: ((Activity) -> Unit)? = null
	var onEditFavouritesClickListener: (() -> Unit)? = null

	override fun getItemCount() = items.value?.size?.coerceAtLeast(1) ?: 1
	override fun getViewType(item: Activity?) = when {
		items.value?.size != itemCount -> ViewType.EMPTY.ordinal
		else -> ViewType.SIMPLE.ordinal
	}

	override fun onBind(binding: ViewBinding, item: Activity?) = when {
		getViewType(item) == ViewType.SIMPLE.ordinal && item != null ->
			onBindSimple(binding as ItemActivityCardSquareBinding, item)
		else -> onBindEmpty(binding as ItemEmptyFavouritesBinding)
	}

	private fun onBindEmpty(binding: ItemEmptyFavouritesBinding) {
		binding.root.clipToOutline = true
		binding.containerClickable.setOnClickListener { onEditFavouritesClickListener?.invoke() }
	}

	private fun onBindSimple(binding: ItemActivityCardSquareBinding, item: Activity) {
		binding.containerClickable.setOnClickListener { onClickListener?.invoke(item) }
		binding.buttonMore.setOnClickListener { onMoreClickListener?.invoke(item) }

		binding.root.clipToOutline = true
		binding.textTitle.text = item.name
		binding.textSubtitle.text = item.type.getSubtitle(binding.textSubtitle.context)
		binding.imageIcon.setImageResource(item.icon)

		with(item.color) {
			val neutralColor = MaterialColors.getColor(binding.root, R.attr.colorSecondaryVariant)
			val textColor = MaterialColors.getColor(binding.root, android.R.attr.textColorSecondary)
			when (binding.root.context.isNightMode()) {
				true -> {
					binding.root.backgroundTintList = ColorStateList.valueOf(neutralColor)
					binding.textTitle.setTextColor(this)
					binding.textSubtitle.setTextColor(this)
					binding.imageIcon.setColorFilter(this)
					binding.buttonMore.setColorFilter(this)
				}
				false -> {
					binding.root.backgroundTintList = ColorStateList.valueOf(this)
					binding.textTitle.setTextColor(textColor)
					binding.textSubtitle.setTextColor(textColor)
					binding.imageIcon.setColorFilter(textColor)
					binding.buttonMore.setColorFilter(textColor)
				}
			}
		}
	}
}