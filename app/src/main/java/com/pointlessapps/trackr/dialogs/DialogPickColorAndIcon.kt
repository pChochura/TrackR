package com.pointlessapps.trackr.dialogs

import android.content.res.ColorStateList
import androidx.activity.ComponentActivity
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pointlessapps.trackr.adapters.AdapterColors
import com.pointlessapps.trackr.adapters.AdapterIcons
import com.pointlessapps.trackr.databinding.DialogPickColorAndIconBinding
import com.pointlessapps.trackr.utils.ResourcesUtils

class DialogPickColorAndIcon(private val activity: ComponentActivity, color: Int, icon: Int) :
	DialogCore<DialogPickColorAndIconBinding>(activity, DialogPickColorAndIconBinding::inflate) {

	private var onSavedListener: ((Int, Int) -> Unit)? = null

	private val allIcons = ResourcesUtils.getIcons()
	private val allColors = ResourcesUtils.getColors(activity)

	private val color = MutableLiveData(color)
	private val icon = MutableLiveData(icon)

	override fun show(): DialogPickColorAndIcon {
		makeDialog { binding, dialog ->
			color.observe(activity) {
				binding.imageIcon.backgroundTintList = ColorStateList.valueOf(it)
			}
			icon.observe(activity) { binding.imageIcon.setImageResource(it) }

			with(binding.listIcons) {
				adapter = AdapterIcons(allIcons).apply { onClickListener = { icon.value = it } }
				layoutManager = GridLayoutManager(activity, 3, RecyclerView.HORIZONTAL, false)
			}

			with(binding.listColors) {
				adapter = AdapterColors(allColors).apply { onClickListener = { color.value = it } }
				layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
			}

			binding.buttonCancel.setOnClickListener { dialog.dismiss() }
			binding.buttonSave.setOnClickListener {
				onSavedListener?.invoke(color.value!!, icon.value!!)
				dialog.dismiss()
			}
		}

		return this
	}

	fun setOnSavedListener(onSavedListener: (Int, Int) -> Unit): DialogPickColorAndIcon {
		this.onSavedListener = onSavedListener

		return this
	}
}