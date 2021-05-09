package com.pointlessapps.trackr.fragments

import com.pointlessapps.trackr.charts.ChartMostFrequent
import com.pointlessapps.trackr.charts.ChartTotalEarnings
import com.pointlessapps.trackr.databinding.FragmentInsightsBinding

class FragmentInsights : FragmentCore<FragmentInsightsBinding>(FragmentInsightsBinding::inflate) {

	override fun created() {
		binding.root.addView(ChartMostFrequent(requireContext()))
		binding.root.addView(ChartTotalEarnings(requireContext()))
	}
}