package com.pointlessapps.trackr.adapters

import android.content.res.ColorStateList
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.viewbinding.ViewBinding
import com.google.android.material.color.MaterialColors
import com.pointlessapps.trackr.R
import com.pointlessapps.trackr.databinding.ItemEventCardBinding
import com.pointlessapps.trackr.models.Event
import com.pointlessapps.trackr.utils.InflateMethod
import com.pointlessapps.trackr.utils.getIncome
import com.pointlessapps.trackr.utils.isNightMode

class AdapterEvents(list: LiveData<List<Event>>) :
	AdapterCore<Event>(list, viewTypeContainer) {

	init {
		setHasStableIds(true)
	}

	companion object {
		val viewTypeContainer = object : AdapterCore.ViewTypeContainer() {
			override fun getInflateMethodByNumber(viewType: Int): InflateMethod<ViewBinding> =
				ItemEventCardBinding::inflate
		}
	}

	override fun getItemId(position: Int) =
		items.value?.getOrNull(position)?.id?.hashCode()?.toLong() ?: super.getItemId(position)

	override fun onBind(binding: ViewBinding, item: Event?) {
		if (binding !is ItemEventCardBinding || item == null) {
			return
		}

		binding.containerClickable.setOnClickListener { onClickListener?.invoke(item) }

		binding.root.clipToOutline = true
		binding.textTitle.text = item.activity.name
		binding.textSubtitle.text = item.activity.type.getSubtitle(binding.textSubtitle.context)
		binding.imageIcon.setImageResource(item.activity.icon)
		val income = item.getIncome()
		binding.textIncome.isVisible = income != null
		binding.textIncome.text = income?.toString(binding.textIncome.context)

		with(item.activity.color) {
			val neutralColor = MaterialColors.getColor(binding.root, R.attr.colorSecondaryVariant)
			val textColor = MaterialColors.getColor(binding.root, android.R.attr.textColorSecondary)
			when (binding.root.context.isNightMode()) {
				true -> {
					binding.root.backgroundTintList = ColorStateList.valueOf(neutralColor)
					binding.textTitle.setTextColor(this)
					binding.textSubtitle.setTextColor(this)
					binding.textIncome.setTextColor(this)
					binding.imageIcon.setColorFilter(this)
				}
				false -> {
					binding.root.backgroundTintList = ColorStateList.valueOf(this)
					binding.textTitle.setTextColor(textColor)
					binding.textSubtitle.setTextColor(textColor)
					binding.textIncome.setTextColor(textColor)
					binding.imageIcon.setColorFilter(textColor)
				}
			}
		}
	}
}