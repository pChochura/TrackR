package com.pointlessapps.trackr.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ItemSpacingDecoration(
	@RecyclerView.Orientation private val orientation: Int,
	private val spacing: Int = 10.toDp()
) :
	RecyclerView.ItemDecoration() {

	override fun getItemOffsets(
		outRect: Rect,
		view: View,
		parent: RecyclerView,
		state: RecyclerView.State
	) {
		if (parent.getChildAdapterPosition(view) < 1) {
			return
		}

		when (orientation) {
			RecyclerView.VERTICAL -> outRect.top = spacing
			RecyclerView.HORIZONTAL -> outRect.left = spacing
		}
	}
}