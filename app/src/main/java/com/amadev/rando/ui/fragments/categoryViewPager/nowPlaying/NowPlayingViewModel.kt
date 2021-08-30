package com.amadev.rando.ui.fragments.categoryViewPager.nowPlaying

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amadev.rando.data.ApiClient
import com.amadev.rando.data.ApiService
import com.amadev.rando.model.MovieDetailsResults
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NowPlayingViewModel(private val apiClient: ApiClient) : ViewModel() {

    private val nowPlayingMoviesResultsMutableLiveData =
        MutableLiveData<ArrayList<MovieDetailsResults>>()
    val nowPlayingMoviesResultsLiveData = nowPlayingMoviesResultsMutableLiveData

    fun getPopularMovies() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = ApiService(apiClient).getNowPlayingMovies(1)
            if (response.isSuccessful) {
                response.body()?.let {
                    val results = it.results as ArrayList<MovieDetailsResults>
                    nowPlayingMoviesResultsMutableLiveData.postValue(results)
                }
            }
        }
    }
}