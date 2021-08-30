package com.amadev.rando.ui.fragments.categoryViewPager.topRated

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amadev.rando.data.ApiClient
import com.amadev.rando.data.ApiService
import com.amadev.rando.model.MovieDetailsResults
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TopRatedFragmentViewModel(private val apiClient: ApiClient) : ViewModel() {

    private val topRatedMoviesResultsMutableLiveData =
        MutableLiveData<ArrayList<MovieDetailsResults>>()
    val topRatedMoviesResultsLiveData = topRatedMoviesResultsMutableLiveData

    fun getTopRatedMovies() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = ApiService(apiClient).getTopRatedMovie(1)
            if (response.isSuccessful) {
                response.body()?.let {
                    val results = it.results as ArrayList<MovieDetailsResults>
                    topRatedMoviesResultsMutableLiveData.postValue(results)
                }
            }
        }
    }
}