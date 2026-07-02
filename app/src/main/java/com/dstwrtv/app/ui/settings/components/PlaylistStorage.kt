package com.dstwrtv.app.ui.settings.components

import android.content.Context

data class SavedPlaylist(
    val id: String,
    val name: String,
    val url: String,
    val type: String, // "m3u" or "xtream"
    val host: String = "",
    val user: String = "",
    val pass: String = ""
)

object PlaylistStorage {
    fun savePlaylists(context: Context, playlists: List<SavedPlaylist>) {
        val sharedPrefs = context.getSharedPreferences("dstwr_prefs", Context.MODE_PRIVATE)
        val serialized = playlists.joinToString("\n") { 
            "${it.id.replace("||", "")}||${it.name.replace("||", "")}||${it.url.replace("||", "")}||${it.type.replace("||", "")}||${it.host.replace("||", "")}||${it.user.replace("||", "")}||${it.pass.replace("||", "")}"
        }
        sharedPrefs.edit().putString("saved_playlists_list", serialized).apply()
    }

    fun getPlaylists(context: Context): List<SavedPlaylist> {
        val sharedPrefs = context.getSharedPreferences("dstwr_prefs", Context.MODE_PRIVATE)
        val serialized = sharedPrefs.getString("saved_playlists_list", "") ?: ""
        if (serialized.isBlank()) return emptyList()
        return serialized.split("\n").mapNotNull { line ->
            val parts = line.split("||")
            if (parts.size >= 4) {
                SavedPlaylist(
                    id = parts[0],
                    name = parts[1],
                    url = parts[2],
                    type = parts[3],
                    host = parts.getOrNull(4) ?: "",
                    user = parts.getOrNull(5) ?: "",
                    pass = parts.getOrNull(6) ?: ""
                )
            } else null
        }
    }
}
