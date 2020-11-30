package com.coreclouet.mytunes.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Track(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "collectionId") val collectionId: Long,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "preview_url") val previewUrl: String,
    @ColumnInfo(name = "number") val number: Int,
    @ColumnInfo(name = "time_millis") val timeMillis: Long,
    @ColumnInfo(name = "is_streamable") val streamable: Boolean
)