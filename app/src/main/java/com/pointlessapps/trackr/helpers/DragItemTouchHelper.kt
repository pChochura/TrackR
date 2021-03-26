package com.pointlessapps.trackr.helpers

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class DragItemTouchHelper(
	dragDirs: Int,
	private val onMoveListener: (RecyclerView.Adapter<*>, Int, Int) -> Unit
) : ItemTouchHelper.SimpleCallback(dragDirs, 0) {

	override fun onMove(
		recyclerView: RecyclerView,
		viewHolder: RecyclerView.ViewHolder,
		target: RecyclerView.ViewHolder
	) = true.also {
		onMoveListener(
			recyclerView.adapter!!,
			viewHolder.bindingAdapterPosition,
			target.bindingAdapterPosition
		)
	}

	override fun onSelectedChanged(
		viewHolder: RecyclerView.ViewHolder?,
		actionState: Int
	) {
		super.onSelectedChanged(viewHolder, actionState)

		if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
			viewHolder?.itemView?.alpha = 0.5f
		}
	}

	override fun clearView(
		recyclerView: RecyclerView,
		viewHolder: RecyclerView.ViewHolder
	) {
		super.clearView(recyclerView, viewHolder)
		viewHolder.itemView.alpha = 1f
	}

	override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) = Unit
}