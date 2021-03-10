package com.pointlessapps.trackr.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.pointlessapps.trackr.utils.InflateMethod

abstract class AdapterCore<T, Binding : ViewBinding, AdderBinding : ViewBinding>(
	protected val list: LiveData<List<T>>,
	private val inflateMethod: InflateMethod<Binding>,
	private val adderInflateMethod: InflateMethod<AdderBinding>? = null
) : RecyclerView.Adapter<AdapterCore<T, Binding, AdderBinding>.ViewHolder>() {

	var onClickListener: ((T) -> Unit)? = null
	var onAddClickListener: (() -> Unit)? = null

	private val observer = { _: List<T> -> notifyDataSetChanged() }

	abstract fun onBind(root: Binding, item: T)
	open fun onBindAdder(root: AdderBinding) = Unit
	open fun onCreate(root: Binding) = Unit

	init {
		list.observeForever(observer)
	}

	override fun onViewDetachedFromWindow(holder: ViewHolder) {
		list.removeObserver(observer)
		super.onViewDetachedFromWindow(holder)
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		if (adderInflateMethod != null && viewType == +ViewType.ADDER) {
			return ViewHolder(
				null,
				adderInflateMethod.invoke(LayoutInflater.from(parent.context), parent, false)
			)
		}

		return object :
			ViewHolder(inflateMethod(LayoutInflater.from(parent.context), parent, false)) {
			init {
				onCreate(binding!!)
			}
		}
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		if (adderInflateMethod != null && position == itemCount - 1) {
			onBindAdder(holder.adderBinding!!)

			return
		}

		onBind(holder.binding!!, list.value!![position])
	}

	override fun getItemCount() = (list.value?.size ?: 0) + if (adderInflateMethod != null) 1 else 0
	override fun getItemId(position: Int) =
		list.value?.getOrNull(position)?.hashCode()?.toLong() ?: 0

	override fun getItemViewType(position: Int): Int {
		if (adderInflateMethod != null && position == itemCount - 1) {
			return +ViewType.ADDER
		}

		return +ViewType.SIMPLE
	}

	protected enum class ViewType { SIMPLE, ADDER }

	protected operator fun ViewType.unaryPlus() = ordinal

	open inner class ViewHolder(val binding: Binding?, val adderBinding: AdderBinding? = null) :
		RecyclerView.ViewHolder(binding?.root ?: adderBinding?.root!!)
}