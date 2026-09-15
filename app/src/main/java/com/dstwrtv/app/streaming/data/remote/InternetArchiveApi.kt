package com.dstwrtv.app.streaming.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface InternetArchiveApi {
    @GET("advancedsearch.php")
    suspend fun search(
        @Query("q") query: String,
        @Query("fl[]") fields: List<String> = listOf("identifier,title,year"),
        @Query("rows") rows: Int = 20,
        @Query("page") page: Int = 1,
        @Query("output") output: String = "json"
    ): InternetArchiveSearchResponse

    @GET("metadata/{identifier}")
    suspend fun metadata(@retrofit2.http.Path("identifier") identifier: String): InternetArchiveMetadataResponse
}

data class InternetArchiveSearchResponse(val response: InternetArchiveSearchResult?)
data class InternetArchiveSearchResult(val docs: List<InternetArchiveDoc> = emptyList())
data class InternetArchiveDoc(val identifier: String? = null, val title: String? = null, val year: Int? = null)
data class InternetArchiveMetadataResponse(val files: List<InternetArchiveFile> = emptyList())
data class InternetArchiveFile(val name: String? = null, val format: String? = null, val size: String? = null)
