package com.pointlessapps.trackr.charts

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.viewbinding.ViewBinding
import com.pointlessapps.trackr.utils.InflateMethod

@Suppress("LeakingThis")
open class ChartCore<Binding : ViewBinding>(
	context: Context,
	inflateMethod: InflateMethod<Binding>
) : FrameLayout(context) {
	protected var binding = inflateMethod(LayoutInflater.from(context), this, true)
}