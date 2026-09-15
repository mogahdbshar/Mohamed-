package com.dstwrtv.app.streaming.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.dstwrtv.app.streaming.domain.model.Episode
import com.dstwrtv.app.streaming.domain.model.Movie
import com.dstwrtv.app.streaming.domain.model.Season
import com.dstwrtv.app.streaming.domain.model.TvShow
import com.dstwrtv.app.streaming.domain.source.PlaybackSource
import com.dstwrtv.app.streaming.domain.source.SourceDiscoveryResult
import com.dstwrtv.app.ui.components.DSTWRTheme

@Composable
fun StreamingView(viewModel: StreamingViewModel, onOpenPlayer: (PlaybackSource, String) -> Unit = { _, _ -> }) {
    val query by viewModel.query.collectAsState(); val movies by viewModel.movies.collectAsState(); val shows by viewModel.shows.collectAsState(); val searchMovies by viewModel.searchMovies.collectAsState(); val searchShows by viewModel.searchShows.collectAsState(); val movie by viewModel.selectedMovie.collectAsState(); val show by viewModel.selectedShow.collectAsState(); val seasons by viewModel.seasons.collectAsState(); val episodes by viewModel.episodes.collectAsState(); val result by viewModel.sourceResult.collectAsState(); val selected by viewModel.selectedSource.collectAsState(); val loading by viewModel.loading.collectAsState(); val error by viewModel.error.collectAsState()
    when {
        movie != null -> MovieDetailsScreen(movie!!, result, selected, loading, error, viewModel::closeDetails, { viewModel.discoverMovie(movie!!) }, { viewModel.selectSource(it); onOpenPlayer(it, movie!!.title) })
        show != null -> TvDetailsScreen(show!!, seasons, episodes, result, selected, loading, error, viewModel::closeDetails, { viewModel.openSeason(show!!, it) }, { viewModel.discoverEpisode(show!!, it) }, { viewModel.selectSource(it); onOpenPlayer(it, show!!.name) })
        else -> StreamingHomeScreen(query, if (query.isBlank()) movies else searchMovies, if (query.isBlank()) shows else searchShows, loading, viewModel::setQuery, viewModel::openMovie, viewModel::openShow, viewModel::refreshCatalog, viewModel::loadMore, error)
    }
}

@Composable
private fun StreamingHomeScreen(query: String, movies: List<Movie>, shows: List<TvShow>, loading: Boolean, onQuery: (String) -> Unit, onMovie: (Movie) -> Unit, onShow: (TvShow) -> Unit, onRefresh: () -> Unit, onLoadMore: () -> Unit, error: String?) {
    val listState = rememberLazyListState()
    val hero = if (query.isBlank()) movies.firstOrNull() else searchMoviesHero(movies)
    LazyColumn(state = listState, modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 105.dp)) {
        item("header") {
            Column(Modifier.padding(horizontal = 20.dp, vertical = 14.dp)) {
                Text("المشاهدة", color = Color.White, fontSize = 29.sp, fontWeight = FontWeight.Black)
                Text("أفلام ومسلسلات في مكان واحد", color = DSTWRTheme.TextMuted, fontSize = 13.sp)
                Spacer(Modifier.height(14.dp))
                OutlinedTextField(value = query, onValueChange = onQuery, modifier = Modifier.fillMaxWidth(), singleLine = true, leadingIcon = { Icon(Icons.Rounded.Search, null) }, trailingIcon = if (query.isNotBlank()) ({ IconButton(onClick = { onQuery("") }) { Icon(Icons.Rounded.Close, null) } }) else null, placeholder = { Text("ابحث عن فيلم أو مسلسل") }, shape = RoundedCornerShape(20.dp))
            }
        }
        if (query.isBlank() && hero != null) item("hero") { CinematicHero(hero.images.backdropPath, hero.title, hero.overview) }
        if (loading && movies.isEmpty() && shows.isEmpty()) item("loading") { Box(Modifier.fillMaxWidth().height(220.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() } }
        item("movies-title") { SectionHeader("الأفلام", Icons.Rounded.Movie) }
        if (movies.isEmpty()) item("movies-empty") { SectionMessage("لا توجد أفلام متاحة حاليًا") }
        itemsIndexed(movies, key = { _, m -> "m:${m.provider}:${m.providerId}:${m.id}" }) { index, item -> VerticalMediaCard(item.title, item.releaseDate, item.images.posterPath, item.images.backdropPath, item.rating, onClick = { onMovie(item) }) }
        item("shows-title") { SectionHeader("المسلسلات", Icons.Rounded.Tv) }
        if (shows.isEmpty()) item("shows-empty") { SectionMessage("لا توجد مسلسلات متاحة حاليًا") }
        itemsIndexed(shows, key = { _, s -> "t:${s.provider}:${s.providerId}:${s.id}" }) { _, item -> VerticalMediaCard(item.name, item.firstAirDate, item.images.posterPath, item.images.backdropPath, item.rating, onClick = { onShow(item) }) }
        if (!loading && movies.isEmpty() && shows.isEmpty()) item("empty") { EmptyState(error ?: "لا توجد نتائج متاحة حاليًا", onRefresh) }
        if (loading) item("footer-loading") { Box(Modifier.fillMaxWidth().padding(18.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp) } }
    }
    LaunchedEffect(listState, movies.size, shows.size, query) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0 }
            .collect { last -> if (query.isBlank() && last >= listState.layoutInfo.totalItemsCount - 5) onLoadMore() }
    }
}

