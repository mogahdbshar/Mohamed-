package com.dstwrtv.app.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Entity(tableName = "channels")
data class Channel(
    @PrimaryKey val url: String,
    val name: String,
    val category: String,
    val logo: String?,
    val isFavorite: Boolean = false
)

