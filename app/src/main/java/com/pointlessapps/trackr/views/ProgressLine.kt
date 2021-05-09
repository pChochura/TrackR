package com.pointlessapps.trackr.views

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.content.withStyledAttributes
import com.google.android.material.color.MaterialColors
import com.pointlessapps.trackr.R

class ProgressLine @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

	private val valueAnimator = ValueAnimator().apply {
		duration = 3000
		interpolator = DecelerateInterpolator(2f)
		addUpdateListener {
			progress = it.animatedValue as Float
			invalidate()
		}
	}

	private var progress: Float = 0f

	fun setProgress(progress: Float) {
		valueAnimator.setFloatValues(this.progress, progress)
		valueAnimator.start()
	}

	var color: Int = 0
		set(value) {
			field = value
			paint.color = value
			invalidate()
		}
	var colorBackground: Int = 0
		set(value) {
			field = value
			backgroundPaint.color = value
			invalidate()
		}

	private var backgroundPaint = Paint()
	private val paint = Paint()

	private var radius = 0f

	init {
		context.withStyledAttributes(attrs, R.styleable.ProgressLine, defStyleAttr) {
			color = getColor(
				R.styleable.ProgressLine_android_color,
				MaterialColors.getColor(this@ProgressLine, R.attr.colorPrimary)
			)
			colorBackground = getColor(
				R.styleable.ProgressLine_android_colorBackground,
				MaterialColors.getColor(this@ProgressLine, R.attr.colorPrimary)
			)
			progress = getFloat(R.styleable.ProgressLine_progress, 0f)
		}

		paint.color = color
		paint.style = Paint.Style.FILL

		backgroundPaint.color = colorBackground
		backgroundPaint.style = Paint.Style.FILL
	}

	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec)
		val width = MeasureSpec.getSize(widthMeasureSpec)
		val height = MeasureSpec.getSize(heightMeasureSpec)
		radius = Integer.min(width, height) * 0.5f
	}

	override fun onDraw(canvas: Canvas) {
		canvas.drawRoundRect(
			0f, 0f,
			width.toFloat(), height.toFloat(),
			radius, radius,
			backgroundPaint
		)
		canvas.drawRoundRect(
			0f, 0f,
			width * progress, height.toFloat(),
			radius, radius,
			paint
		)
	}
}