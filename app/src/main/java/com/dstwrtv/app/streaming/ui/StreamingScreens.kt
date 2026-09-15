package com.dstwrtv.app.streaming.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.dstwrtv.app.streaming.domain.model.Episode
import com.dstwrtv.app.streaming.domain.model.Movie
import com.dstwrtv.app.streaming.domain.model.Season
import com.dstwrtv.app.streaming.domain.model.TvShow
import com.dstwrtv.app.ui.components.DSTWRTheme

@Composable
fun StreamingView(
    viewModel: StreamingViewModel,
    onOpenPlayer: (String) -> Unit = {}
) {
    val query by viewModel.query.collectAsState()
    val movies by viewModel.movies.collectAsState()
    val shows by viewModel.shows.collectAsState()
    val searchMovies by viewModel.searchMovies.collectAsState()
    val searchShows by viewModel.searchShows.collectAsState()
    val movie by viewModel.selectedMovie.collectAsState()
    val show by viewModel.selectedShow.collectAsState()
    val seasons by viewModel.seasons.collectAsState()
    val episodes by viewModel.episodes.collectAsState()
    val sourceResult by viewModel.sourceResult.collectAsState()
    val loading by viewModel.loading.collectAsState()

    when {
        movie != null -> MovieDetailsScreen(movie!!, sourceResult?.sources?.isNotEmpty() == true, loading, onBack = viewModel::closeDetails, onWatch = { viewModel.discoverMovie(movie!!) })
        show != null -> TvDetailsScreen(show!!, seasons, episodes, sourceResult?.sources?.isNotEmpty() == true, loading, onBack = viewModel::closeDetails, onSeason = { viewModel.openSeason(show!!, it) }, onEpisode = { viewModel.discoverEpisode(show!!, it) })
        else -> StreamingHomeScreen(
            query = query,
            movies = if (query.isBlank()) movies else searchMovies,
            shows = if (query.isBlank()) shows else searchShows,
            loading = loading,
            onQuery = viewModel::setQuery,
            onMovie = viewModel::openMovie,
            onShow = viewModel::openShow,
            onRefresh = viewModel::refreshCatalog
        )
    }
}

@Composable
private fun StreamingHomeScreen(
    query: String,
    movies: List<Movie>,
    shows: List<TvShow>,
    loading: Boolean,
    onQuery: (String) -> Unit,
    onMovie: (Movie) -> Unit,
    onShow: (TvShow) -> Unit,
    onRefresh: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(bottom = 110.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
            Text("المشاهدة", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black)
            Text("أفلام ومسلسلات في مكان واحد", color = DSTWRTheme.TextMuted, fontSize = 13.sp)
            Spacer(Modifier.height(14.dp))
            OutlinedTextField(
                value = query,
                onValueChange = onQuery,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Icon(Icons.Rounded.Search, null) },
                trailingIcon = if (query.isNotBlank()) ({ IconButton(onClick = { onQuery("") }) { Icon(Icons.Rounded.Close, null) } }) else null,
                placeholder = { Text("ابحث عن فيلم أو مسلسل") },
                shape = RoundedCornerShape(20.dp)
            )
        }
        if (loading && movies.isEmpty() && shows.isEmpty()) {
            Box(Modifier.fillMaxWidth().height(240.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        }
        MediaSection("أفلام", movies, onMovie = onMovie)
        MediaSection("مسلسلات", shows, onShow = onShow)
        if (!loading && movies.isEmpty() && shows.isEmpty()) {
            Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Rounded.Movie, null, tint = DSTWRTheme.TextMuted, modifier = Modifier.size(42.dp))
                    Spacer(Modifier.height(10.dp))
                    Text("لا توجد نتائج متاحة حاليًا", color = DSTWRTheme.TextMuted)
                    Spacer(Modifier.height(14.dp))
                    Text("إعادة المحاولة", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.clip(RoundedCornerShape(16.dp)).background(DSTWRTheme.SurfaceDark).clickable(onClick = onRefresh).padding(horizontal = 18.dp, vertical = 10.dp))
                }
            }
        }
    }
}

@Composable
private fun MediaSection(title: String, movies: List<Movie> = emptyList(), shows: List<TvShow> = emptyList(), onMovie: (Movie) -> Unit = {}, onShow: (TvShow) -> Unit = {}) {
    if (movies.isEmpty() && shows.isEmpty()) return
    Column(modifier = Modifier.padding(top = 8.dp)) {
        Text(title, color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp))
        LazyRow(contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            if (movies.isNotEmpty()) items(movies, key = { "m${it.provider}:${it.providerId}:${it.id}" }) { MediaCard(it.title, it.releaseDate, it.images.posterPath, it.rating, Icons.Rounded.Movie) { onMovie(it) } }
            else items(shows, key = { "t${it.provider}:${it.providerId}:${it.id}" }) { MediaCard(it.name, it.firstAirDate, it.images.posterPath, it.rating, Icons.Rounded.Tv) { onShow(it) } }
        }
    }
}

