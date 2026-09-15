package com.dstwrtv.app.streaming.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TvMazeApi {
    @GET("shows")
    suspend fun shows(
        @Query("page") page: Int? = null
    ): List<TvMazeShowDto>

    @GET("search/shows")
    suspend fun searchShows(
        @Query("q") query: String
    ): List<TvMazeSearchResultDto>

    @GET("shows/{id}")
    suspend fun show(
        @Path("id") id: Int,
        @Query("embed") embed: String = "episodes"
    ): TvMazeShowDto
}
