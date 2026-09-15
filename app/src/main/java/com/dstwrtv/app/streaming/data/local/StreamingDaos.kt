package com.dstwrtv.app.streaming.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Query("SELECT * FROM streaming_movies ORDER BY updatedAt DESC")
    fun observeAll(): Flow<List<MovieEntity>>

    @Query("SELECT * FROM streaming_movies WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): MovieEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(movie: MovieEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(movies: List<MovieEntity>)
}

@Dao
interface TvShowDao {
    @Query("SELECT * FROM streaming_tv_shows ORDER BY updatedAt DESC")
    fun observeAll(): Flow<List<TvShowEntity>>

    @Query("SELECT * FROM streaming_tv_shows WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): TvShowEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(show: TvShowEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(shows: List<TvShowEntity>)
}

@Dao
interface SeasonDao {
    @Query("SELECT * FROM streaming_seasons WHERE tvShowId = :tvShowId ORDER BY seasonNumber ASC")
    fun observeForShow(tvShowId: Int): Flow<List<SeasonEntity>>

    @Query("SELECT * FROM streaming_seasons WHERE tvShowId = :tvShowId AND seasonNumber = :seasonNumber LIMIT 1")
    suspend fun get(tvShowId: Int, seasonNumber: Int): SeasonEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(season: SeasonEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(seasons: List<SeasonEntity>)
}

@Dao
interface EpisodeDao {
    @Query("SELECT * FROM streaming_episodes WHERE tvShowId = :tvShowId AND seasonNumber = :seasonNumber ORDER BY episodeNumber ASC")
    fun observeForSeason(tvShowId: Int, seasonNumber: Int): Flow<List<EpisodeEntity>>

    @Query("SELECT * FROM streaming_episodes WHERE tvShowId = :tvShowId AND seasonNumber = :seasonNumber ORDER BY episodeNumber ASC")
    suspend fun getForSeason(tvShowId: Int, seasonNumber: Int): List<EpisodeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(episodes: List<EpisodeEntity>)
}

@Dao
interface WatchHistoryDao {
    @Query("SELECT * FROM streaming_watch_history ORDER BY watchedAt DESC LIMIT :limit")
    fun observeRecent(limit: Int = 50): Flow<List<WatchHistoryEntity>>

    @Query("SELECT * FROM streaming_watch_history WHERE mediaKey = :mediaKey LIMIT 1")
    suspend fun get(mediaKey: String): WatchHistoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: WatchHistoryEntity)

    @Query("DELETE FROM streaming_watch_history WHERE mediaKey = :mediaKey")
    suspend fun delete(mediaKey: String)
}

@Dao
interface StreamingFavoriteDao {
    @Query("SELECT * FROM streaming_favorites ORDER BY addedAt DESC")
    fun observeAll(): Flow<List<StreamingFavoriteEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM streaming_favorites WHERE mediaType = :mediaType AND tmdbId = :tmdbId AND seasonNumber = :seasonNumber AND episodeNumber = :episodeNumber)")
    suspend fun exists(mediaType: String, tmdbId: Int, seasonNumber: Int, episodeNumber: Int): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: StreamingFavoriteEntity)

    @Query("DELETE FROM streaming_favorites WHERE mediaType = :mediaType AND tmdbId = :tmdbId AND seasonNumber = :seasonNumber AND episodeNumber = :episodeNumber")
    suspend fun delete(mediaType: String, tmdbId: Int, seasonNumber: Int, episodeNumber: Int)
}
