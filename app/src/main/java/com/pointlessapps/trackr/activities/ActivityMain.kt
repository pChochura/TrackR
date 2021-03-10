package com.pointlessapps.trackr.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.pointlessapps.trackr.R
import com.pointlessapps.trackr.databinding.ActivityMainBinding
import com.pointlessapps.trackr.dialogs.DialogProfile
import com.pointlessapps.trackr.fragments.FragmentCalendar
import com.pointlessapps.trackr.fragments.FragmentHome
import com.pointlessapps.trackr.fragments.FragmentInsights
import com.pointlessapps.trackr.managers.FragmentManager
import com.pointlessapps.trackr.viewModels.ViewModelMain

class ActivityMain : AppCompatActivity() {

	private val viewModel by viewModels<ViewModelMain>()
	private lateinit var fragmentManager: FragmentManager

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		val binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)

		prepareNavigation(binding)
		prepareButtons(binding)
	}

	private fun prepareNavigation(binding: ActivityMainBinding) {
		fragmentManager = FragmentManager.of(
			supportFragmentManager,
			binding.navigationBottom,
			FragmentInsights(),
			FragmentHome(),
			FragmentCalendar()
		)

		with(fragmentManager) {
			showIn(R.id.containerFragment)
			selectMiddle()
		}
	}

	private fun prepareButtons(binding: ActivityMainBinding) {
		binding.buttonProfile.setOnClickListener {
			DialogProfile(this).show().setOnLogoutClickListener {

			}
		}
	}

	override fun onBackPressed() {
		if (!fragmentManager.popHistory()) {
			super.onBackPressed()
		}
	}
}