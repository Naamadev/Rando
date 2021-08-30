package com.amadev.rando.ui.fragments.categoryViewPager.popularFragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amadev.rando.data.ApiClient
import com.amadev.rando.data.ApiService
import com.amadev.rando.model.MovieDetailsResults
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PopularFragmentViewModel(private val apiClient: ApiClient) : ViewModel() {

    private val popularMoviesResultsMutableLiveData =
        MutableLiveData<ArrayList<MovieDetailsResults>>()
    val popularMoviesResultsLiveData = popularMoviesResultsMutableLiveData

    fun getPopularMovies() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = ApiService(apiClient).getPopularMovie(1)
            if (response.isSuccessful) {
                response.body()?.let {
                    val results = it.results as ArrayList<MovieDetailsResults>
                    popularMoviesResultsMutableLiveData.postValue(results)
                }
            }
        }
    }
}