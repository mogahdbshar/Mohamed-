package com.dstwrtv.app.streaming.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.dstwrtv.app.streaming.domain.model.Episode
import com.dstwrtv.app.streaming.domain.model.Movie
import com.dstwrtv.app.streaming.domain.model.Season
import com.dstwrtv.app.streaming.domain.model.TvShow
import com.dstwrtv.app.streaming.domain.source.PlaybackSource
import com.dstwrtv.app.streaming.domain.source.SourceDiscoveryResult
import com.dstwrtv.app.ui.components.DSTWRTheme

@Composable
fun StreamingView(viewModel: StreamingViewModel, onOpenPlayer: (String, String) -> Unit = { _, _ -> }) {
    val query by viewModel.query.collectAsState(); val movies by viewModel.movies.collectAsState(); val shows by viewModel.shows.collectAsState(); val searchMovies by viewModel.searchMovies.collectAsState(); val searchShows by viewModel.searchShows.collectAsState(); val movie by viewModel.selectedMovie.collectAsState(); val show by viewModel.selectedShow.collectAsState(); val seasons by viewModel.seasons.collectAsState(); val episodes by viewModel.episodes.collectAsState(); val result by viewModel.sourceResult.collectAsState(); val selected by viewModel.selectedSource.collectAsState(); val loading by viewModel.loading.collectAsState(); val error by viewModel.error.collectAsState()
    when {
        movie != null -> MovieDetailsScreen(movie!!, result, selected, loading, error, viewModel::closeDetails, { viewModel.discoverMovie(movie!!) }, { viewModel.selectSource(it); onOpenPlayer(it.url, movie!!.title) })
        show != null -> TvDetailsScreen(show!!, seasons, episodes, result, selected, loading, error, viewModel::closeDetails, { viewModel.openSeason(show!!, it) }, { viewModel.discoverEpisode(show!!, it) }, { viewModel.selectSource(it); onOpenPlayer(it.url, "${show!!.name} - ${it.label}") })
        else -> StreamingHomeScreen(query, if (query.isBlank()) movies else searchMovies, if (query.isBlank()) shows else searchShows, loading, viewModel::setQuery, viewModel::openMovie, viewModel::openShow, viewModel::refreshCatalog, error)
    }
}

@Composable private fun StreamingHomeScreen(query: String, movies: List<Movie>, shows: List<TvShow>, loading: Boolean, onQuery: (String) -> Unit, onMovie: (Movie) -> Unit, onShow: (TvShow) -> Unit, onRefresh: () -> Unit, error: String?) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(bottom = 110.dp)) {
        Column(Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) { Text("المشاهدة", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black); Text("أفلام ومسلسلات في مكان واحد", color = DSTWRTheme.TextMuted, fontSize = 13.sp); Spacer(Modifier.height(14.dp)); OutlinedTextField(value = query, onValueChange = onQuery, modifier = Modifier.fillMaxWidth(), singleLine = true, leadingIcon = { Icon(Icons.Rounded.Search, null) }, trailingIcon = if (query.isNotBlank()) ({ IconButton(onClick = { onQuery("") }) { Icon(Icons.Rounded.Close, null) } }) else null, placeholder = { Text("ابحث عن فيلم أو مسلسل") }, shape = RoundedCornerShape(20.dp)) }
        if (loading && movies.isEmpty() && shows.isEmpty()) Box(Modifier.fillMaxWidth().height(240.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        MediaSection("أفلام", movies, onMovie = onMovie); MediaSection("مسلسلات", shows = shows, onShow = onShow)
        if (!loading && movies.isEmpty() && shows.isEmpty()) EmptyState(error ?: "لا توجد نتائج متاحة حاليًا", onRefresh)
    }
}

@Composable private fun MediaSection(title: String, movies: List<Movie> = emptyList(), shows: List<TvShow> = emptyList(), onMovie: (Movie) -> Unit = {}, onShow: (TvShow) -> Unit = {}) {
    if (movies.isEmpty() && shows.isEmpty()) return
    Column(Modifier.padding(top = 8.dp)) { Text(title, color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)); LazyRow(contentPadding = PaddingValues(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) { if (movies.isNotEmpty()) items(movies, key = { "m${it.provider}:${it.providerId}:${it.id}" }) { MediaCard(it.title, it.releaseDate, it.images.posterPath, it.rating, Icons.Rounded.Movie) { onMovie(it) } } else items(shows, key = { "t${it.provider}:${it.providerId}:${it.id}" }) { MediaCard(it.name, it.firstAirDate, it.images.posterPath, it.rating, Icons.Rounded.Tv) { onShow(it) } } } }
}

@Composable private fun MediaCard(title: String, date: String?, image: String?, rating: Double, icon: ImageVector, onClick: () -> Unit) { Column(Modifier.width(128.dp).clickable(onClick = onClick)) { Box(Modifier.width(128.dp).height(184.dp).clip(RoundedCornerShape(18.dp)).background(DSTWRTheme.SurfaceDark)) { AsyncImage(model = imageUrl(image), contentDescription = title, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop); Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = .72f)), startY = 90f))); Row(Modifier.align(Alignment.BottomStart).padding(8.dp), verticalAlignment = Alignment.CenterVertically) { Icon(icon, null, tint = Color.White, modifier = Modifier.size(16.dp)); Spacer(Modifier.width(4.dp)); if (rating > 0) Text(String.format("%.1f", rating), color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold) } }; Spacer(Modifier.height(7.dp)); Text(title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis); Text(date?.take(4).orEmpty(), color = DSTWRTheme.TextMuted, fontSize = 10.sp) } }

