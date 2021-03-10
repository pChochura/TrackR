package com.pointlessapps.trackr.managers


import androidx.annotation.IdRes
import androidx.core.view.children
import androidx.core.view.get
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pointlessapps.trackr.R
import com.pointlessapps.trackr.fragments.FragmentCore

class FragmentManager private constructor(
	private val fragmentManager: androidx.fragment.app.FragmentManager,
	private val bottomNavigation: BottomNavigationView,
	private val fragments: Array<out FragmentCore<*>>
) {
	private val history = mutableListOf<FragmentCore<*>>()

	private var startingPosition = -1

	@IdRes
	private var containerId: Int? = null
	var currentFragment: FragmentCore<*>? = null
		private set

	companion object {
		fun of(
			fragmentManager: androidx.fragment.app.FragmentManager,
			bottomNavigation: BottomNavigationView,
			vararg fragments: FragmentCore<*>
		) = FragmentManager(fragmentManager, bottomNavigation, fragments)
	}

	init {
		fragments.forEach(::prepareFragment)
		bottomNavigation.apply {
			setOnNavigationItemSelectedListener {
				setFragment(fragments[menu.children.indexOf(it)])
				true
			}
		}
	}

	private fun prepareFragment(fragment: FragmentCore<*>) {
		fragment.forceChangeFragment = { changeFragment(it) }
		fragment.forcePopBackStack = { popHistory() }
	}

	fun showIn(@IdRes containerId: Int) {
		this.containerId = containerId
	}

	fun selectFirst() = selectAt(0)
	fun selectMiddle() = selectAt(fragments.size / 2)
	fun selectLast() = selectAt(fragments.size - 1)
	fun selectAt(position: Int) {
		if (startingPosition == position) {
			return
		}

		startingPosition = if (startingPosition == -1) position else startingPosition
		setFragment(fragments[position])
	}

	private fun changeFragment(fragment: FragmentCore<*>) =
		setFragment(fragment.apply { prepareFragment(this) })

	private fun setFragment(fragment: FragmentCore<*>, addToHistory: Boolean = true) {
		if (containerId === null) {
			throw NullPointerException("Fragment container cannot be null.")
		}

		if (fragment === currentFragment) {
			return
		}

		currentFragment = fragment

		fragmentManager.beginTransaction().apply {
			setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
			if (currentFragment !== null) {
				replace(containerId!!, fragment as Fragment)
			} else {
				add(containerId!!, fragment as Fragment)
			}
			commit()
		}

		val fragmentIndex = fragments.indexOfFirst { it === fragment }
		if (fragmentIndex != -1) {
			if (addToHistory) {
				history.remove(fragment)
			}

			bottomNavigation.menu.setGroupCheckable(0, true, true)
		} else {
			bottomNavigation.menu.setGroupCheckable(0, false, true)
		}

		if (addToHistory) {
			history.add(fragment)
		}

		if (fragmentIndex != -1) {
			bottomNavigation.selectedItemId = bottomNavigation.menu[fragmentIndex].itemId
		}
	}

	fun popHistory(): Boolean {
		if (startingPosition == -1 ||
			(history.size <= 1 && currentFragment === fragments[startingPosition])
		) {
			return false
		}

		if (history.size <= 1) {
			setFragment(fragments[startingPosition], false)
			history.removeAt(history.lastIndex)

			return true
		}

		history.removeAt(history.lastIndex)
		setFragment(history.last(), false)

		return true
	}
}
