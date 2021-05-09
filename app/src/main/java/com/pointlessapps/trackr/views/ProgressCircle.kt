package com.pointlessapps.trackr.views

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.content.withStyledAttributes
import androidx.core.graphics.ColorUtils
import com.google.android.material.color.MaterialColors
import com.pointlessapps.trackr.R
import com.pointlessapps.trackr.utils.toDp
import java.lang.Integer.max

class ProgressCircle @JvmOverloads constructor(
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
			backgroundPaint.color =
				ColorUtils.setAlphaComponent(value, (255 * backgroundAlpha).toInt())
			invalidate()
		}
	var strokeWidth: Float = 0f
		set(value) {
			field = value
			paint.strokeWidth = strokeWidth
			invalidate()
		}
	var backgroundAlpha: Float = 0f
		set(value) {
			field = value
			backgroundPaint.color =
				ColorUtils.setAlphaComponent(color, (255 * value).toInt())
			invalidate()
		}

	private var backgroundPaint = Paint()
	private val paint = Paint()

	private var radius: Float = 0f

	init {
		context.withStyledAttributes(attrs, R.styleable.ProgressCircle, defStyleAttr) {
			color = getColor(
				R.styleable.ProgressCircle_android_color,
				MaterialColors.getColor(this@ProgressCircle, R.attr.colorPrimary)
			)
			strokeWidth = getDimension(R.styleable.ProgressCircle_strokeWidth, 4f.toDp())
			progress = getFloat(R.styleable.ProgressCircle_progress, 0f)
			backgroundAlpha = getFloat(R.styleable.ProgressCircle_backgroundAlpha, 0f)
		}

		paint.color = color
		paint.style = Paint.Style.STROKE
		paint.strokeCap = Paint.Cap.ROUND
		paint.strokeWidth = strokeWidth

		backgroundPaint.color = ColorUtils.setAlphaComponent(color, (255 * backgroundAlpha).toInt())
	}

	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec)
		val width = MeasureSpec.getSize(widthMeasureSpec)
		val height = MeasureSpec.getSize(heightMeasureSpec)
		radius = max(width, height) * 0.5f
	}

	override fun onDraw(canvas: Canvas) {
		canvas.drawCircle(width * 0.5f, height * 0.5f, radius, backgroundPaint)
		canvas.drawArc(
			strokeWidth * 0.5f, strokeWidth * 0.5f,
			width - strokeWidth * 0.5f, height - strokeWidth * 0.5f,
			-90f, progress * 360f,
			false, paint
		)
	}
}