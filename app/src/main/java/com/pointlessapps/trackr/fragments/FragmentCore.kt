package com.pointlessapps.trackr.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.pointlessapps.trackr.utils.InflateMethod

abstract class FragmentCore<Binding : ViewBinding>(private val inflateMethod: InflateMethod<Binding>) :
	Fragment() {

	protected lateinit var binding: Binding

	abstract fun created()

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		if (!::binding.isInitialized) {
			binding = inflateMethod(inflater, container, false)
			created()
		}

		return binding.root
	}

	override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int) =
		runCatching {
			AnimationUtils.loadAnimation(
				requireContext(),
				transit
			)
		}.getOrElse {
			runCatching {
				AnimationUtils.loadAnimation(
					requireContext(),
					nextAnim
				)
			}.getOrNull()
		}
}
