package com.coreclouet.mytunes.repository

import com.coreclouet.mytunes.data.remote.ItunesApi

class TrackRepository(private val api: ItunesApi) {
    fun searchTracks(term:String) = api.searchTracks(term)
}