@Composable private fun MovieDetailsScreen(movie: Movie, result: SourceDiscoveryResult?, selected: PlaybackSource?, loading: Boolean, error: String?, onBack: () -> Unit, onWatch: () -> Unit, onSource: (PlaybackSource) -> Unit) { DetailsLayout(onBack) { DetailsHero(movie.images.backdropPath ?: movie.images.posterPath, movie.title, movie.releaseDate, movie.rating); Text(movie.overview.orEmpty().ifBlank { "لا يتوفر وصف لهذا العنوان حاليًا." }, color = Color.White.copy(alpha = .86f), lineHeight = 22.sp); Spacer(Modifier.height(18.dp)); WatchButton(loading, result?.sources?.isNotEmpty() == true, onWatch); SourcePanel(result, selected, loading, error, onSource) } }

@Composable private fun TvDetailsScreen(show: TvShow, seasons: List<Season>, episodes: List<Episode>, result: SourceDiscoveryResult?, selected: PlaybackSource?, loading: Boolean, error: String?, onBack: () -> Unit, onSeason: (Season) -> Unit, onEpisode: (Episode) -> Unit, onSource: (PlaybackSource) -> Unit) { DetailsLayout(onBack) { DetailsHero(show.images.backdropPath ?: show.images.posterPath, show.name, show.firstAirDate, show.rating); Text(show.overview.orEmpty().ifBlank { "لا يتوفر وصف لهذا المسلسل حاليًا." }, color = Color.White.copy(alpha = .86f), lineHeight = 22.sp); Spacer(Modifier.height(18.dp)); Text("المواسم", color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.ExtraBold); Spacer(Modifier.height(8.dp)); LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) { items(seasons, key = { "s${it.seasonNumber}" }) { season -> Text("الموسم ${season.seasonNumber}", color = Color.White, modifier = Modifier.clip(RoundedCornerShape(14.dp)).background(DSTWRTheme.SurfaceDark).clickable { onSeason(season) }.padding(horizontal = 14.dp, vertical = 10.dp)) } }; if (episodes.isNotEmpty()) { Spacer(Modifier.height(18.dp)); Text("الحلقات", color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.ExtraBold); Spacer(Modifier.height(8.dp)); episodes.forEach { episode -> Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(DSTWRTheme.SurfaceDark).clickable { onEpisode(episode) }.padding(12.dp), verticalAlignment = Alignment.CenterVertically) { Text("${episode.episodeNumber}", color = DSTWRTheme.PrimaryRed, fontWeight = FontWeight.Black, modifier = Modifier.width(30.dp)); Column(Modifier.weight(1f)) { Text(episode.name, color = Color.White, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis); Text(episode.overview.orEmpty(), color = DSTWRTheme.TextMuted, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis) }; Icon(Icons.Rounded.PlayArrow, null, tint = Color.White) }; Spacer(Modifier.height(7.dp)) } }; SourcePanel(result, selected, loading, error, onSource) } }

