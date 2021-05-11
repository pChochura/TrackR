package com.pointlessapps.trackr.fragments

import androidx.core.view.isVisible
import com.pointlessapps.trackr.charts.ChartMostFrequent
import com.pointlessapps.trackr.charts.ChartTimeSpentThisMonth
import com.pointlessapps.trackr.charts.ChartTotalEarnings
import com.pointlessapps.trackr.databinding.FragmentInsightsBinding

class FragmentInsights : FragmentCore<FragmentInsightsBinding>(FragmentInsightsBinding::inflate) {

	override fun created() {
		val charts = listOf(
			ChartMostFrequent(requireContext()),
			ChartTotalEarnings(requireContext()),
			ChartTimeSpentThisMonth(requireContext()),
		)
		var readyCounter = charts.size
		charts.forEach {
			it.isVisible = false
			it.setOnFinishedLoadingListener {
				isVisible = true
				if (--readyCounter == 0) {
					binding.progress.isVisible = false
				}
			}
			binding.listCharts.addView(it, binding.listCharts.childCount - 1)
		}
	}
}