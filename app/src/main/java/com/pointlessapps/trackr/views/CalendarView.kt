package com.pointlessapps.trackr.views

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Build
import android.text.TextPaint
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.OverScroller
import androidx.annotation.ColorInt
import androidx.core.animation.addListener
import androidx.core.animation.doOnEnd
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.withStyledAttributes
import androidx.core.view.NestedScrollingChild
import androidx.core.view.ViewCompat
import androidx.lifecycle.MutableLiveData
import com.google.android.material.color.MaterialColors
import com.pointlessapps.trackr.R
import com.pointlessapps.trackr.utils.toDp
import com.pointlessapps.trackr.utils.toSp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.*
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.sign

class CalendarView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
	View(context, attrs, defStyleAttr), NestedScrollingChild {
	constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
	constructor(context: Context) : this(context, null, 0)

	private val ripples = mutableListOf<Ripple>()

	var monthScrollListener: ((Calendar) -> Unit)? = null
	var daySelectedListener: ((Calendar) -> Unit)? = null
	val eventsLiveData = MutableLiveData<List<Event>>()

	private val eventsByDay = mutableMapOf<Long, List<Event>>()

	private val scroller = OverScroller(context, DecelerateInterpolator(2f))
	private val gestureDetector =
		GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
			override fun onFling(
				e1: MotionEvent?,
				e2: MotionEvent?,
				velocityX: Float,
				velocityY: Float
			) = true
			override fun onSingleTapUp(e: MotionEvent): Boolean {
				if (e.x > paddingLeft && e.x < 7 * columnWidth + paddingLeft && e.y > headerPaddingVertical * 2 + textHeight + paddingTop) {
					val x = ((e.x - paddingLeft) / columnWidth).toInt()
					val y =
						((e.y - paddingTop - headerPaddingVertical * 2 - textHeight) / rowHeight).toInt()
					runOnCalendar(currentMonth.timeInMillis) {
						set(Calendar.DAY_OF_MONTH, 1)
						add(
							Calendar.DAY_OF_MONTH,
							-(get(Calendar.DAY_OF_WEEK) + 7 - Calendar.MONDAY) % 7
						)
						add(Calendar.DAY_OF_WEEK, y * 7 + x)
						if (selectedDay.get(Calendar.DAY_OF_MONTH) != get(Calendar.DAY_OF_MONTH)) {
							selectedDay.timeInMillis = timeInMillis
							invalidate()
							daySelectedListener?.invoke(selectedDay.clone() as Calendar)
						}
					}

					return true
				}

				return super.onSingleTapUp(e)
			}

			override fun onDown(e: MotionEvent?): Boolean {
				scroller.forceFinished(true)
				return true
			}

			override fun onScroll(
				e1: MotionEvent?,
				e2: MotionEvent?,
				distanceX: Float,
				distanceY: Float
			): Boolean {
				startNestedScroll(ViewCompat.SCROLL_AXIS_HORIZONTAL or ViewCompat.SCROLL_AXIS_VERTICAL)
				dispatchNestedScroll(distanceX.toInt(), distanceY.toInt(), 0, 0, null)
				offsetX -= distanceX.toInt()
				if (abs(offsetX) > 7 * columnWidth) {
					currentMonth.add(Calendar.MONTH, -offsetX.sign)
					monthScrollListener?.invoke(currentMonth.clone() as Calendar)
					offsetX -= (7 * columnWidth) * offsetX.sign
				}
				invalidate()
				return true
			}
		})

	private var offsetX = 0

	private var textSize: Float = 0f
	private var rowHeight: Int = 0
	private var columnWidth: Int = 0
	private var textColorPrimary: Int = 0
	private var textColorSecondary: Int = 0
	private var colorAccent: Int = 0
	private var fontFamily: Typeface? = null
	private var autoSize = false

	private val textPaint = TextPaint()
	private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

	private val headerPaddingVertical = 4.toDp()
	private val tempCalendar = Calendar.getInstance()
	private val today = Calendar.getInstance()
	private val selectedDay = Calendar.getInstance()
	private val currentMonth = Calendar.getInstance().apply {
		firstDayOfWeek = Calendar.MONDAY
	}

	private var textHeight = 0
	private val headerLabels = (0..6).map {
		resources.getString(
			resources.getIdentifier("weekday_${it}_abbrev", "string", context.packageName)
		).toUpperCase(Locale.getDefault())
	}

	private val eventsObserver: (List<Event>) -> Unit = { list ->
		eventsByDay.apply {
			clear()
			putAll(
				list.groupBy {
					Instant.ofEpochMilli(it.dateInMillis).atZone(ZoneId.systemDefault())
						.toLocalDate().toEpochDay()
				}
			)
		}
		daySelectedListener?.invoke(selectedDay.clone() as Calendar)
		invalidate()
	}

	override fun onAttachedToWindow() {
		super.onAttachedToWindow()
		eventsLiveData.observeForever(eventsObserver)
	}

	override fun onDetachedFromWindow() {
		eventsLiveData.removeObserver(eventsObserver)
		super.onDetachedFromWindow()
	}

	init {
		context.withStyledAttributes(attrs, R.styleable.CalendarView, defStyleAttr) {
			textSize = getDimension(R.styleable.CalendarView_android_textSize, 12f.toSp())
			rowHeight = getDimension(R.styleable.CalendarView_android_rowHeight, 0f).toInt()
			textColorPrimary = getColor(
				R.styleable.CalendarView_android_textColorPrimary,
				MaterialColors.getColor(this@CalendarView, android.R.attr.textColorPrimary)
			)
			textColorSecondary = getColor(
				R.styleable.CalendarView_android_textColorSecondary,
				MaterialColors.getColor(this@CalendarView, android.R.attr.textColorSecondary)
			)
			colorAccent = getColor(
				R.styleable.CalendarView_android_colorAccent,
				MaterialColors.getColor(this@CalendarView, android.R.attr.colorAccent)
			)
			fontFamily = when {
				Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> getFont(R.styleable.CalendarView_android_fontFamily)
				else -> ResourcesCompat.getFont(
					context,
					getResourceId(R.styleable.CalendarView_android_fontFamily, R.font.montserrat)
				)
			}
		}

		if (rowHeight == 0) {
			autoSize = true
		}

		textPaint.textSize = textSize
		textPaint.textAlign = Paint.Align.CENTER
		textPaint.typeface = fontFamily
		textHeight = Rect().also { textPaint.getTextBounds("0", 0, 1, it) }.height()
		post { monthScrollListener?.invoke(currentMonth.clone() as Calendar) }
	}

	private fun runOnCalendar(
		startingTime: Long = tempCalendar.timeInMillis,
		block: Calendar.() -> Unit
	) {
		val timeInMillis = tempCalendar.timeInMillis
		tempCalendar.timeInMillis = startingTime
		block(tempCalendar)
		tempCalendar.timeInMillis = timeInMillis
	}

	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		val widthMode = MeasureSpec.getMode(widthMeasureSpec)
		val widthSize = MeasureSpec.getSize(widthMeasureSpec)
		val heightMode = MeasureSpec.getMode(heightMeasureSpec)

		if (widthMode == MeasureSpec.UNSPECIFIED && heightMode == MeasureSpec.UNSPECIFIED) {
			throw UnsupportedOperationException("Cannot calculate the values for day Width/Height with the current configuration.")
		}

		columnWidth = ((widthSize - (paddingLeft + paddingRight)) / 7f + 0.5).toInt()
		if (autoSize) {
			rowHeight = columnWidth
		}
		super.onMeasure(
			widthMeasureSpec,
			MeasureSpec.makeMeasureSpec(
				rowHeight * 6 + headerPaddingVertical * 2 + textHeight + paddingTop + paddingBottom,
				MeasureSpec.EXACTLY
			)
		)
	}

	@SuppressLint("ClickableViewAccessibility")
	override fun onTouchEvent(event: MotionEvent): Boolean {
		if (event.pointerCount > 1) {
			ripples.lastOrNull()?.finish(ripples::remove)
		}

		when (event.action) {
			MotionEvent.ACTION_DOWN -> {
				val x = event.x
				val y = event.y

				if (y < headerPaddingVertical * 2 + textHeight && x < paddingLeft && x > width - paddingRight) {
					return gestureDetector.onTouchEvent(event)
				}

				val finalX =
					(((x - paddingLeft) / columnWidth).toInt() + 0.5f) * columnWidth + paddingLeft
				val finalY =
					(((y - headerPaddingVertical * 2 - textHeight) / rowHeight).toInt() + 0.5f) * rowHeight + headerPaddingVertical * 2 + textHeight
				ripples.add(Ripple(x, y, finalX, finalY, rowHeight * 0.75f))

				return gestureDetector.onTouchEvent(event)
			}
			MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
				stopNestedScroll()
				ripples.lastOrNull()?.finish(ripples::remove)
				val diffX = offsetX.sign * (7 * columnWidth) - offsetX
				scroller.startScroll(
					offsetX,
					0,
					if (abs(offsetX) < columnWidth) -offsetX else diffX,
					0,
					200
				)
				return gestureDetector.onTouchEvent(event)
			}
		}
		return gestureDetector.onTouchEvent(event)
	}

	override fun onDraw(canvas: Canvas) {
		if (scroller.computeScrollOffset()) {
			offsetX = scroller.currX
			if (scroller.isFinished && offsetX != 0) {
				currentMonth.add(Calendar.MONTH, -offsetX.sign)
				monthScrollListener?.invoke(currentMonth.clone() as Calendar)
				offsetX = 0
			}
			invalidate()
		}

		onDrawBackground(canvas)
		ripples.forEach { it.draw(canvas) }
		((-1)..1).forEach { offset ->
			onDrawHeader(canvas, offset)
			onDrawDays(canvas, offset)
		}
	}

	private fun onDrawEvents(
		canvas: Canvas,
		events: List<Event>?,
		currentMonth: Boolean,
		x: Float,
		y: Float
	) {
		if (events == null) {
			return
		}

		val dotSize = 4f.toDp()
		val count = events.size
		val offset = (count - 1) * 0.75f * dotSize
		events.forEachIndexed { index, event ->
			val dotPosX = -offset + index * 1.5f * dotSize
			val dotPosY = rowHeight.toFloat() * 0.5f - dotSize * 3
			paint.color = when (currentMonth) {
				true -> event.color
				false -> MaterialColors.compositeARGBWithAlpha(
					event.color,
					(0.4 * 255).toInt()
				)
			}
			canvas.drawCircle(x + dotPosX, y + dotPosY, dotSize * 0.5f, paint)
		}
	}

	private fun onDrawDays(canvas: Canvas, monthOffset: Int = 0) {
		runOnCalendar(currentMonth.timeInMillis) {
			add(Calendar.MONTH, monthOffset)
			val currentMonthInt = get(Calendar.MONTH)
			set(Calendar.DAY_OF_MONTH, 1)
			add(Calendar.DAY_OF_MONTH, -(get(Calendar.DAY_OF_WEEK) + 7 - Calendar.MONDAY) % 7)
			(0..6).forEach { column ->
				(0..5).forEach { row ->
					val isDaySelected = selectedDay.get(Calendar.DAY_OF_YEAR) == get(Calendar.DAY_OF_YEAR) &&
							selectedDay.get(Calendar.YEAR) == get(Calendar.YEAR)
					textPaint.color = when {
						isDaySelected -> Color.WHITE
						currentMonthInt == get(Calendar.MONTH) -> textColorPrimary
						else -> textColorSecondary
					}
					val x = paddingLeft + columnWidth * column +
							columnWidth * 0.5f + offsetX + monthOffset * 7 * columnWidth
					val y = 2 * headerPaddingVertical + textHeight +
							row * rowHeight + (rowHeight + textHeight) * 0.5f + paddingTop
					if (isDaySelected) {
						paint.color = when (currentMonthInt) {
							get(Calendar.MONTH) -> colorAccent
							else -> MaterialColors.compositeARGBWithAlpha(
								colorAccent,
								(0.4 * 255).toInt()
							)
						}
						canvas.drawCircle(
							x,
							y - textHeight,
							min(rowHeight, columnWidth) * 0.5f,
							paint
						)
					}
					if (today.get(Calendar.DAY_OF_YEAR) == get(Calendar.DAY_OF_YEAR) &&
						today.get(Calendar.YEAR) == get(Calendar.YEAR)
					) {
						paint.style = Paint.Style.STROKE
						paint.color = textPaint.color
						canvas.drawCircle(
							x,
							y - textHeight,
							min(rowHeight, columnWidth) * 0.5f,
							paint
						)
						paint.style = Paint.Style.FILL
					}
					canvas.drawText("${get(Calendar.DAY_OF_MONTH)}", x, y - textHeight, textPaint)

					val epochDay = LocalDate.of(
						get(Calendar.YEAR),
						get(Calendar.MONTH) + 1,
						get(Calendar.DAY_OF_MONTH)
					).toEpochDay()
					if (!eventsByDay[epochDay].isNullOrEmpty()) {
						onDrawEvents(
							canvas,
							eventsByDay[epochDay],
							currentMonthInt == get(Calendar.MONTH),
							x,
							y
						)
					}

					add(Calendar.WEEK_OF_YEAR, 1)
				}
				add(Calendar.WEEK_OF_YEAR, -6)
				add(Calendar.DAY_OF_WEEK, 1)
			}
		}
	}

	private fun onDrawHeader(canvas: Canvas, monthOffset: Int = 0) {
		(0..6).forEach {
			val text = headerLabels[it]
			textPaint.color = textColorSecondary
			val x =
				columnWidth * it + columnWidth * 0.5f + paddingLeft + offsetX + monthOffset * 7 * columnWidth
			val y = headerPaddingVertical + textHeight.toFloat() + paddingTop
			canvas.drawText(text, x, y, textPaint)
		}
	}

	private fun onDrawBackground(canvas: Canvas) {
		backgroundTintList?.defaultColor?.let(canvas::drawColor)
	}

	inner class Ripple(
		private var x: Float,
		private var y: Float,
		private var finalX: Float,
		private var finalY: Float,
		private val finalRadius: Float,
	) {
		private var radius = 0f
		private var percent = 0f
		private var onEndListener: ((Ripple) -> Unit)? = null
		private var finishAnimation: ValueAnimator? = null

		private fun animation(onUpdate: (Float) -> Unit) =
			ValueAnimator.ofInt(0, 100).apply {
				duration = 200
				interpolator = DecelerateInterpolator(2f)
				addUpdateListener { onUpdate(it.animatedFraction) }
			}

		init {
			animation {
				percent = it
				invalidate()
			}.also(ValueAnimator::start).doOnEnd {
				prepareFinishAnimation()
				if (onEndListener != null) {
					finishAnimation?.start()
				}
			}
		}

		private fun prepareFinishAnimation() {
			finishAnimation = animation {
				percent = 1f - it
				invalidate()
			}.apply {
				addListener(
					onEnd = {
						onEndListener?.invoke(this@Ripple)
					},
					onStart = {
						x = finalX
						y = finalY
						radius = finalRadius
					}
				)
			}
		}

		fun draw(canvas: Canvas) {
			paint.color = MaterialColors.getColor(this@CalendarView, R.attr.colorPrimaryVariant)
			paint.alpha = (percent * 255 * 0.5).toInt()
			canvas.drawCircle(
				x + (finalX - x) * percent + offsetX,
				y + (finalY - y) * percent,
				radius + (finalRadius - radius) * percent,
				paint
			)
		}

		fun finish(onEndListener: (Ripple) -> Unit) {
			this.onEndListener = onEndListener
			finishAnimation?.start()
		}
	}

	data class Event(
		@ColorInt val color: Int,
		val dateInMillis: Long
	)
}