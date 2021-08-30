package com.amadev.rando.data

import com.amadev.rando.model.*
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiInterface {

    @GET("search/movie")
    fun searchForMovies(
        @Query("api_key") key: String,
        @Query("query") query: String
    ): Call<MovieDetailsModel>

    @GET("movie/now_playing")
    fun getNowPlayingMovies(
        @Query("api_key") key: String,
        @Query("page") page : Int
    ): Call<MovieDetailsModel>

    @GET("movie/upcoming")
    fun getUpcomingMoviesByPage(
        @Query("api_key") key: String,
        @Query("page") page: Int
    ): Call<MovieDetailsModel>

    @GET("movie/top_rated")
    fun getTopRatedMoviesByPage(
        @Query("api_key") key: String,
        @Query("page") page: Int
    ): Call<MovieDetailsModel>

    @GET("movie/popular")
    fun getPopularMoviesByPage(
        @Query("api_key") key: String,
        @Query("page") page: Int
    ): Call<MovieDetailsModel>

    @GET("movie/{movie_id}/videos")
    fun getTrailerVideo(
        @Path("movie_id") movieId: Int,
        @Query("api_key") key: String,
    ): Call<VideoDetailsModel>

    @GET("movie/{movie_id}/credits")
    fun getCastDetails(
        @Path("movie_id") movieId: Int,
        @Query("api_key") key: String,
    ): Call<Cast>

    @GET("genre/movie/list")
    fun getGenreList(@Query("api_key") key: String) : Call<Genre>

}