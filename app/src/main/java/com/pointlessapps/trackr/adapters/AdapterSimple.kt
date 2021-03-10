package com.pointlessapps.trackr.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.viewbinding.ViewBinding

open class AdapterSimple<T, Binding : ViewBinding>(
	list: LiveData<List<T>>,
	inflateMethod: (LayoutInflater, ViewGroup?, Boolean) -> Binding
) : AdapterCore<T, Binding, Nothing>(list, inflateMethod) {

	override fun onBind(root: Binding, item: T) {
		root.root.setOnClickListener { onClickListener?.invoke(item) }
	}
}