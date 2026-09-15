package com.dstwrtv.app.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dstwrtv.app.model.Channel
import com.dstwrtv.app.streaming.data.local.CatalogCacheDao
import com.dstwrtv.app.streaming.data.local.EpisodeDao
import com.dstwrtv.app.streaming.data.local.EpisodeEntity
import com.dstwrtv.app.streaming.data.local.MediaIdentityDao
import com.dstwrtv.app.streaming.data.local.MediaIdentityEntity
import com.dstwrtv.app.streaming.data.local.CatalogCacheEntity
import com.dstwrtv.app.streaming.data.local.MovieDao
import com.dstwrtv.app.streaming.data.local.MovieEntity
import com.dstwrtv.app.streaming.data.local.SeasonDao
import com.dstwrtv.app.streaming.data.local.SeasonEntity
import com.dstwrtv.app.streaming.data.local.StreamingFavoriteDao
import com.dstwrtv.app.streaming.data.local.StreamingFavoriteEntity
import com.dstwrtv.app.streaming.data.local.TvShowDao
import com.dstwrtv.app.streaming.data.local.TvShowEntity
import com.dstwrtv.app.streaming.data.local.WatchHistoryDao
import com.dstwrtv.app.streaming.data.local.WatchHistoryEntity

@Database(
    entities = [
        Channel::class,
        MovieEntity::class,
        TvShowEntity::class,
        SeasonEntity::class,
        EpisodeEntity::class,
        WatchHistoryEntity::class,
        StreamingFavoriteEntity::class,
        MediaIdentityEntity::class,
        CatalogCacheEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun channelDao(): ChannelDao
    abstract fun movieDao(): MovieDao
    abstract fun tvShowDao(): TvShowDao
    abstract fun seasonDao(): SeasonDao
    abstract fun episodeDao(): EpisodeDao
    abstract fun watchHistoryDao(): WatchHistoryDao
    abstract fun streamingFavoriteDao(): StreamingFavoriteDao
    abstract fun mediaIdentityDao(): MediaIdentityDao
    abstract fun catalogCacheDao(): CatalogCacheDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "destour_sport_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
