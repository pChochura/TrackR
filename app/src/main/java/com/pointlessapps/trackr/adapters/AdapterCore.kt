package com.pointlessapps.trackr.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.pointlessapps.trackr.utils.InflateMethod

abstract class AdapterCore<ItemType>(
	protected val items: LiveData<List<ItemType>>,
	private val viewTypeContainer: ViewTypeContainer
) : RecyclerView.Adapter<AdapterCore<ItemType>.ViewHolder>() {

	var onClickListener: ((ItemType) -> Unit)? = null

	private val observer = { _: List<ItemType> -> notifyDataSetChanged() }

	abstract fun onBind(binding: ViewBinding, item: ItemType?)
	open fun getViewType(item: ItemType?) = 0
	open fun getId(item: ItemType?) = item.hashCode().toLong()

	init {
		items.observeForever(observer)
	}

	override fun onViewDetachedFromWindow(holder: ViewHolder) {
		items.removeObserver(observer)
		super.onViewDetachedFromWindow(holder)
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val inflateMethod = viewTypeContainer.getInflateMethodByNumber(viewType)
		return ViewHolder(inflateMethod(LayoutInflater.from(parent.context), parent, false))
	}

	override fun getItemCount() = items.value?.size ?: 0
	override fun getItemId(position: Int) = getId(items.value?.getOrNull(position))
	override fun getItemViewType(position: Int) = getViewType(items.value?.getOrNull(position))
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		onClickListener?.also { onClickListener ->
			holder.binding.root.setOnClickListener {
				onClickListener(items.value?.getOrNull(position) ?: return@setOnClickListener)
			}
		}
		onBind(holder.binding, items.value?.getOrNull(position))
	}

	inner class ViewHolder(val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root)

	abstract class ViewTypeContainer {
		abstract fun getInflateMethodByNumber(viewType: Int): InflateMethod<ViewBinding>
	}
}