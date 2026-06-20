package com.example

import android.app.Application
import com.example.db.AppDatabase
import com.example.repository.ChannelRepository
import java.io.File

class DestourApplication : Application() {
    private val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { ChannelRepository(database.channelDao()) }

    override fun onCreate() {
        super.onCreate()
        clearUnneededCache()
    }

    private fun clearUnneededCache() {
        try {
            // Delete old cache files to prevent bloat and slow down
            val cacheDir = applicationContext.cacheDir
            val files = cacheDir.listFiles()
            if (files != null) {
                var cacheSpace = 0L
                for (file in files) {
                    val size = getFolderSize(file)
                    cacheSpace += size
                    // If file is older than 2 days or cache is large, delete it
                    if (System.currentTimeMillis() - file.lastModified() > 2 * 24 * 60 * 60 * 1000 || cacheSpace > 50 * 1024 * 1024) {
                        file.deleteRecursively()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getFolderSize(file: File): Long {
        var size: Long = 0
        if (file.isDirectory) {
            val children = file.listFiles()
            if (children != null) {
                for (child in children) {
                    size += getFolderSize(child)
                }
            }
        } else {
            size = file.length()
        }
        return size
    }
}
