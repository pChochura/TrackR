package com.pointlessapps.trackr.fragments

import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pointlessapps.trackr.activities.ActivityAddActivity
import com.pointlessapps.trackr.adapters.AdapterAllActivities
import com.pointlessapps.trackr.databinding.FragmentActivitiesBinding
import com.pointlessapps.trackr.models.Activity
import com.pointlessapps.trackr.utils.ItemSpacingDecoration
import com.pointlessapps.trackr.viewModels.ViewModelHome

class FragmentActivities :
	FragmentCore<FragmentActivitiesBinding>(FragmentActivitiesBinding::inflate) {

	private val viewModel by activityViewModels<ViewModelHome>()
	private val launcher =
		registerForActivityResult(ActivityResultContracts.StartActivityForResult()) result@{
			if (it.resultCode != android.app.Activity.RESULT_OK) {
				return@result
			}

			val activity = it.data?.getParcelableExtra<Activity>(ActivityAddActivity.KEY_DATA)
				?: return@result

			viewModel.addOrUpdateActivity(activity)
		}

	override fun created() {
		viewModel.isLoading.observe(this) {
			binding.progress.isVisible = it
			binding.containerLists.isVisible = !it
		}

		with(binding.listAllActivities) {
			adapter = AdapterAllActivities(viewModel.getAllActivities()).apply {
				onAddClickListener = { onAddActivityClicked() }
				onClickListener = { onEditActivityClicked(it) }
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

	private fun onEditActivityClicked(activity: Activity) {
		launcher.launch(
			Intent(requireContext(), ActivityAddActivity::class.java)
				.putExtra(ActivityAddActivity.KEY_DATA, activity)
		)
	}
}