package com.pointlessapps.trackr.fragments

import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
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
	private val viewModel by viewModels<ViewModelCalendar>()

	override fun created() {
		prepareLoader()
		prepareList()
		prepareCalendar()
	}

	private fun prepareList() {
		with(binding.listEvents) {
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
			binding.progress.isVisible = it
		}
	}

	private fun prepareCalendar() {
		viewModel.events.observe(this) {
			binding.calendar.eventsLiveData.value = it.map { event ->
				CalendarView.Event(
					event.id.hashCode(),
					event.activity.color,
					event.date.time
				)
			}.toSet()
		}

		binding.calendar.daySelectedListener = { day ->
			if (viewModel.selectedDay.value?.timeInMillis != day.timeInMillis) {
				viewModel.selectedDay.value = day
			}
		}
		binding.calendar.monthScrollListener = { month ->
			viewModel.displayedMonth.value =
				YearMonth.of(month.get(Calendar.YEAR), month.get(Calendar.MONTH) + 1)
			binding.textDate.text = monthFormat.format(month.time)
		}
	}
}