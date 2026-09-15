package com.dstwrtv.app.streaming.domain.catalog

import com.dstwrtv.app.streaming.domain.model.MediaType

data class CatalogQuery(
    val mediaType: MediaType,
    val page: Int = 1,
    val pageSize: Int = 30,
    val query: String? = null,
    val genreId: Int? = null,
    val year: Int? = null,
    val sort: CatalogSort = CatalogSort.POPULAR
)

enum class CatalogSort {
    POPULAR,
    RATING,
    NEWEST,
    OLDEST,
    TRENDING,
    ALPHABETICAL
}

data class CatalogSection(
    val id: String,
    val title: String,
    val query: CatalogQuery
)
