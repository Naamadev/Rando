package com.amadev.rando.ui.fragments.signinFragment

import android.content.Context
import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amadev.rando.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Dispatcher

sealed class Messages {
    object EmptyField : Messages()
    object InvalidEmail : Messages()
    object VerifyEmailSent : Messages()
    object LoginFailed : Messages()
}

class SignInViewModel(
    private val context: Context,
    private var auth: FirebaseAuth,
    private val firebaseDatabase: FirebaseDatabase
) : ViewModel() {

    companion object {
        val emptyField = Messages.EmptyField
        val invalidEmail = Messages.InvalidEmail
        val verifyEmailSent = Messages.VerifyEmailSent
        val loginFailed = Messages.LoginFailed
    }

    private var uuid = provideFirebaseUuiD()

    private val loginSuccessfulMutableLiveData = MutableLiveData<Boolean>()
    val loginSuccessfulLiveData = loginSuccessfulMutableLiveData

    private val messageMutableLiveData = MutableLiveData<String>()
    val messageLiveData = messageMutableLiveData

    private val setUpProgressbarVisibilityMutableLiveData = MutableLiveData<Boolean>()
    val setUpProgressbarVisibility = setUpProgressbarVisibilityMutableLiveData

    private val loginAutomaticallyIfPossibleMutableLiveData = MutableLiveData<Boolean>()
    val loginAutomaticallyIfPossibleLiveData = loginAutomaticallyIfPossibleMutableLiveData

    private val errorMessageMutableLiveData = MutableLiveData<String>()
    val errorMessageLiveData = errorMessageMutableLiveData


    fun validateInput(username: String, password: String) {
        username.trim()
        password.trim()
        when {
            username.isEmpty() || password.isEmpty() ->
                errorMessageLiveData.value = getMessage(emptyField)
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
                .not() -> errorMessageLiveData.value = getMessage(invalidEmail)
            else -> doLoginWithEmailAndPassword(username, password)
        }
    }

    private fun doLoginWithEmailAndPassword(username: String, password: String) {
        auth = FirebaseAuth.getInstance()
        username.trim()
        password.trim()
        setUpProgressbarVisibilityMutableLiveData.value = true
        auth.signInWithEmailAndPassword(username, password)
            .addOnSuccessListener {
                if (auth.currentUser != null) {
                    if (auth.currentUser!!.isEmailVerified.not()) {
                        loginSuccessfulMutableLiveData.value = false
                        messageMutableLiveData.value = getMessage(verifyEmailSent)
                        setUpProgressbarVisibilityMutableLiveData.value = false
                    } else {
                        setUpProgressbarVisibilityMutableLiveData.value = false
                        loginSuccessfulMutableLiveData.value = true
                    }
                }
            }
            .addOnFailureListener {
                messageMutableLiveData.value = getMessage(loginFailed)
                setUpProgressbarVisibilityMutableLiveData.value = false
            }
    }

    fun loginAutomaticallyIfPossible() {
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if (currentUser != null && currentUser.isEmailVerified) {
            loginSuccessfulMutableLiveData.value = true
        }
    }

    private fun provideFirebaseUuiD(): String {
        val currentUser = auth.currentUser
        var uid = ""
        if (currentUser != null) {
            uid = currentUser.uid
        }
        return uid
    }

    private fun getMessage(message: Messages) =
        when (message) {
            is Messages.EmptyField -> context.getString(R.string.field_cannot_be_empty)
            is Messages.InvalidEmail -> context.getString(R.string.pleaseEnterValidEmailAdress)
            is Messages.VerifyEmailSent -> context.getString(R.string.pleaseVerifyEmailSent)
            is Messages.LoginFailed -> context.getString(R.string.failedToLogIn)
        }
}