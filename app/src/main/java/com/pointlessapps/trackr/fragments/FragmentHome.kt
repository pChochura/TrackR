package com.pointlessapps.trackr.fragments

import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.pointlessapps.trackr.R
import com.pointlessapps.trackr.adapters.AdapterFavourites
import com.pointlessapps.trackr.databinding.FragmentHomeBinding
import com.pointlessapps.trackr.dialogs.DialogCompleteActivityInfo
import com.pointlessapps.trackr.dialogs.DialogMessage
import com.pointlessapps.trackr.dialogs.DialogSelectFavourites
import com.pointlessapps.trackr.models.Activity
import com.pointlessapps.trackr.models.Event
import com.pointlessapps.trackr.utils.GridItemSpacingDecoration
import com.pointlessapps.trackr.viewModels.ViewModelHome
import com.pointlessapps.trackr.viewModels.ViewModelMain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class FragmentHome : FragmentCore<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

	private val mainViewModel by activityViewModels<ViewModelMain>()
	private val viewModel by activityViewModels<ViewModelHome>()

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
		with(binding.listFavourites) {
			adapter = AdapterFavourites(viewModel.getFavourites()).apply {
				onClickListener = { onActivityClicked(it) }
				onMoreClickListener = { onActivityClicked(it, true) }
				onEditFavouritesClickListener = { onShowEditFavouritesClicked() }
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

	private fun onShowEditFavouritesClicked() {
		lifecycleScope.launchWhenResumed {
			if (viewModel.hasActivities()) {
				withContext(Dispatchers.Main) {
					DialogMessage(
						requireActivity(),
						R.string.no_activities,
						R.string.add_an_activity_first,
						R.string.ok
					).show()
				}

				return@launchWhenResumed
			}
			withContext(Dispatchers.Main) {
				DialogSelectFavourites(requireActivity() as AppCompatActivity, viewModel.getAllActivities())
					.setOnPickedListener(viewModel::setActivitiesWithFavouriteState)
					.show()
			}
		}
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