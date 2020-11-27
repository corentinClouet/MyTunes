package com.coreclouet.mytunes.data.remote

import com.coreclouet.mytunes.data.model.SearchResult
import com.coreclouet.mytunes.data.model.Track
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesApi {
    @GET("search")
    fun searchTracks(@Query("term") term:String): Call<SearchResult>
}