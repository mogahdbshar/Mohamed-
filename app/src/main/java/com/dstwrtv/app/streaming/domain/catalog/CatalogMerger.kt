package com.dstwrtv.app.streaming.domain.catalog

import com.dstwrtv.app.streaming.domain.model.Movie
import com.dstwrtv.app.streaming.domain.model.TvShow

object CatalogMerger {
    fun movies(items: List<Movie>): List<Movie> = items
        .groupBy { identity(it.provider, it.providerId, it.title, it.releaseDate) }
        .values
        .map { group -> group.maxWithOrNull(compareBy<Movie> { it.voteCount }.thenBy { it.rating }) ?: group.first() }

    fun tvShows(items: List<TvShow>): List<TvShow> = items
        .groupBy { identity(it.provider, it.providerId, it.name, it.firstAirDate) }
        .values
        .map { group -> group.maxWithOrNull(compareBy<TvShow> { it.voteCount }.thenBy { it.rating }) ?: group.first() }

    private fun identity(provider: String, providerId: String?, title: String, date: String?): String {
        if (!providerId.isNullOrBlank()) return "$provider:$providerId"
        return "title:${title.trim().lowercase()}:${date.orEmpty().take(4)}"
    }
}
