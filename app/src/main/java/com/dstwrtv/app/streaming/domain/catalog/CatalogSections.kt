package com.dstwrtv.app.streaming.domain.catalog

import com.dstwrtv.app.streaming.domain.model.MediaType

object CatalogSections {
    fun movieDefaults(): List<CatalogSection> = listOf(
        CatalogSection("movies_trending", "Trending Movies", CatalogQuery(MediaType.MOVIE, sort = CatalogSort.TRENDING)),
        CatalogSection("movies_popular", "Popular Movies", CatalogQuery(MediaType.MOVIE, sort = CatalogSort.POPULAR)),
        CatalogSection("movies_rated", "Top Rated Movies", CatalogQuery(MediaType.MOVIE, sort = CatalogSort.RATING)),
        CatalogSection("movies_new", "New Movies", CatalogQuery(MediaType.MOVIE, sort = CatalogSort.NEWEST))
    )

    fun tvDefaults(): List<CatalogSection> = listOf(
        CatalogSection("tv_trending", "Trending Series", CatalogQuery(MediaType.TV, sort = CatalogSort.TRENDING)),
        CatalogSection("tv_popular", "Popular Series", CatalogQuery(MediaType.TV, sort = CatalogSort.POPULAR)),
        CatalogSection("tv_rated", "Top Rated Series", CatalogQuery(MediaType.TV, sort = CatalogSort.RATING)),
        CatalogSection("tv_new", "New Series", CatalogQuery(MediaType.TV, sort = CatalogSort.NEWEST))
    )
}
