package com.pointlessapps.trackr.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class GridItemSpacingDecoration(private val spanCount: Int, private val spacing: Int = 10.toDp()) :
	RecyclerView.ItemDecoration() {

	override fun getItemOffsets(
		outRect: Rect,
		view: View,
		parent: RecyclerView,
		state: RecyclerView.State
	) {
		val position = parent.getChildAdapterPosition(view)
		val size = parent.adapter?.itemCount ?: 0
		if (size <= 1) {
			return
		}
		when {
			position % spanCount < spanCount - 1 -> outRect.right = spacing / 2
			else -> outRect.left = spacing / 2
		}

		if (size <= spanCount) {
			return
		}
		if (position >= spanCount) {
			outRect.top = spacing
		}
	}
}