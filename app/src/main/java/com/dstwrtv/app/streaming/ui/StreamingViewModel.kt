package com.dstwrtv.app.streaming.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dstwrtv.app.streaming.StreamingContainer
import com.dstwrtv.app.streaming.domain.model.Episode
import com.dstwrtv.app.streaming.domain.model.MediaType
import com.dstwrtv.app.streaming.domain.model.Movie
import com.dstwrtv.app.streaming.domain.model.Season
import com.dstwrtv.app.streaming.domain.model.TvShow
import com.dstwrtv.app.streaming.domain.source.PlaybackSource
import com.dstwrtv.app.streaming.domain.source.SourceDiscoveryRequest
import com.dstwrtv.app.streaming.domain.source.SourceDiscoveryResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import org.json.JSONArray
import java.util.concurrent.TimeUnit

class StreamingViewModel : ViewModel() {
    private val engine = StreamingContainer.metadataEngine
    private val sourceEngine = StreamingContainer.sourceDiscoveryEngine
    private val translator = ArabicTranslator()
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
    private val _selectedSource = MutableStateFlow<PlaybackSource?>(null)
    val selectedSource: StateFlow<PlaybackSource?> = _selectedSource.asStateFlow()
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    init { refreshCatalog() }

    fun refreshCatalog() = viewModelScope.launch {
        _loading.value = true; _error.value = null
        val (movieResult, tvResult) = awaitAll(async { engine.popularMovies() }, async { engine.popularTvShows() })
        @Suppress("UNCHECKED_CAST") val m = movieResult as Result<com.dstwrtv.app.streaming.domain.model.CatalogPage<Movie>>
        @Suppress("UNCHECKED_CAST") val t = tvResult as Result<com.dstwrtv.app.streaming.domain.model.CatalogPage<TvShow>>
        m.onSuccess { page -> _movies.value = translateMovieCards(page.items) }.onFailure { _error.value = it.message }
        t.onSuccess { page -> _shows.value = translateShowCards(page.items) }.onFailure { if (_error.value == null) _error.value = it.message }
        _loading.value = false
    }

    fun setQuery(value: String) {
        _query.value = value
        if (value.isBlank()) { _searchMovies.value = emptyList(); _searchShows.value = emptyList(); return }
        viewModelScope.launch {
            _loading.value = true
            val (m, t) = awaitAll(async { engine.searchMovies(value) }, async { engine.searchTvShows(value) })
            @Suppress("UNCHECKED_CAST") val mr = m as Result<com.dstwrtv.app.streaming.domain.model.CatalogPage<Movie>>
            @Suppress("UNCHECKED_CAST") val tr = t as Result<com.dstwrtv.app.streaming.domain.model.CatalogPage<TvShow>>
            _searchMovies.value = mr.getOrNull()?.items?.let { translateMovieCards(it) }.orEmpty()
            _searchShows.value = tr.getOrNull()?.items?.let { translateShowCards(it) }.orEmpty()
            _loading.value = false
        }
    }

    fun openMovie(movie: Movie) = viewModelScope.launch {
        _selectedMovie.value = movie; _selectedShow.value = null; clearPlayback(); _loading.value = true
        val detailed = engine.movieDetails(movie.providerId ?: movie.id.toString(), movie.provider).getOrNull() ?: movie
        _selectedMovie.value = translateMovie(detailed)
        _loading.value = false
    }

    fun openShow(show: TvShow) = viewModelScope.launch {
        _selectedShow.value = show; _selectedMovie.value = null; clearPlayback(); _episodes.value = emptyList(); _seasons.value = emptyList(); _loading.value = true
        val detailed = engine.tvDetails(show.providerId ?: show.id.toString(), show.provider).getOrNull() ?: show
        val localized = translateShow(detailed)
        _selectedShow.value = localized
        loadSeasons(localized, allowProbe = true)
        _loading.value = false
    }

    fun closeDetails() { _selectedMovie.value = null; _selectedShow.value = null; _seasons.value = emptyList(); _episodes.value = emptyList(); clearPlayback() }

    private suspend fun loadSeasons(show: TvShow, allowProbe: Boolean) {
        val providerId = show.providerId ?: return
        val count = show.numberOfSeasons
        val numbers = if (count != null && count > 0) 1..count.coerceAtMost(30) else if (allowProbe) 1..20 else 1..0
        val result = mutableListOf<Season>()
        for (number in numbers) {
            engine.seasonDetails(providerId, number, show.provider).onSuccess { pair ->
                if (pair.second.isNotEmpty() || pair.first.episodeCount > 0) result += translateSeason(pair.first)
            }
        }
        _seasons.value = result.sortedBy { it.seasonNumber }
    }

