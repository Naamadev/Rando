package com.amadev.rando.ui.fragments.mainFragment

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amadev.rando.R
import com.amadev.rando.data.ApiClient
import com.amadev.rando.data.ApiService
import com.amadev.rando.model.MovieDetailsResults
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URLEncoder

sealed class Messages {
    object SomethingWentWrong : Messages()
}
class MainFragmentViewModel(
    private val auth : FirebaseAuth,
    private val context: Context,
    private val apiClient: ApiClient
    ) : ViewModel() {

    companion object {
        val somethingWentWrong = Messages.SomethingWentWrong
    }

    private val upComingMoviesResultsMutableLiveData =
        MutableLiveData<ArrayList<MovieDetailsResults>>()
    val upcomingMoviesResultsLiveData = upComingMoviesResultsMutableLiveData

    private val topRatedMoviesResultsMutableLiveData =
        MutableLiveData<ArrayList<MovieDetailsResults>>()
    val topRatedMoviesResultsLiveData = topRatedMoviesResultsMutableLiveData

    private val popularMoviesResultsMutableLiveData =
        MutableLiveData<ArrayList<MovieDetailsResults>>()
    val popularMoviesResultsLiveData = popularMoviesResultsMutableLiveData

    private val nowPlayingMoviesMutableLiveData =
        MutableLiveData<ArrayList<MovieDetailsResults>>()
    val nowPlayingMoviesLiveData = nowPlayingMoviesMutableLiveData

    private val searchedMoviesMutableLiveData =
        MutableLiveData<ArrayList<MovieDetailsResults>>()
    val searchedMoviesLiveData = searchedMoviesMutableLiveData

    private val _isUserLoggedInMutableLiveData = MutableLiveData<Boolean>()
    val isUserLoggedIn = _isUserLoggedInMutableLiveData

    private val popUpMessageMutableLiveData = MutableLiveData<String>()

    fun getSearchedMovies(query: String) {
        val queryEncoded = encodeUrlString(query)

        viewModelScope.launch(Dispatchers.IO) {
            val response = ApiService(apiClient).searchForMovies(queryEncoded)
            if (response.isSuccessful) {
                response.body()?.let {
                    val results = it.results as ArrayList<MovieDetailsResults>
                    searchedMoviesMutableLiveData.postValue(results)
                }

            } else if (response.isSuccessful.not()) {
                popUpMessageMutableLiveData.postValue(getMessage(somethingWentWrong))
            }
        }
    }

    private fun encodeUrlString(string: String): String {
        return URLEncoder.encode(string, "utf-8")
    }

    fun getNowPlayingMovies() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = ApiService(apiClient).getNowPlayingMovies(1)
            if (response.isSuccessful) {
                response.body()?.let {
                    val results = it.results.reversed() as ArrayList<MovieDetailsResults>
                    nowPlayingMoviesMutableLiveData.postValue(results)
                }
            }
        }
    }

    fun getUpcomingMovies() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = ApiService(apiClient).getUpcomingMovie(1)
            if (response.isSuccessful) {
                response.body()?.let {
                    val results = it.results as ArrayList<MovieDetailsResults>
                    upComingMoviesResultsMutableLiveData.postValue(results)
                }
            }
        }
    }

    fun getTopRatedMovies() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = ApiService(apiClient).getTopRatedMovie(1)
            if (response.isSuccessful) {
                val responseBody = response.body()
                responseBody?.let {
                    val results = responseBody.results as ArrayList<MovieDetailsResults>
                        topRatedMoviesResultsMutableLiveData.postValue(results)
                }
            } else {
                popUpMessageMutableLiveData.postValue(getMessage(somethingWentWrong))
            }

        }
    }

    fun getPopularMovies() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = ApiService(apiClient).getPopularMovie(1)
            if (response.isSuccessful) {
                val responseBody = response.body()

                responseBody?.let {
                    val results = responseBody.results as ArrayList<MovieDetailsResults>
                    results.apply {
                        popularMoviesResultsMutableLiveData.postValue(results)
                    }
                }
            } else if (response.isSuccessful.not()) {
                popUpMessageMutableLiveData.postValue(getMessage(somethingWentWrong))
            }
        }
    }

    private fun getMessage(messages: Messages) =
        when (messages) {
            is Messages.SomethingWentWrong -> context.getString(R.string.somethingWentWrong)
        }

    fun isUserLoggedIn() {
        _isUserLoggedInMutableLiveData.value = auth.currentUser != null
    }
}