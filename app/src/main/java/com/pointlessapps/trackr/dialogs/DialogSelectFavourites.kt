package com.pointlessapps.trackr.dialogs

import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.recyclerview.widget.ItemTouchHelper
import com.pointlessapps.trackr.App
import com.pointlessapps.trackr.R
import com.pointlessapps.trackr.adapters.AdapterSelectFavourites
import com.pointlessapps.trackr.databinding.DialogListBinding
import com.pointlessapps.trackr.helpers.DragItemTouchHelper
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.map

class DialogSelectFavourites(
	private val activity: AppCompatActivity,
	activities: LiveData<List<com.pointlessapps.trackr.models.Activity>>
) : DialogCore<DialogListBinding>(activity, DialogListBinding::inflate) {

	private val appPreferencesRepository = (activity.application as App).appPreferencesRepository
	private var onSavedListener: ((List<Pair<String, Boolean>>) -> Unit)? = null
	private val favourites = activities.asFlow().map { data ->
		data.map { SelectedActivity(it, false) }.toMutableList()
	}.asLiveData() as MutableLiveData<MutableList<SelectedActivity>>

	override fun show(): DialogSelectFavourites {
		makeDialog { binding, dialog ->
			binding.textTitle.setText(R.string.favourites)
			binding.textSubtitle.setText(R.string.drag_and_drop_to_rearrange)
			binding.buttonSave.setOnClickListener {
				onSavedListener?.invoke(favourites.value!!.map { it.activity.id to it.selected })
				dialog.dismiss()
			}
			with(binding.listItems) {
				adapter = AdapterSelectFavourites(favourites.map { it.toList() }).apply {
					onItemSelected = { activity ->
						activity.selected = !activity.selected
						favourites.value = favourites.value
					}
				}
				ItemTouchHelper(DragItemTouchHelper(ItemTouchHelper.UP or ItemTouchHelper.DOWN) { _, from, to ->
					favourites.value = favourites.value?.also {
						val temp = it[from]
						it[from] = it[to]
						it[to] = temp
					}
				}).attachToRecyclerView(this)
			}

			favourites.observe(activity) { list ->
				if (list.isNullOrEmpty()) return@observe

				CoroutineScope(Job()).launch {
					val favourites = favourites.value?.toMutableList() ?: return@launch
					appPreferencesRepository.getFavouritesIds().reversed().forEach { id ->
						favourites.find { it.activity.id == id }?.also {
							it.selected = true
							favourites.remove(it)
							favourites.add(0, it)
						}
					}
					this@DialogSelectFavourites.favourites.postValue(favourites)
					withContext(Dispatchers.Main) {
						binding.progress.isVisible = false
						binding.listItems.alpha = 1f
					}
				}
			}
		}

		return this
	}

	fun setOnPickedListener(onSavedListener: (List<Pair<String, Boolean>>) -> Unit): DialogSelectFavourites {
		this.onSavedListener = onSavedListener

		return this
	}

	inner class SelectedActivity(
		val activity: com.pointlessapps.trackr.models.Activity,
		var selected: Boolean
	)
}