package com.pointlessapps.trackr.viewModels

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.pointlessapps.trackr.R
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ViewModelMain(application: Application) : AndroidViewModel(application) {

	private val _googleUser = MutableLiveData(GoogleSignIn.getLastSignedInAccount(application))
	val googleUser: LiveData<GoogleSignInAccount?> get() = _googleUser

	private val _isSignedIn = MutableLiveData(Firebase.auth.currentUser != null)
	val isSignedIn: LiveData<Boolean> get() = _isSignedIn

	private val googleSignInClient = GoogleSignIn.getClient(
		application, GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
			.requestIdToken(application.getString(R.string.default_web_client_id))
			.requestProfile()
			.requestEmail()
			.build()
	)

	fun isSignedInToFirebase() = Firebase.auth.currentUser != null
	fun getSignInIntent() = googleSignInClient.signInIntent

	fun signInFirebaseFromIntent(data: Intent) {
		viewModelScope.launch {
			runCatching {
				val account = GoogleSignIn.getSignedInAccountFromIntent(data).await()
				_googleUser.postValue(account)
				val credential = GoogleAuthProvider.getCredential(account.idToken, null)
				val result = runCatching {
					Firebase.auth.currentUser!!.linkWithCredential(credential).await()
				}
				if (result.isFailure) {
					Firebase.auth.signInWithCredential(credential).await()
				}
				_isSignedIn.postValue(Firebase.auth.currentUser != null)
			}
		}
	}

	fun signOut() {
		viewModelScope.launch {
			googleSignInClient.signOut().await()
			_googleUser.postValue(null)
			signInAnonymously()
		}
	}

	fun signInAnonymously() {
		viewModelScope.launch {
			val result = Firebase.auth.signInAnonymously().await()
			_isSignedIn.postValue(result.user != null)
		}
	}
}