@Composable private fun SourcePanel(result: SourceDiscoveryResult?, selected: PlaybackSource?, loading: Boolean, error: String?, onSource: (PlaybackSource) -> Unit) { if (loading) { Spacer(Modifier.height(12.dp)); Row(verticalAlignment = Alignment.CenterVertically) { CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp); Spacer(Modifier.width(8.dp)); Text("جاري البحث عن أفضل مصدر...", color = DSTWRTheme.TextMuted, fontSize = 12.sp) } }; if (error != null && !loading) Text(error, color = DSTWRTheme.TextMuted, fontSize = 12.sp, modifier = Modifier.padding(top = 10.dp)); if (result != null && result.sources.isNotEmpty()) { Spacer(Modifier.height(16.dp)); Text("مصادر التشغيل", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold); Spacer(Modifier.height(8.dp)); result.sources.forEach { source -> val active = source.url == selected?.url; Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(if (active) DSTWRTheme.PrimaryRed.copy(alpha = .16f) else DSTWRTheme.SurfaceDark).clickable { onSource(source) }.padding(13.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Rounded.PlayCircle, null, tint = if (active) DSTWRTheme.PrimaryRed else Color.White, modifier = Modifier.size(24.dp)); Spacer(Modifier.width(10.dp)); Column(Modifier.weight(1f)) { Text(source.label, color = Color.White, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis); Text(listOfNotNull(source.quality?.let { "${it}p" }, source.format?.uppercase(), source.language).joinToString(" • ").ifBlank { "تشغيل مباشر" }, color = DSTWRTheme.TextMuted, fontSize = 11.sp) }; if (active) Icon(Icons.Rounded.CheckCircle, null, tint = DSTWRTheme.PrimaryRed) }; Spacer(Modifier.height(7.dp)) } } }

@Composable private fun EmptyState(message: String, onRefresh: () -> Unit) { Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) { Column(horizontalAlignment = Alignment.CenterHorizontally) { Icon(Icons.Rounded.Movie, null, tint = DSTWRTheme.TextMuted, modifier = Modifier.size(42.dp)); Spacer(Modifier.height(10.dp)); Text(message, color = DSTWRTheme.TextMuted); Spacer(Modifier.height(14.dp)); Text("إعادة المحاولة", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.clip(RoundedCornerShape(16.dp)).background(DSTWRTheme.SurfaceDark).clickable(onClick = onRefresh).padding(horizontal = 18.dp, vertical = 10.dp)) } } }
@Composable private fun DetailsLayout(onBack: () -> Unit, content: @Composable ColumnScope.() -> Unit) { Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(bottom = 110.dp)) { Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = onBack) { Icon(Icons.Rounded.ArrowBack, "رجوع", tint = Color.White) }; Text("التفاصيل", color = Color.White, fontWeight = FontWeight.Bold) }; Column(Modifier.padding(horizontal = 20.dp), content = content) } }
@Composable private fun DetailsHero(image: String?, title: String, date: String?, rating: Double) { Box(Modifier.fillMaxWidth().height(250.dp).clip(RoundedCornerShape(24.dp)).background(DSTWRTheme.SurfaceDark)) { AsyncImage(model = imageUrl(image), contentDescription = title, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop); Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = .88f))))); Column(Modifier.align(Alignment.BottomStart).padding(18.dp)) { Text(title, color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Black, maxLines = 2); Text("${date?.take(4).orEmpty()}  •  ${if (rating > 0) String.format("%.1f", rating) else "بدون تقييم"}", color = Color.White.copy(alpha = .8f), fontSize = 12.sp) } }; Spacer(Modifier.height(16.dp)) }
@Composable private fun WatchButton(loading: Boolean, hasSource: Boolean, onWatch: () -> Unit) { Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(DSTWRTheme.PrimaryRed).clickable(enabled = !loading, onClick = onWatch).padding(vertical = 15.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) { if (loading) CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(20.dp)) else Icon(Icons.Rounded.PlayArrow, null, tint = Color.White); Spacer(Modifier.width(8.dp)); Text(if (hasSource) "إعادة البحث عن مصدر" else "مشاهدة", color = Color.White, fontWeight = FontWeight.Black) } }
private fun imageUrl(value: String?): String? { if (value.isNullOrBlank()) return null; return if (value.startsWith("http://") || value.startsWith("https://")) value else "https://image.tmdb.org/t/p/w500$value" }
