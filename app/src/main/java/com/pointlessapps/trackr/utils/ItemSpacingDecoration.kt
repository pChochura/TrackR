package com.pointlessapps.trackr.utils

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.view.forEach
import androidx.core.view.forEachIndexed
import androidx.recyclerview.widget.RecyclerView

class ItemSpacingDecoration(
	@RecyclerView.Orientation private val orientation: Int,
	private val spacing: Int = 10.toDp(),
	@ColorInt private val color: Int = Color.TRANSPARENT,
	private val strokeWidth: Float = 1f.toDp()
) : RecyclerView.ItemDecoration() {

	private val paint = Paint().apply {
		style = Paint.Style.STROKE
		strokeCap = Paint.Cap.ROUND
		strokeWidth = this@ItemSpacingDecoration.strokeWidth
		color = this@ItemSpacingDecoration.color
	}

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

	override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
		parent.forEachIndexed { index, view ->
			if (index < 1) {
				return@forEachIndexed
			}

			val x = view.left.toFloat()
			val y = view.top.toFloat()
			when (orientation) {
				RecyclerView.VERTICAL -> c.drawLine(x, y - spacing * 0.5f, c.width.toFloat(), y - spacing * 0.5f, paint)
				RecyclerView.HORIZONTAL -> c.drawLine(x - spacing * 0.5f, y, x - spacing * 0.5f, c.height.toFloat(), paint)
			}
		}
		super.onDraw(c, parent, state)
	}
}