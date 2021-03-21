package com.pointlessapps.trackr.fragments

import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pointlessapps.trackr.adapters.AdapterEvents
import com.pointlessapps.trackr.databinding.FragmentCalendarBinding
import com.pointlessapps.trackr.dialogs.DialogShowEvent
import com.pointlessapps.trackr.models.Event
import com.pointlessapps.trackr.utils.ItemSpacingDecoration
import com.pointlessapps.trackr.viewModels.ViewModelCalendar
import com.pointlessapps.trackr.views.CalendarView
import java.text.SimpleDateFormat
import java.time.YearMonth
import java.util.*

class FragmentCalendar : FragmentCore<FragmentCalendarBinding>(FragmentCalendarBinding::inflate) {

	private val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
	private val viewModel by activityViewModels<ViewModelCalendar>()

	override fun created() {
		prepareLoader()
		prepareList()

		refreshed()
	}

	override fun refreshed() {
		prepareCalendar()
	}

	private fun prepareList() {
		with(root.listEvents) {
			adapter = AdapterEvents(viewModel.selectedEvents).apply {
				onClickListener = { showEventDialog(it) }
			}
			layoutManager = object : LinearLayoutManager(requireContext()) {
				override fun canScrollVertically() = false
			}
			addItemDecoration(ItemSpacingDecoration(RecyclerView.VERTICAL))
		}
	}

	private fun showEventDialog(event: Event) {
		DialogShowEvent(requireActivity(), event)
			.setOnDeleteListener { viewModel.removeEventFromCalendar(it.id) }
			.show()
	}

	private fun prepareLoader() {
		viewModel.isLoading.observe(this) {
			root.progress.isVisible = it
		}
	}

	private fun prepareCalendar() {
		viewModel.events.observe(this) {
			root.calendar.eventsLiveData.value = it.map { event ->
				CalendarView.Event(
					event.activity.color,
					event.date.time
				)
			}
		}

		root.calendar.daySelectedListener = { day ->
			viewModel.selectedDay.value = day
		}
		root.calendar.monthScrollListener = { month ->
			viewModel.displayedMonth.value =
				YearMonth.of(month.get(Calendar.YEAR), month.get(Calendar.MONTH) + 1)
			root.textDate.text = monthFormat.format(month.time)
		}
	}
}