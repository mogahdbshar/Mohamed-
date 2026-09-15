package com.dstwrtv.app.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""CREATE TABLE IF NOT EXISTS streaming_movies (id INTEGER NOT NULL,title TEXT NOT NULL,originalTitle TEXT,overview TEXT,releaseDate TEXT,rating REAL NOT NULL,voteCount INTEGER NOT NULL,runtimeMinutes INTEGER,genreIds TEXT NOT NULL,posterPath TEXT,backdropPath TEXT,updatedAt INTEGER NOT NULL,PRIMARY KEY(id))""")
        db.execSQL("""CREATE TABLE IF NOT EXISTS streaming_tv_shows (id INTEGER NOT NULL,name TEXT NOT NULL,originalName TEXT,overview TEXT,firstAirDate TEXT,rating REAL NOT NULL,voteCount INTEGER NOT NULL,numberOfSeasons INTEGER,numberOfEpisodes INTEGER,genreIds TEXT NOT NULL,posterPath TEXT,backdropPath TEXT,updatedAt INTEGER NOT NULL,PRIMARY KEY(id))""")
        db.execSQL("""CREATE TABLE IF NOT EXISTS streaming_seasons (tvShowId INTEGER NOT NULL,seasonNumber INTEGER NOT NULL,name TEXT NOT NULL,overview TEXT,airDate TEXT,episodeCount INTEGER NOT NULL,posterPath TEXT,updatedAt INTEGER NOT NULL,PRIMARY KEY(tvShowId,seasonNumber))""")
        db.execSQL("""CREATE TABLE IF NOT EXISTS streaming_episodes (tvShowId INTEGER NOT NULL,seasonNumber INTEGER NOT NULL,episodeNumber INTEGER NOT NULL,name TEXT NOT NULL,overview TEXT,airDate TEXT,runtimeMinutes INTEGER,stillPath TEXT,rating REAL NOT NULL,updatedAt INTEGER NOT NULL,PRIMARY KEY(tvShowId,seasonNumber,episodeNumber))""")
        db.execSQL("""CREATE TABLE IF NOT EXISTS streaming_watch_history (mediaKey TEXT NOT NULL,mediaType TEXT NOT NULL,tmdbId INTEGER NOT NULL,seasonNumber INTEGER,episodeNumber INTEGER,positionMs INTEGER NOT NULL,durationMs INTEGER NOT NULL,watchedAt INTEGER NOT NULL,PRIMARY KEY(mediaKey))""")
        db.execSQL("""CREATE TABLE IF NOT EXISTS streaming_favorites (mediaType TEXT NOT NULL,tmdbId INTEGER NOT NULL,seasonNumber INTEGER NOT NULL,episodeNumber INTEGER NOT NULL,addedAt INTEGER NOT NULL,PRIMARY KEY(mediaType,tmdbId,seasonNumber,episodeNumber))""")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_streaming_movies_updatedAt ON streaming_movies(updatedAt)")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_streaming_tv_shows_updatedAt ON streaming_tv_shows(updatedAt)")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_streaming_episodes_show_season ON streaming_episodes(tvShowId,seasonNumber)")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_streaming_watch_history_watchedAt ON streaming_watch_history(watchedAt)")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_streaming_favorites_addedAt ON streaming_favorites(addedAt)")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE streaming_movies ADD COLUMN provider TEXT NOT NULL DEFAULT 'tmdb'")
        db.execSQL("ALTER TABLE streaming_movies ADD COLUMN providerId TEXT")
        db.execSQL("ALTER TABLE streaming_tv_shows ADD COLUMN provider TEXT NOT NULL DEFAULT 'tmdb'")
        db.execSQL("ALTER TABLE streaming_tv_shows ADD COLUMN providerId TEXT")
        db.execSQL("ALTER TABLE streaming_seasons ADD COLUMN provider TEXT NOT NULL DEFAULT 'tmdb'")
        db.execSQL("ALTER TABLE streaming_seasons ADD COLUMN providerId TEXT")
        db.execSQL("ALTER TABLE streaming_episodes ADD COLUMN provider TEXT NOT NULL DEFAULT 'tmdb'")
        db.execSQL("ALTER TABLE streaming_episodes ADD COLUMN providerId TEXT")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_streaming_movies_provider ON streaming_movies(provider,providerId)")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_streaming_tv_provider ON streaming_tv_shows(provider,providerId)")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_streaming_seasons_provider ON streaming_seasons(provider,providerId)")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_streaming_episodes_provider ON streaming_episodes(provider,providerId)")
        db.execSQL("CREATE TABLE IF NOT EXISTS streaming_media_identity (mediaType TEXT NOT NULL,provider TEXT NOT NULL,providerId TEXT NOT NULL,imdbId TEXT,tmdbId INTEGER,tvMazeId INTEGER,canonicalTitle TEXT NOT NULL,year INTEGER,updatedAt INTEGER NOT NULL,PRIMARY KEY(mediaType,provider,providerId))")
        db.execSQL("CREATE TABLE IF NOT EXISTS streaming_catalog_cache (cacheKey TEXT NOT NULL,provider TEXT NOT NULL,page INTEGER NOT NULL,mediaType TEXT NOT NULL,payload TEXT NOT NULL,storedAt INTEGER NOT NULL,PRIMARY KEY(cacheKey))")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_streaming_catalog_cache_provider_page ON streaming_catalog_cache(provider,mediaType,page)")
    }
}
