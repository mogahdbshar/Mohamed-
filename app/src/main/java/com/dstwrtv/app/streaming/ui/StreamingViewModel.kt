package com.dstwrtv.app.streaming.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dstwrtv.app.streaming.StreamingContainer
import com.dstwrtv.app.streaming.domain.model.CatalogPage
import com.dstwrtv.app.streaming.domain.model.Episode
import com.dstwrtv.app.streaming.domain.model.MediaType
import com.dstwrtv.app.streaming.domain.model.Movie
import com.dstwrtv.app.streaming.domain.model.Season
import com.dstwrtv.app.streaming.domain.model.TvShow
import com.dstwrtv.app.streaming.domain.source.SourceDiscoveryRequest
import com.dstwrtv.app.streaming.domain.source.SourceDiscoveryResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StreamingViewModel : ViewModel() {
    private val engine = StreamingContainer.metadataEngine
    private val sourceEngine = StreamingContainer.sourceDiscoveryEngine

    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> = _movies.asStateFlow()

    private val _shows = MutableStateFlow<List<TvShow>>(emptyList())
    val shows: StateFlow<List<TvShow>> = _shows.asStateFlow()

    private val _searchMovies = MutableStateFlow<List<Movie>>(emptyList())
    val searchMovies: StateFlow<List<Movie>> = _searchMovies.asStateFlow()

    private val _searchShows = MutableStateFlow<List<TvShow>>(emptyList())
    val searchShows: StateFlow<List<TvShow>> = _searchShows.asStateFlow()

    private val _selectedMovie = MutableStateFlow<Movie?>(null)
    val selectedMovie: StateFlow<Movie?> = _selectedMovie.asStateFlow()

    private val _selectedShow = MutableStateFlow<TvShow?>(null)
    val selectedShow: StateFlow<TvShow?> = _selectedShow.asStateFlow()

    private val _seasons = MutableStateFlow<List<Season>>(emptyList())
    val seasons: StateFlow<List<Season>> = _seasons.asStateFlow()

    private val _episodes = MutableStateFlow<List<Episode>>(emptyList())
    val episodes: StateFlow<List<Episode>> = _episodes.asStateFlow()

    private val _sourceResult = MutableStateFlow<SourceDiscoveryResult?>(null)
    val sourceResult: StateFlow<SourceDiscoveryResult?> = _sourceResult.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    init { refreshCatalog() }

    fun refreshCatalog() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            val movie = engine.popularMovies()
            val tv = engine.popularTvShows()
            movie.onSuccess { _movies.value = it.items }.onFailure { _error.value = it.message }
            tv.onSuccess { _shows.value = it.items }.onFailure { if (_error.value == null) _error.value = it.message }
            _loading.value = false
        }
    }

    fun setQuery(value: String) {
        _query.value = value
        if (value.isBlank()) {
            _searchMovies.value = emptyList()
            _searchShows.value = emptyList()
            return
        }
        viewModelScope.launch {
            _loading.value = true
            val moviesResult = engine.searchMovies(value)
            val showsResult = engine.searchTvShows(value)
            _searchMovies.value = moviesResult.getOrNull()?.items.orEmpty()
            _searchShows.value = showsResult.getOrNull()?.items.orEmpty()
            _loading.value = false
        }
    }

    fun openMovie(movie: Movie) {
        _selectedMovie.value = movie
        _selectedShow.value = null
        _sourceResult.value = null
    }

    fun openShow(show: TvShow) {
        _selectedShow.value = show
        _selectedMovie.value = null
        _sourceResult.value = null
        loadSeasons(show)
    }

    fun closeDetails() {
        _selectedMovie.value = null
        _selectedShow.value = null
        _seasons.value = emptyList()
        _episodes.value = emptyList()
        _sourceResult.value = null
    }

    private fun loadSeasons(show: TvShow) {
        viewModelScope.launch {
            val count = show.numberOfSeasons ?: 0
            val result = mutableListOf<Season>()
            for (number in 1..count.coerceAtMost(30)) {
                engine.seasonDetails(show.providerId ?: return@launch, number, show.provider)
                    .onSuccess { result += it.first }
            }
            _seasons.value = result
        }
    }

    fun openSeason(show: TvShow, season: Season) {
        viewModelScope.launch {
            _loading.value = true
            engine.seasonDetails(show.providerId ?: return@launch, season.seasonNumber, show.provider)
                .onSuccess { _episodes.value = it.second }
                .onFailure { _error.value = it.message }
            _loading.value = false
        }
    }

    fun discoverMovie(movie: Movie, preferredLanguage: String? = null, preferredQuality: Int? = null) {
        discover(MediaType.MOVIE, movie.provider, movie.providerId ?: movie.id.toString(), null, null, preferredLanguage, preferredQuality)
    }

    fun discoverEpisode(show: TvShow, episode: Episode, preferredLanguage: String? = null, preferredQuality: Int? = null) {
        discover(MediaType.TV_SHOW, show.provider, show.providerId ?: show.id.toString(), episode.seasonNumber, episode.episodeNumber, preferredLanguage, preferredQuality)
    }

    private fun discover(
        type: MediaType,
        provider: String,
        providerId: String,
        season: Int?,
        episode: Int?,
        language: String?,
        quality: Int?
    ) {
        viewModelScope.launch {
            _loading.value = true
            _sourceResult.value = null
            _error.value = null
            _sourceResult.value = sourceEngine.discover(
                SourceDiscoveryRequest(type, provider, providerId, season, episode, language, quality)
            )
            _loading.value = false
        }
    }
}
