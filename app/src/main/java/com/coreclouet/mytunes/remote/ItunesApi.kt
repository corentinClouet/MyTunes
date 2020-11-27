package com.coreclouet.mytunes.remote

import com.coreclouet.mytunes.model.SearchResult
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesApi {
    @GET("search")
    suspend fun searchTracks(@Query("term") term:String): Response<SearchResult>
}