    fun openSeason(show: TvShow, season: Season) = viewModelScope.launch {
        val providerId = show.providerId ?: return@launch
        _loading.value = true
        engine.seasonDetails(providerId, season.seasonNumber, show.provider)
            .onSuccess { pair -> _episodes.value = translateEpisodes(pair.second) }
            .onFailure { _error.value = "تعذر تحميل حلقات هذا الموسم حاليًا." }
        _loading.value = false
    }

    fun discoverMovie(movie: Movie, preferredLanguage: String? = "ar", preferredQuality: Int? = null) = discover(MediaType.MOVIE, movie.provider, movie.providerId ?: movie.id.toString(), null, null, preferredLanguage, preferredQuality, movie.title)
    fun discoverEpisode(show: TvShow, episode: Episode, preferredLanguage: String? = "ar", preferredQuality: Int? = null) = discover(MediaType.TV_SHOW, show.provider, show.providerId ?: show.id.toString(), episode.seasonNumber, episode.episodeNumber, preferredLanguage, preferredQuality, episode.name)

    private fun discover(type: MediaType, provider: String, providerId: String, season: Int?, episode: Int?, language: String?, quality: Int?, title: String?) = viewModelScope.launch {
        _loading.value = true; _sourceResult.value = null; _selectedSource.value = null; _error.value = null
        val result = sourceEngine.discover(SourceDiscoveryRequest(type, provider, providerId, season, episode, language, quality, title))
        _sourceResult.value = result; _selectedSource.value = result.sources.firstOrNull()
        if (result.sources.isEmpty()) _error.value = "لم يتم العثور على مصدر تشغيل مباشر متاح لهذا العنوان حاليًا."
        _loading.value = false
    }

    fun selectSource(source: PlaybackSource) { _selectedSource.value = source }
    fun clearPlayback() { _sourceResult.value = null; _selectedSource.value = null }

    private suspend fun translateMovieCards(items: List<Movie>): List<Movie> = items.map { it.copy(title = translator.translate(it.title)) }
    private suspend fun translateShowCards(items: List<TvShow>): List<TvShow> = items.map { it.copy(name = translator.translate(it.name)) }
    private suspend fun translateMovie(item: Movie): Movie = item.copy(title = translator.translate(item.title), overview = item.overview?.let { translator.translate(it) }, originalTitle = item.originalTitle)
    private suspend fun translateShow(item: TvShow): TvShow = item.copy(name = translator.translate(item.name), overview = item.overview?.let { translator.translate(it) }, originalName = item.originalName)
    private suspend fun translateSeason(item: Season): Season = item.copy(name = translator.translate(item.name), overview = item.overview?.let { translator.translate(it) })
    private suspend fun translateEpisodes(items: List<Episode>): List<Episode> = items.map { it.copy(name = translator.translate(it.name), overview = it.overview?.let { text -> translator.translate(text) }) }
}

private class ArabicTranslator {
    private val client = OkHttpClient.Builder().connectTimeout(8, TimeUnit.SECONDS).readTimeout(10, TimeUnit.SECONDS).build()
    private val cache = object : LinkedHashMap<String, String>(256, 0.75f, true) { override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, String>?) = size > 256 }
    @Synchronized private fun cached(text: String): String? = cache[text]
    @Synchronized private fun put(text: String, value: String) { cache[text] = value }

    suspend fun translate(text: String): String {
        val value = text.trim()
        if (value.isBlank() || value.any { it in '\u0600'..'\u06FF' }) return text
        cached(value)?.let { return it }
        return withContext(Dispatchers.IO) {
            runCatching {
                val url = "https://translate.googleapis.com/translate_a/single".toHttpUrl().newBuilder()
                    .addQueryParameter("client", "gtx").addQueryParameter("sl", "auto").addQueryParameter("tl", "ar").addQueryParameter("dt", "t").addQueryParameter("q", value).build()
                client.newCall(okhttp3.Request.Builder().url(url).get().build()).execute().use { response ->
                    if (!response.isSuccessful) return@runCatching text
                    val chunks = JSONArray(response.body?.string().orEmpty()).optJSONArray(0) ?: return@runCatching text
                    val translated = buildString { for (i in 0 until chunks.length()) chunks.optJSONArray(i)?.let { append(it.optString(0)) } }.trim()
                    translated.takeIf { it.isNotBlank() }?.also { put(value, it) } ?: text
                }
            }.getOrDefault(text)
        }
    }
}
