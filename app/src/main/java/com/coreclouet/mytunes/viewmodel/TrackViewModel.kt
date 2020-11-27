package com.coreclouet.mytunes.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.coreclouet.mytunes.data.model.LoadingState
import com.coreclouet.mytunes.data.model.SearchResult
import com.coreclouet.mytunes.data.model.Track
import com.coreclouet.mytunes.repository.TrackRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TrackViewModel(private val repo: TrackRepository) : ViewModel(), Callback<SearchResult> {

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

    private val _data = MutableLiveData<SearchResult>()
    val data: LiveData<SearchResult>
        get() = _data

    fun fetchData(term:String) {
        _loadingState.postValue(LoadingState.LOADING)
        repo.searchTracks(term).enqueue(this)
    }

    override fun onFailure(call: Call<SearchResult>, t: Throwable) {
        _loadingState.postValue(LoadingState.error(t.message))
    }

    override fun onResponse(call: Call<SearchResult>, response: Response<SearchResult>) {
        if (response.isSuccessful) {
            _data.postValue(response.body())
            _loadingState.postValue(LoadingState.LOADED)
        } else {
            _loadingState.postValue(LoadingState.error(response.errorBody().toString()))
        }
    }
}