private fun searchMoviesHero(movies: List<Movie>): Movie? = movies.firstOrNull()

@Composable private fun CinematicHero(backdrop: String?, title: String, overview: String?) {
    if (backdrop.isNullOrBlank()) return
    Box(Modifier.fillMaxWidth().height(205.dp).padding(horizontal = 16.dp).clip(RoundedCornerShape(24.dp)).background(DSTWRTheme.SurfaceDark)) {
        ProgressiveImage(backdrop, title, Modifier.fillMaxSize(), 720)
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = .93f)))))
        Column(Modifier.align(Alignment.BottomStart).padding(16.dp)) {
            Text("مختارات سينمائية", color = DSTWRTheme.TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(title, color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.Black, maxLines = 2, overflow = TextOverflow.Ellipsis)
            if (!overview.isNullOrBlank()) Text(overview, color = Color.White.copy(alpha = .72f), fontSize = 10.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable private fun SectionHeader(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) { Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) { Icon(icon, null, tint = DSTWRTheme.PrimaryRed, modifier = Modifier.size(20.dp)); Spacer(Modifier.width(8.dp)); Text(title, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold) } }
@Composable private fun SectionMessage(text: String) { Text(text, color = DSTWRTheme.TextMuted, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) }

@Composable private fun VerticalMediaCard(title: String, date: String?, poster: String?, backdrop: String?, rating: Double, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp).clip(RoundedCornerShape(22.dp)).background(DSTWRTheme.SurfaceDark).clickable(onClick = onClick).padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.width(112.dp).height(164.dp).clip(RoundedCornerShape(17.dp)).background(Color.Black)) {
            ProgressiveImage(poster ?: backdrop, title, Modifier.fillMaxSize(), 360)
            Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = .72f)))))
            if (rating > 0) Row(Modifier.align(Alignment.BottomStart).padding(8.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Rounded.Star, null, tint = Color.White, modifier = Modifier.size(14.dp)); Spacer(Modifier.width(3.dp)); Text(String.format("%.1f", rating), color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
        }
        Spacer(Modifier.width(13.dp)); Column(Modifier.weight(1f).padding(end = 4.dp)) { Text(title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, maxLines = 2, overflow = TextOverflow.Ellipsis); Spacer(Modifier.height(7.dp)); Text(date?.take(4).orEmpty(), color = DSTWRTheme.TextMuted, fontSize = 11.sp); Spacer(Modifier.height(7.dp)); Text("تفاصيل العنوان والمشاهدة", color = DSTWRTheme.TextMuted, fontSize = 11.sp, maxLines = 2) }
        Icon(Icons.Rounded.ChevronLeft, null, tint = Color.White.copy(alpha = .7f), modifier = Modifier.size(22.dp))
    }
}

