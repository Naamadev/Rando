package com.amadev.rando.ui.fragments.mainFragment

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amadev.rando.data.ApiClient
import com.amadev.rando.data.ApiService
import com.amadev.rando.model.MovieDetailsResults
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URLEncoder

class MainFragmentViewModel(
    private val apiClient: ApiClient,
    private val firebaseAuth: FirebaseAuth,
    private val firebaseDatabase: FirebaseDatabase
) : ViewModel() {

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

    private val popUpMessageMutableLiveData = MutableLiveData<String>()
    private val currentPage = MutableLiveData<Int>()




    fun getSearchedMovies(query: String) {
        val queryEncoded = encodeUrlString(query)

        viewModelScope.launch(Dispatchers.IO) {
            val response = ApiService(apiClient).searchForMovies(queryEncoded)
            if (response.isSuccessful) {
                response.body()?.let {
                    val results = it.results as ArrayList<MovieDetailsResults>
                    searchedMoviesMutableLiveData.postValue(results)
                    Log.e("searched", query)
                    Log.e("results", results.toString())
                }
            } else if (response.isSuccessful.not()) {
                Log.e("failed", "failed")
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
                popUpMessageMutableLiveData.postValue("Please check internet connection")
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
                        Log.e("results", popularMoviesResultsMutableLiveData.value.toString())

                    }
                }
            } else if (response.isSuccessful.not()) {
                popUpMessageMutableLiveData.postValue("Please check internet connection")
                Log.e("results", "error")

            }

        }
    }
}