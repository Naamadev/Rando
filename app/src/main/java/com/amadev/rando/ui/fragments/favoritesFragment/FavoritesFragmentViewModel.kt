package com.amadev.rando.ui.fragments.favoritesFragment

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amadev.rando.R
import com.amadev.rando.model.MovieDetailsResults
import com.amadev.rando.util.Util
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
        const val FAVORITE_MOVIES = "Favorite movies"
        val failedToLoadFavoriteMovies = Messages.FailedToLoadFavoriteMovies
        val thereIsNoMoviesHere = Messages.ThereIsNoMoviesHere
    }

    private val username = provideFirebaseUsername()

    private val _favoritesMoviesMutableLiveData = MutableLiveData<ArrayList<MovieDetailsResults>>()
    val favoritesMoviesLiveData = _favoritesMoviesMutableLiveData

    private val _popUpMessageMutableLiveData = MutableLiveData<String>()
    val popUpMessageLiveData = _popUpMessageMutableLiveData

    private val _progressBarVisibilityMutableLiveData = MutableLiveData<Boolean>()
    val progressBarLiveData = _progressBarVisibilityMutableLiveData


    fun getFavoriteMovies() {
        val firebaseReference =
            firebaseDatabase.getReference("Users")
                .child(Util.replaceFirebaseForbiddenChars(username))
                .child(FAVORITE_MOVIES)

        val query: Query = firebaseReference
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _progressBarVisibilityMutableLiveData.value = false

                val favoriteMoviesList = ArrayList<MovieDetailsResults>()
                snapshot.children.forEach { data ->
                    data.getValue(MovieDetailsResults::class.java)
                        ?.let { favoriteMoviesList.add(it) }
                }

                if (favoriteMoviesList.isEmpty().not()) {
                    when {
                        favoriteMoviesList.size > 1 -> {
                            _favoritesMoviesMutableLiveData.value =
                                favoriteMoviesList.reversed() as ArrayList<MovieDetailsResults>
                        }
                        else -> {
                            _favoritesMoviesMutableLiveData.value = favoriteMoviesList
                        }
                    }
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

    private fun provideFirebaseUsername(): String {
        val currentUser = firebaseAuth.currentUser
        lateinit var username: String
        currentUser?.let {
            for (profiler in it.providerData) {
                username = profiler.email.toString()
            }
        }
        return username
    }
}