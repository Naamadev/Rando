package com.amadev.rando.ui.fragments.signupFragment

import android.content.Context
import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amadev.rando.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

sealed class Messages {
    object EmptyField : Messages()
    object InvalidEmail : Messages()
    object VerifyEmailSent : Messages()
    object SignInFailed : Messages()
    object PasswordsNotSame : Messages()
    object UserCreated : Messages()
    object EmailAlreadyExist : Messages()
    object PasswordToWeak : Messages()
}

class SignUpViewModel(private val context: Context, private val auth: FirebaseAuth) : ViewModel() {

    companion object {
        val emptyField = Messages.EmptyField
        val invalidEmail = Messages.InvalidEmail
        val verifyEmailSent = Messages.VerifyEmailSent
        val signInFailed = Messages.SignInFailed
        val passwordNotSame = Messages.PasswordsNotSame
        val emailAlreadyExists = Messages.EmailAlreadyExist
        val passwordToWeak = Messages.PasswordToWeak
    }

    private val popUpTextMutableLiveData = MutableLiveData<String>()
    val popUpTextLiveData = popUpTextMutableLiveData

    private val progressBarVisibleMutableLiveData = MutableLiveData<Boolean>()
    val progressBarVisibleLiveData = progressBarVisibleMutableLiveData

    private val accountSuccessfullyCreatedMutableLiveData = MutableLiveData<Boolean>()
    val accountSuccessfullyCreatedLiveData = accountSuccessfullyCreatedMutableLiveData


    fun validateInput(username: String, password: String, repeatPassword: String) {
        username.trim()
        password.trim()
        repeatPassword.trim()

        when {
            username.isEmpty() -> popUpTextMutableLiveData.value = getMessage(emptyField)
            password.isEmpty() -> popUpTextMutableLiveData.value = getMessage(emptyField)
            repeatPassword.isEmpty() -> popUpTextMutableLiveData.value =
                getMessage(emptyField)
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
                .not() -> popUpTextMutableLiveData.value = getMessage(invalidEmail)
            password != repeatPassword -> popUpTextMutableLiveData.value =
                getMessage(passwordNotSame)
            else -> createUserWithEmailAndPassword(username, password)
        }
    }

    private fun createUserWithEmailAndPassword(username: String, password: String) {
        progressBarVisibleMutableLiveData.value = true
        auth.createUserWithEmailAndPassword(username, password).addOnCompleteListener {
            if (it.isSuccessful) {
                sendVerificationEmail()
                progressBarVisibleMutableLiveData.value = false
                accountSuccessfullyCreatedMutableLiveData.value = true
                popUpTextMutableLiveData.value = getMessage(verifyEmailSent)

            } else if (it.isSuccessful.not()) {
                try {
                    throw it.exception!!
                } catch (e: FirebaseAuthWeakPasswordException) {
                    popUpTextMutableLiveData.value = getMessage(passwordToWeak)
                } catch (e: FirebaseAuthUserCollisionException) {
                    popUpTextMutableLiveData.value = getMessage(emailAlreadyExists)
                } catch (e: Exception) {
                    popUpTextMutableLiveData.value = getMessage(signInFailed)
                }
                progressBarVisibleMutableLiveData.value = false
            }
        }
    }

    private fun sendVerificationEmail() {
        val user = FirebaseAuth.getInstance().currentUser
        user!!.sendEmailVerification()
    }

    private fun getMessage(message: Messages) =
        when (message) {
            is Messages.EmptyField -> context.getString(R.string.field_cannot_be_empty)
            is Messages.InvalidEmail -> context.getString(R.string.pleaseEnterValidEmailAdress)
            is Messages.VerifyEmailSent -> context.getString(R.string.verificationEmailSent)
            is Messages.SignInFailed -> context.getString(R.string.failedToSignUp)
            is Messages.PasswordsNotSame -> context.getString(R.string.passwordsAreNotSame)
            is Messages.UserCreated -> context.getString(R.string.userCreatedSuccessfully)
            is Messages.EmailAlreadyExist -> context.getString(R.string.emailAlreadyExists)
            is Messages.PasswordToWeak -> context.getString(R.string.passwordIsToWeak)
        }
}