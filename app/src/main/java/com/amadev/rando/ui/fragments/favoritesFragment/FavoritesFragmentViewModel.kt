package com.amadev.rando.ui.fragments.favoritesFragment

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amadev.rando.R
import com.amadev.rando.model.MovieDetailsResults
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

sealed class Messages {
    object FailedToLoadFavoriteMovies : Messages()
    object ThereIsNoMoviesHere : Messages()
}

class FavoritesFragmentViewModel(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseDatabase: FirebaseDatabase,
    private val context: Context
) : ViewModel() {

    companion object {
        const val FAVORITE_MOVIES = "favorite movies"
        val failedToLoadFavoriteMovies = Messages.FailedToLoadFavoriteMovies
        val thereIsNoMoviesHere = Messages.ThereIsNoMoviesHere
    }

    private var uuid = provideFirebaseUuiD()

    private val _favoritesMoviesMutableLiveData = MutableLiveData<ArrayList<MovieDetailsResults>>()
    val favoritesMoviesLiveData = _favoritesMoviesMutableLiveData

    private val _popUpMessageMutableLiveData = MutableLiveData<String>()
    val popUpMessageLiveData = _popUpMessageMutableLiveData

    private val _progressBarVisibilityMutableLiveData = MutableLiveData<Boolean>()
    val progressBarLiveData = _progressBarVisibilityMutableLiveData


    fun getFavoriteMovies() {
        val favoriteMoviesList = ArrayList<MovieDetailsResults>()

        val firebaseReference =
            firebaseDatabase.getReference("users")
                .child(uuid)
                .child(FAVORITE_MOVIES)

        val query: Query = firebaseReference
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _progressBarVisibilityMutableLiveData.value = false

                snapshot.children.forEach { data ->
                    data.getValue(MovieDetailsResults::class.java)
                        ?.let { favoriteMoviesList.add(it) }
                }

                if (favoriteMoviesList.isEmpty().not()) {
                    _favoritesMoviesMutableLiveData.value = favoriteMoviesList
                } else {
                    _popUpMessageMutableLiveData.value = getMessages(thereIsNoMoviesHere)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                _progressBarVisibilityMutableLiveData.value = false
                _popUpMessageMutableLiveData.value = getMessages(failedToLoadFavoriteMovies)
            }
        })
    }

    private fun getMessages(messages: Messages) =
        when (messages) {
            is Messages.FailedToLoadFavoriteMovies -> context.getString(R.string.failedToLoadFavoriteMovies)
            is Messages.ThereIsNoMoviesHere -> context.getString(R.string.thereIsNoMoviesHere)
        }

    private fun provideFirebaseUuiD() : String {
        val currentUser = firebaseAuth.currentUser
        var uid = ""
        if (currentUser != null) {
            uid = currentUser.uid
        }
        return uid
    }
}