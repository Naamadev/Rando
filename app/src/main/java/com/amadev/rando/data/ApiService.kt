package com.amadev.rando.data

import com.amadev.rando.BuildConfig
import com.amadev.rando.model.Cast
import com.amadev.rando.model.Genre
import com.amadev.rando.model.PopularMoviesModel
import com.amadev.rando.model.VideoDetailsModel
import retrofit2.Response

class ApiService(private val api: ApiClient) {


    fun getPopularMovie(page: Int): Response<PopularMoviesModel> {
        val callResponse = api.buildService(ApiInterface::class.java)
            .getPopularMoviesByPage(BuildConfig.TMDB_API_KEY, page)
        return callResponse.execute()
    }

    fun getTrailerVideo(movieId: Int?): Response<VideoDetailsModel>? {
        val callResponse = movieId?.let {
            api.buildService(ApiInterface::class.java)
                .getTrailerVideo(it, BuildConfig.TMDB_API_KEY)
        }
        callResponse?.execute().let {
            return it
        }
    }

    fun getCastDetails(movieId: Int?): Response<Cast>? {
        val callResponse = movieId?.let {
            api.buildService(ApiInterface::class.java)
                .getCastDetails(it, BuildConfig.TMDB_API_KEY)
        }
        callResponse?.execute().let {
            return it
        }
    }

    fun getGenreList(): Response<Genre>? {
        val callResponse = api.buildService(ApiInterface::class.java)
            .getGenreList(BuildConfig.TMDB_API_KEY)
        callResponse.execute().let {
            return it
        }
    }

}
