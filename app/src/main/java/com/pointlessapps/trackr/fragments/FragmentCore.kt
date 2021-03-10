package com.pointlessapps.trackr.fragments

import android.animation.Animator
import android.animation.AnimatorInflater
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.pointlessapps.trackr.R
import com.pointlessapps.trackr.utils.InflateMethod

abstract class FragmentCore<Binding : ViewBinding>(private val inflateMethod: InflateMethod<Binding>) :
	Fragment() {

	var forcePopBackStack: (() -> Unit)? = null
	var forceChangeFragment: ((FragmentCore<*>) -> Unit)? = null

	abstract fun created()
	open fun refreshed() = Unit

	private var _root: Binding? = null
	protected val root: Binding
		get() = _root!!

	private var forceRefresh = false
		get() {
			field = false
			return true
		}

	fun forceRefresh() {
		forceRefresh = true
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		if (_root == null || forceRefresh) {
			_root = inflateMethod(inflater, container, false)

			created()
		} else {
			refreshed()
		}

		return _root?.root
	}

	override fun onCreateAnimator(transit: Int, enter: Boolean, nextAnim: Int): Animator? =
		AnimatorInflater.loadAnimator(
			requireContext(),
			if (enter) {
				R.anim.fade_in
			} else {
				R.anim.fade_out
			}
		)

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
