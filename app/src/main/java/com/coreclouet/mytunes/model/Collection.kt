package com.coreclouet.mytunes.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Collection(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "artistId") val artistId: Long,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "view_url") val viewUrl: String,
    @ColumnInfo(name = "artwork_url_30") val artworkUrl30: String,
    @ColumnInfo(name = "artwork_url_60") val artworkUrl60: String,
    @ColumnInfo(name = "artwork_url_100") val artworkUrl100: String,
    @ColumnInfo(name = "release_date") val releaseDate: String,
    @ColumnInfo(name = "track_count") val trackCount: Int,
    @ColumnInfo(name = "primary_genre_name") val primaryGenreName: String
)