package com.pointlessapps.trackr.charts

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.viewbinding.ViewBinding
import com.pointlessapps.trackr.utils.InflateMethod

@SuppressLint("ViewConstructor")
@Suppress("LeakingThis")
open class ChartCore<Binding : ViewBinding>(
	context: Context,
	inflateMethod: InflateMethod<Binding>
) : FrameLayout(context) {

	protected var onFinishedLoadingListener: (ChartCore<Binding>.() -> Unit)? = null

	protected var binding = inflateMethod(LayoutInflater.from(context), this, true)

	fun setOnFinishedLoadingListener(onFinishedLoadingListener: ChartCore<Binding>.() -> Unit): ChartCore<Binding> {
		this.onFinishedLoadingListener = onFinishedLoadingListener

		return this
	}
}