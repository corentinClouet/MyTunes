package com.coreclouet.mytunes.model.dto

data class TrackDto (
    val artistId: Long,
    val collectionId: Long,
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val collectionName: String,
    val artworkUrl: String,
    val previewUrl: String,
    val timeInMillis: Long
)