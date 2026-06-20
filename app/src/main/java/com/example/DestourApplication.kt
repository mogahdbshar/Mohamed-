package com.example

import android.app.Application
import com.example.db.AppDatabase
import com.example.repository.ChannelRepository

class DestourApplication : Application() {
    private val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { ChannelRepository(database.channelDao()) }
}
