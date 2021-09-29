package com.pointlessapps.trackr.activities

import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.pointlessapps.trackr.R
import com.pointlessapps.trackr.databinding.ActivityMainBinding
import com.pointlessapps.trackr.dialogs.DialogProfile
import com.pointlessapps.trackr.viewModels.ViewModelMain

class ActivityMain : AppCompatActivity() {

	private val viewModel by viewModels<ViewModelMain>()
	private val intentLauncher =
		registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
			viewModel.signInFirebaseFromIntent(it.data ?: return@registerForActivityResult)
		}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		val binding = ActivityMainBinding.inflate(layoutInflater)
//		AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//		delegate.applyDayNight()
		setContentView(binding.root)

		if (!viewModel.isSignedInToFirebase()) {
			viewModel.signInAnonymously()
		}

		prepareNavigation(binding)
		prepareButtons(binding)
	}

	private fun prepareNavigation(binding: ActivityMainBinding) {
		val navController = findNavController(R.id.containerFragment)
		binding.navigationBottom.setupWithNavController(navController)
//		binding.navigationBottom.setOnNavigationItemSelectedListener itemSelectedListener@{
//			if (it.itemId == R.id.home) {
//				return@itemSelectedListener navController.navigateUp()
//			}
//
//			return@itemSelectedListener NavigationUI.onNavDestinationSelected(it, navController)
//		}
	}

	private fun prepareButtons(binding: ActivityMainBinding) {
		binding.buttonProfile.setOnClickListener {
			DialogProfile(this, viewModel.googleUser).show()
				.setOnLoginClickListener {
					intentLauncher.launch(viewModel.getSignInIntent())
				}.setOnLogoutClickListener(viewModel::signOut)
		}

		binding.buttonMenu.setOnClickListener {
			findNavController(R.id.containerFragment).navigate(R.id.activities)
		}
	}
}