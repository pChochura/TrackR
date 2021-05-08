package com.pointlessapps.trackr.fragments

import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.pointlessapps.trackr.R
import com.pointlessapps.trackr.activities.ActivityAddActivity
import com.pointlessapps.trackr.adapters.AdapterAllActivities
import com.pointlessapps.trackr.adapters.AdapterFavourites
import com.pointlessapps.trackr.databinding.FragmentHomeBinding
import com.pointlessapps.trackr.dialogs.DialogCompleteActivityInfo
import com.pointlessapps.trackr.dialogs.DialogMessage
import com.pointlessapps.trackr.dialogs.DialogSelectFavourites
import com.pointlessapps.trackr.models.Activity
import com.pointlessapps.trackr.models.Event
import com.pointlessapps.trackr.utils.GridItemSpacingDecoration
import com.pointlessapps.trackr.utils.ItemSpacingDecoration
import com.pointlessapps.trackr.viewModels.ViewModelHome
import com.pointlessapps.trackr.viewModels.ViewModelMain
import java.util.*

class FragmentHome : FragmentCore<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

	private val mainViewModel by activityViewModels<ViewModelMain>()
	private val viewModel by activityViewModels<ViewModelHome>()
	private val launcher =
		registerForActivityResult(ActivityResultContracts.StartActivityForResult()) result@{
			if (it.resultCode != android.app.Activity.RESULT_OK) {
				return@result
			}

			val activity = it.data?.getParcelableExtra<Activity>(ActivityAddActivity.KEY_DATA)
				?: return@result

			viewModel.addActivity(activity)
		}

	override fun created() {
		viewModel.isLoading.observe(this) {
			binding.progress.isVisible = it
			binding.containerLists.isVisible = !it
		}
		binding.buttonEditFavourites.setOnClickListener { onShowEditFavouritesClicked() }

		mainViewModel.isSignedIn.observe(this) { isSignedIn ->
			if (isSignedIn) {
				prepareLists()
			}
		}
	}

	private fun prepareLists() {
		viewModel.isFavouriteSectionHidden.observe(this) { isHidden ->
			binding.containerFavourites.isGone = isHidden

			with(binding.listFavourites) {
				adapter = AdapterFavourites(viewModel.getFavourites()).apply {
					onClickListener = { onActivityClicked(it) }
					onMoreClickListener = { onActivityClicked(it, true) }
					onEditFavouritesClickListener = { onShowEditFavouritesClicked() }
					onHideSectionClickListener = {
						DialogMessage(
							requireActivity(),
							R.string.are_you_sure,
							R.string.hide_favourites_section_description,
							R.string.confirm,
							R.string.cancel
						).setOnConfirmClicked(viewModel::hideFavouriteSection).show()
					}
				}
				viewModel.getFavourites().observe(this@FragmentHome) {
					layoutManager =
						object : GridLayoutManager(context, if (it.isNotEmpty()) 2 else 1) {
							override fun canScrollVertically() = false
						}
				}
				if (itemDecorationCount > 0) {
					invalidateItemDecorations()
				} else {
					addItemDecoration(GridItemSpacingDecoration(2))
				}
			}
		}

		with(binding.listAllActivities) {
			adapter = AdapterAllActivities(viewModel.allActivities).apply {
				onAddClickListener = { onAddActivityClicked() }
				onClickListener = { onActivityClicked(it) }
				onMoreClickListener = { onActivityClicked(it, true) }
			}
			layoutManager = object : LinearLayoutManager(context) {
				override fun canScrollVertically() = false
			}
			addItemDecoration(ItemSpacingDecoration(RecyclerView.VERTICAL))
		}
	}

	private fun onShowEditFavouritesClicked() {
		if (viewModel.allActivities.value.isNullOrEmpty()) {
			DialogMessage(
				requireActivity(),
				R.string.no_activities,
				R.string.add_an_activity_first,
				R.string.ok
			).show()

			return
		}
		DialogSelectFavourites(requireActivity(), viewModel.allActivities.value!!)
			.setOnPickedListener(viewModel::setActivitiesWithFavouriteState)
			.show()
	}

	private fun onAddActivityClicked() {
		launcher.launch(Intent(requireContext(), ActivityAddActivity::class.java))
	}

	private fun onActivityClicked(activityInfo: Activity, forceCompleteInfo: Boolean = false) {
		if (activityInfo.type.isComplete() && !forceCompleteInfo) {
			addEventToCalendar(
				Event(
					date = Date(),
					activity = activityInfo
				)
			)

			return
		}

		DialogCompleteActivityInfo(requireActivity(), activityInfo, !forceCompleteInfo).show()
			.setOnAddedListener { addEventToCalendar(it) }
	}

	private fun addEventToCalendar(event: Event) {
		viewModel.addEventToCalendar(event)
		Snackbar.make(
			binding.root,
			requireContext().getString(R.string.added_activity_to_calendar, event.activity.name),
			Snackbar.LENGTH_LONG
		).setAction(R.string.undo) {
			viewModel.removeEventFromCalendar(event)
		}.show()
	}
}