package com.dstwrtv.app.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/** Adds provider-neutral identity and catalog-cache tables without touching IPTV data. */
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS streaming_media_identity (
                mediaType TEXT NOT NULL,
                provider TEXT NOT NULL,
                providerId TEXT NOT NULL,
                imdbId TEXT,
                tmdbId INTEGER,
                tvmazeId INTEGER,
                title TEXT NOT NULL,
                year INTEGER,
                updatedAt INTEGER NOT NULL,
                PRIMARY KEY(mediaType, provider, providerId)
            )
        """.trimIndent())

        db.execSQL("CREATE INDEX IF NOT EXISTS index_streaming_media_identity_imdbId ON streaming_media_identity(imdbId)")

        db.execSQL("""
            CREATE TABLE IF NOT EXISTS streaming_catalog_cache (
                mediaType TEXT NOT NULL,
                provider TEXT NOT NULL,
                providerId TEXT NOT NULL,
                title TEXT NOT NULL,
                originalTitle TEXT,
                overview TEXT,
                year INTEGER,
                rating REAL NOT NULL,
                voteCount INTEGER NOT NULL,
                posterPath TEXT,
                backdropPath TEXT,
                genres TEXT NOT NULL,
                updatedAt INTEGER NOT NULL,
                expiresAt INTEGER NOT NULL,
                PRIMARY KEY(mediaType, provider, providerId)
            )
        """.trimIndent())

        db.execSQL("CREATE INDEX IF NOT EXISTS index_streaming_catalog_cache_mediaType_expiresAt ON streaming_catalog_cache(mediaType, expiresAt)")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_streaming_catalog_cache_rating ON streaming_catalog_cache(mediaType, rating, voteCount)")
    }
}
