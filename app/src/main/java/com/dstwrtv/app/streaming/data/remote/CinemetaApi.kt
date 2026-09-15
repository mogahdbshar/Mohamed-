package com.dstwrtv.app.streaming.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CinemetaApi {
    @GET("catalog/{type}/{catalogId}.json")
    suspend fun catalog(
        @Path("type") type: String,
        @Path("catalogId") catalogId: String = "top",
        @Query("search") search: String? = null,
        @Query("skip") skip: Int? = null
    ): CinemetaCatalogResponse

    @GET("meta/{type}/{id}.json")
    suspend fun meta(
        @Path("type") type: String,
        @Path("id") id: String
    ): CinemetaMetaResponse
}
