package com.amadev.rando.ui.fragments.movieDetailsFragment

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amadev.rando.R
import com.amadev.rando.data.ApiClient
import com.amadev.rando.data.ApiService
import com.amadev.rando.model.CastModelResults
import com.amadev.rando.model.MovieDetailsResults
import com.amadev.rando.util.Util.replaceFirebaseForbiddenChars
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

sealed class Messages {
    object FailedToLoadFavoriteMovies : Messages()
    object FailedAddingToFavorites : Messages()
    object AddedToFavorites : Messages()
    object MovieRemovedFromFavorites : Messages()
    object FailedToRemoveMovie : Messages()
    object YouMustBeLoggedIn : Messages()
}

class MovieDetailsViewModel(
    val context: Context,
    private val apiClient: ApiClient,
    private val firebaseAuth: FirebaseAuth,
    private val firebaseDatabase: FirebaseDatabase
) : ViewModel() {

    companion object {
        const val FAVORITE_MOVIES = "favorite movies"
        val failedAddingToFavorites = Messages.FailedAddingToFavorites
        val addedToFavorites = Messages.AddedToFavorites
        val movieRemovedFromFavorites = Messages.MovieRemovedFromFavorites
        val failedToRemoveMovie = Messages.FailedToRemoveMovie
        val youMustBeLoggedIn = Messages.YouMustBeLoggedIn
    }

    private val _movieDetailsMutableLiveData = MutableLiveData<MovieDetailsResults?>()
    val movieDetailsMutableLiveData = _movieDetailsMutableLiveData

    private val _videoEndPoint = MutableLiveData<String>()
    val videoEndPoint = _videoEndPoint

    private val _castList = MutableLiveData<List<CastModelResults>>()
    val castList = _castList

    private val _popUpMessageMutableLiveData = MutableLiveData<String>()
    val popUpMessageMutableLiveData = _popUpMessageMutableLiveData

    private val _favoriteMoviesMutableLiveData = MutableLiveData<ArrayList<MovieDetailsResults?>>()
    val favoriteMoviesMutableLiveData = _favoriteMoviesMutableLiveData

    private val _isUserLoggedInMutableLiveData = MutableLiveData<Boolean>()
    var isUserLoggedIn = _isUserLoggedInMutableLiveData

    private var uuid = provideFirebaseUuiD()


    fun setUpMovieDetails(results: MovieDetailsResults?) {
        if (results != null) {
            _movieDetailsMutableLiveData.value = results
            results.id.let {
                getTrailerVideo(it)
                getCastDetails(it)
            }
        }
    }

    private fun getTrailerVideo(movieId : Int?) {
        viewModelScope.launch(Dispatchers.IO) {
            val response =
                ApiService(apiClient).getTrailerVideo(movieId)
            response?.let {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.results?.let {
                        if (it.isNotEmpty()) {
                            _videoEndPoint.postValue(it.let {
                                it
                                    .last()
                                    .key
                                    .toString()
                            })
                        }
                    }
                }
            }
        }
    }

    private fun getCastDetails(movieId : Int?) {
        val castList = ArrayList<CastModelResults>()
        viewModelScope.launch(Dispatchers.IO) {
            val response =
                ApiService(apiClient).getCastDetails(movieId)
            response?.let {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.let {
                        it.cast.forEach { results ->
                            if (results.profile_path.isNullOrEmpty().not()) {
                                castList.add(results)
                            }
                        }
                        _castList.postValue(castList)
                    }
                }
            }
        }
    }

    fun isUserLoggedIn() {
        _isUserLoggedInMutableLiveData.value = firebaseAuth.currentUser != null
    }

    fun removeCurrentMovieFromFavoriteMovies(movieId: Int?) {
        val firebaseReference =
            firebaseDatabase.getReference("users")
                .child(uuid)
                .child(FAVORITE_MOVIES)
                .child(movieId.toString())

        firebaseReference.removeValue()
            .addOnSuccessListener {
                popUpMessageMutableLiveData.value = getMessage(movieRemovedFromFavorites)
            }

            .addOnFailureListener {
                popUpMessageMutableLiveData.value = getMessage(failedToRemoveMovie)
            }
    }

    fun addCurrentMovieToFavoriteMovies() {
        val firebaseReference =
            firebaseDatabase.getReference("users")
                .child(uuid)
                .child(FAVORITE_MOVIES)

        firebaseReference
            .child(_movieDetailsMutableLiveData.value?.id.toString())
            .setValue(_movieDetailsMutableLiveData.value)
            .addOnSuccessListener {
                popUpMessageMutableLiveData.value = getMessage(addedToFavorites)
            }
            .addOnFailureListener {
                popUpMessageMutableLiveData.value = getMessage(failedAddingToFavorites)

            }
    }

    fun getFavoriteMovies() {
        val firebaseReference =
            firebaseDatabase.getReference("users")
                .child(uuid)
                .child(FAVORITE_MOVIES)

        val query: Query = firebaseReference
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val favoriteMoviesList = ArrayList<MovieDetailsResults?>()

                snapshot.children.forEach { data ->
                    val list = data.getValue(MovieDetailsResults::class.java)
                    favoriteMoviesList.add(list)
                }
                _favoriteMoviesMutableLiveData.value = favoriteMoviesList
            }

            override fun onCancelled(error: DatabaseError) {
                _popUpMessageMutableLiveData.value = getMessage(failedAddingToFavorites)
            }
        })
    }

    private fun provideFirebaseUuiD() : String {
        val currentUser = firebaseAuth.currentUser
        var uid = ""
        if (currentUser != null) {
            uid = currentUser.uid
        }
        return uid
}

    private fun getMessage(message: Messages) =
        when (message) {
            is Messages.AddedToFavorites -> context.getString(R.string.addedToFavorites)
            is Messages.FailedAddingToFavorites -> context.getString(R.string.failedAddingToFavorites)
            is Messages.FailedToLoadFavoriteMovies -> context.getString(R.string.failedToLoadFavoriteMovies)
            is Messages.MovieRemovedFromFavorites -> context.getString(R.string.movieRemovedFromFavorites)
            is Messages.FailedToRemoveMovie -> context.getString(R.string.failedToRemoveMovie)
            is Messages.YouMustBeLoggedIn -> context.getString(R.string.youMustBeLoggedIn)
        }
}
