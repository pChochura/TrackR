package com.pointlessapps.trackr.fragments

import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pointlessapps.trackr.activities.ActivityAddActivity
import com.pointlessapps.trackr.adapters.AdapterAllActivities
import com.pointlessapps.trackr.adapters.AdapterFavourites
import com.pointlessapps.trackr.databinding.FragmentHomeBinding
import com.pointlessapps.trackr.models.Activity
import com.pointlessapps.trackr.utils.GridItemSpacingDecoration
import com.pointlessapps.trackr.utils.ItemSpacingDecoration
import com.pointlessapps.trackr.viewModels.ViewModelHome

class FragmentHome : FragmentCore<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

	private val viewModel by activityViewModels<ViewModelHome>()
	private val launcher =
		registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
			if (it.resultCode != android.app.Activity.RESULT_OK) {
				return@registerForActivityResult
			}

			val activity = it.data?.getParcelableExtra<Activity>(ActivityAddActivity.KEY_DATA)
			viewModel.addActivity(activity)
		}

	override fun created() {
		with(root.listFavourites) {
			adapter = AdapterFavourites(viewModel.getFavourites()).apply {
				onClickListener = { onActivityClicked(it) }
				setHasStableIds(true)
			}
			layoutManager = object : GridLayoutManager(context, 2) {
				override fun canScrollVertically() = false
			}
			addItemDecoration(GridItemSpacingDecoration(2))
		}

		with(root.listAllActivities) {
			adapter = AdapterAllActivities(viewModel.getAllActivities()).apply {
				setHasStableIds(true)
				onAddClickListener = { onAddActivityClicked() }
				onClickListener = { onActivityClicked(it) }
			}
			layoutManager = object : LinearLayoutManager(context) {
				override fun canScrollVertically() = false
			}
			addItemDecoration(ItemSpacingDecoration(RecyclerView.VERTICAL))
		}
	}

	private fun onAddActivityClicked() {
		launcher.launch(Intent(requireContext(), ActivityAddActivity::class.java))
	}

	private fun onActivityClicked(activity: Activity) {

	}
}