@Composable
private fun MediaCard(title: String, date: String?, image: String?, rating: Double, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Column(modifier = Modifier.width(128.dp).clickable(onClick = onClick)) {
        Box(modifier = Modifier.width(128.dp).height(184.dp).clip(RoundedCornerShape(18.dp)).background(DSTWRTheme.SurfaceDark)) {
            AsyncImage(model = imageUrl(image), contentDescription = title, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = .72f)), startY = 90f)))
            Row(Modifier.align(Alignment.BottomStart).padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(if (rating > 0) String.format("%.1f", rating) else "", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.height(7.dp))
        Text(title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
        Text(date?.take(4).orEmpty(), color = DSTWRTheme.TextMuted, fontSize = 10.sp)
    }
}

@Composable
private fun MovieDetailsScreen(movie: Movie, hasSource: Boolean, loading: Boolean, onBack: () -> Unit, onWatch: () -> Unit) {
    DetailsLayout(onBack) {
        DetailsHero(movie.images.backdropPath ?: movie.images.posterPath, movie.title, movie.releaseDate, movie.rating)
        Text(movie.overview.orEmpty().ifBlank { "لا يتوفر وصف لهذا العنوان حاليًا." }, color = Color.White.copy(alpha = .86f), lineHeight = 22.sp)
        Spacer(Modifier.height(18.dp))
        WatchButton(loading, hasSource, onWatch)
        if (hasSource) Text("تم العثور على مصدر تشغيل جاهز.", color = Color.White.copy(alpha = .7f), fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
    }
}

@Composable
private fun TvDetailsScreen(show: TvShow, seasons: List<Season>, episodes: List<Episode>, hasSource: Boolean, loading: Boolean, onBack: () -> Unit, onSeason: (Season) -> Unit, onEpisode: (Episode) -> Unit) {
    DetailsLayout(onBack) {
        DetailsHero(show.images.backdropPath ?: show.images.posterPath, show.name, show.firstAirDate, show.rating)
        Text(show.overview.orEmpty().ifBlank { "لا يتوفر وصف لهذا المسلسل حاليًا." }, color = Color.White.copy(alpha = .86f), lineHeight = 22.sp)
        Spacer(Modifier.height(18.dp))
        Text("المواسم", color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(seasons, key = { "s${it.seasonNumber}" }) { season ->
                Text("الموسم ${season.seasonNumber}", color = Color.White, modifier = Modifier.clip(RoundedCornerShape(14.dp)).background(DSTWRTheme.SurfaceDark).clickable { onSeason(season) }.padding(horizontal = 14.dp, vertical = 10.dp))
            }
        }
        if (episodes.isNotEmpty()) {
            Spacer(Modifier.height(18.dp))
            Text("الحلقات", color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.ExtraBold)
            episodes.forEach { episode ->
                Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(DSTWRTheme.SurfaceDark).clickable { onEpisode(episode) }.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("${episode.episodeNumber}", color = DSTWRTheme.PrimaryRed, fontWeight = FontWeight.Black, modifier = Modifier.width(30.dp))
                    Column(Modifier.weight(1f)) { Text(episode.name, color = Color.White, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis); Text(episode.overview.orEmpty(), color = DSTWRTheme.TextMuted, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                    Icon(Icons.Rounded.PlayArrow, null, tint = Color.White)
                }
                Spacer(Modifier.height(7.dp))
            }
        }
        if (hasSource) Text("تم العثور على مصدر تشغيل جاهز.", color = Color.White.copy(alpha = .7f), fontSize = 12.sp)
    }
}

@Composable
private fun DetailsLayout(onBack: () -> Unit, content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(bottom = 110.dp)) {
        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = onBack) { Icon(Icons.Rounded.ArrowBack, "رجوع", tint = Color.White) }; Text("التفاصيل", color = Color.White, fontWeight = FontWeight.Bold) }
        Column(Modifier.padding(horizontal = 20.dp), content = content)
    }
}

@Composable
private fun DetailsHero(image: String?, title: String, date: String?, rating: Double) {
    Box(Modifier.fillMaxWidth().height(250.dp).clip(RoundedCornerShape(24.dp)).background(DSTWRTheme.SurfaceDark)) {
        AsyncImage(model = imageUrl(image), contentDescription = title, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = .88f)))))
        Column(Modifier.align(Alignment.BottomStart).padding(18.dp)) {
            Text(title, color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Black, maxLines = 2)
            Text("${date?.take(4).orEmpty()}  •  ${if (rating > 0) String.format("%.1f", rating) else "بدون تقييم"}", color = Color.White.copy(alpha = .8f), fontSize = 12.sp)
        }
    }
    Spacer(Modifier.height(16.dp))
}

@Composable
private fun WatchButton(loading: Boolean, hasSource: Boolean, onWatch: () -> Unit) {
    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(DSTWRTheme.PrimaryRed).clickable(enabled = !loading, onClick = onWatch).padding(vertical = 15.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
        if (loading) CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(20.dp)) else Icon(Icons.Rounded.PlayArrow, null, tint = Color.White)
        Spacer(Modifier.width(8.dp))
        Text(if (hasSource) "إعادة البحث عن مصدر" else "مشاهدة", color = Color.White, fontWeight = FontWeight.Black)
    }
}

private fun imageUrl(value: String?): String? {
    if (value.isNullOrBlank()) return null
    return if (value.startsWith("http://") || value.startsWith("https://")) value else "https://image.tmdb.org/t/p/w500$value"
}
