package com.dstwrtv.app.streaming.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApi {
    @GET("movie/popular")
    suspend fun popularMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "ar-SA",
        @Query("page") page: Int = 1
    ): TmdbPageDto<TmdbMovieDto>

    @GET("tv/popular")
    suspend fun popularTvShows(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "ar-SA",
        @Query("page") page: Int = 1
    ): TmdbPageDto<TmdbTvDto>

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("language") language: String = "ar-SA",
        @Query("page") page: Int = 1,
        @Query("include_adult") includeAdult: Boolean = false
    ): TmdbPageDto<TmdbMovieDto>

    @GET("search/tv")
    suspend fun searchTvShows(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("language") language: String = "ar-SA",
        @Query("page") page: Int = 1,
        @Query("include_adult") includeAdult: Boolean = false
    ): TmdbPageDto<TmdbTvDto>

    @GET("movie/{id}")
    suspend fun movieDetails(
        @Path("id") id: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "ar-SA"
    ): TmdbMovieDto

    @GET("tv/{id}")
    suspend fun tvDetails(
        @Path("id") id: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "ar-SA"
    ): TmdbTvDto

    @GET("tv/{id}/season/{season}")
    suspend fun seasonDetails(
        @Path("id") tvId: Int,
        @Path("season") seasonNumber: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "ar-SA"
    ): TmdbSeasonDto
}