@Composable private fun ProgressiveImage(value: String?, title: String, modifier: Modifier = Modifier, requestedSize: Int = 360) {
    if (value.isNullOrBlank()) return
    AsyncImage(model = ImageRequest.Builder(androidx.compose.ui.platform.LocalContext.current).data(imageUrl(value)).crossfade(180).memoryCachePolicy(CachePolicy.ENABLED).diskCachePolicy(CachePolicy.ENABLED).networkCachePolicy(CachePolicy.ENABLED).size(requestedSize).build(), contentDescription = title, modifier = modifier, contentScale = ContentScale.Crop)
}

@Composable private fun MovieDetailsScreen(movie: Movie, result: SourceDiscoveryResult?, selected: PlaybackSource?, loading: Boolean, error: String?, onBack: () -> Unit, onWatch: () -> Unit, onSource: (PlaybackSource) -> Unit) { DetailsLayout(onBack) { DetailsHero(movie.images.backdropPath ?: movie.images.posterPath, movie.title, movie.releaseDate, movie.rating); Text(movie.overview.orEmpty().ifBlank { "لا يتوفر وصف لهذا العنوان حاليًا." }, color = Color.White.copy(alpha = .86f), lineHeight = 22.sp); Spacer(Modifier.height(18.dp)); WatchButton(loading, result?.sources?.isNotEmpty() == true, onWatch); SourcePanel(result, selected, loading, error, onSource) } }
@Composable private fun TvDetailsScreen(show: TvShow, seasons: List<Season>, episodes: List<Episode>, result: SourceDiscoveryResult?, selected: PlaybackSource?, loading: Boolean, error: String?, onBack: () -> Unit, onSeason: (Season) -> Unit, onEpisode: (Episode) -> Unit, onSource: (PlaybackSource) -> Unit) { DetailsLayout(onBack) { DetailsHero(show.images.backdropPath ?: show.images.posterPath, show.name, show.firstAirDate, show.rating); Text(show.overview.orEmpty().ifBlank { "لا يتوفر وصف لهذا المسلسل حاليًا." }, color = Color.White.copy(alpha = .86f), lineHeight = 22.sp); Spacer(Modifier.height(18.dp)); Text("المواسم", color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.ExtraBold); Spacer(Modifier.height(8.dp)); LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) { items(seasons, key = { "s${it.seasonNumber}" }) { season -> Text("الموسم ${season.seasonNumber}", color = Color.White, modifier = Modifier.clip(RoundedCornerShape(14.dp)).background(DSTWRTheme.SurfaceDark).clickable { onSeason(season) }.padding(horizontal = 14.dp, vertical = 10.dp)) } }; if (episodes.isNotEmpty()) { Spacer(Modifier.height(18.dp)); Text("الحلقات", color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.ExtraBold); Spacer(Modifier.height(8.dp)); episodes.forEach { episode -> Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(DSTWRTheme.SurfaceDark).clickable { onEpisode(episode) }.padding(12.dp), verticalAlignment = Alignment.CenterVertically) { Text("${episode.episodeNumber}", color = DSTWRTheme.PrimaryRed, fontWeight = FontWeight.Black, modifier = Modifier.width(30.dp)); Column(Modifier.weight(1f)) { Text(episode.name, color = Color.White, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis); Text(episode.overview.orEmpty(), color = DSTWRTheme.TextMuted, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis) }; Icon(Icons.Rounded.PlayArrow, null, tint = Color.White) }; Spacer(Modifier.height(7.dp)) } }; SourcePanel(result, selected, loading, error, onSource) } }
@Composable private fun SourcePanel(result: SourceDiscoveryResult?, selected: PlaybackSource?, loading: Boolean, error: String?, onSource: (PlaybackSource) -> Unit) { if (loading) { Spacer(Modifier.height(12.dp)); Row(verticalAlignment = Alignment.CenterVertically) { CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp); Spacer(Modifier.width(8.dp)); Text("جاري البحث عن أفضل مصدر...", color = DSTWRTheme.TextMuted, fontSize = 12.sp) } }; if (error != null && !loading) Text(error, color = DSTWRTheme.TextMuted, fontSize = 12.sp, modifier = Modifier.padding(top = 10.dp)); if (result != null && result.sources.isNotEmpty()) { Spacer(Modifier.height(16.dp)); Text("مصادر التشغيل", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold); Spacer(Modifier.height(8.dp)); result.sources.forEach { source -> val active = source.url == selected?.url; Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(if (active) DSTWRTheme.PrimaryRed.copy(alpha = .16f) else DSTWRTheme.SurfaceDark).clickable { onSource(source) }.padding(13.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Rounded.PlayCircle, null, tint = if (active) DSTWRTheme.PrimaryRed else Color.White, modifier = Modifier.size(24.dp)); Spacer(Modifier.width(10.dp)); Column(Modifier.weight(1f)) { Text(source.label, color = Color.White, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis); Text(listOfNotNull(source.quality?.let { "${it}p" }, source.format?.uppercase(), source.language).joinToString(" • ").ifBlank { "تشغيل مباشر" }, color = DSTWRTheme.TextMuted, fontSize = 11.sp) }; if (active) Icon(Icons.Rounded.CheckCircle, null, tint = DSTWRTheme.PrimaryRed) }; Spacer(Modifier.height(7.dp)) } } }
@Composable private fun EmptyState(message: String, onRefresh: () -> Unit) { Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) { Column(horizontalAlignment = Alignment.CenterHorizontally) { Icon(Icons.Rounded.Movie, null, tint = DSTWRTheme.TextMuted, modifier = Modifier.size(42.dp)); Spacer(Modifier.height(10.dp)); Text(message, color = DSTWRTheme.TextMuted); Spacer(Modifier.height(14.dp)); Text("إعادة المحاولة", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.clip(RoundedCornerShape(16.dp)).background(DSTWRTheme.SurfaceDark).clickable(onClick = onRefresh).padding(horizontal = 18.dp, vertical = 10.dp)) } } }
@Composable private fun DetailsLayout(onBack: () -> Unit, content: @Composable ColumnScope.() -> Unit) { Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(bottom = 110.dp)) { Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = onBack) { Icon(Icons.Rounded.ArrowBack, "رجوع", tint = Color.White) }; Text("التفاصيل", color = Color.White, fontWeight = FontWeight.Bold) }; Column(Modifier.padding(horizontal = 20.dp), content = content) } }
@Composable private fun DetailsHero(image: String?, title: String, date: String?, rating: Double) { Box(Modifier.fillMaxWidth().height(250.dp).clip(RoundedCornerShape(24.dp)).background(DSTWRTheme.SurfaceDark)) { ProgressiveImage(image, title, Modifier.fillMaxSize(), 720); Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = .88f))))); Column(Modifier.align(Alignment.BottomStart).padding(18.dp)) { Text(title, color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Black, maxLines = 2); Text("${date?.take(4).orEmpty()}  •  ${if (rating > 0) String.format("%.1f", rating) else "بدون تقييم"}", color = Color.White.copy(alpha = .8f), fontSize = 12.sp) } }; Spacer(Modifier.height(16.dp)) }
@Composable private fun WatchButton(loading: Boolean, hasSource: Boolean, onWatch: () -> Unit) { Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(DSTWRTheme.PrimaryRed).clickable(enabled = !loading, onClick = onWatch).padding(vertical = 15.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) { if (loading) CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(20.dp)) else Icon(Icons.Rounded.PlayArrow, null, tint = Color.White); Spacer(Modifier.width(8.dp)); Text(if (hasSource) "إعادة البحث عن مصدر" else "مشاهدة", color = Color.White, fontWeight = FontWeight.Black) } }
private fun imageUrl(value: String): String = if (value.startsWith("http://") || value.startsWith("https://")) value else "https://image.tmdb.org/t/p/w500$value"
