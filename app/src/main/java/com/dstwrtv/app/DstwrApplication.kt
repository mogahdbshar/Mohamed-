package com.dstwrtv.app

import android.app.Application
import com.dstwrtv.app.core.settings.SettingsManager
import com.dstwrtv.app.db.AppDatabase
import com.dstwrtv.app.repository.ChannelRepository
import java.io.File

class DstwrApplication : Application() {
    private val database by lazy { AppDatabase.getDatabase(this) }
    val settingsManager by lazy { SettingsManager(this) }
    val remoteConfigManager by lazy { com.dstwrtv.app.core.settings.RemoteConfigManager(this) }
    val telemetryReporter by lazy { com.dstwrtv.app.core.telemetry.TelemetryReporter(this) }
    val repository by lazy { ChannelRepository(database.channelDao(), settingsManager, remoteConfigManager) }

    override fun onCreate() {
        super.onCreate()
        com.dstwrtv.app.core.util.NetworkUtils.init(this)
    }
}
