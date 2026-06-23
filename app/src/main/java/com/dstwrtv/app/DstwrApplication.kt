package com.dstwrtv.app

import android.app.Application
import com.dstwrtv.app.db.AppDatabase
import com.dstwrtv.app.repository.ChannelRepository

class DstwrApplication : Application() {
    private val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { ChannelRepository(database.channelDao()) }
}
