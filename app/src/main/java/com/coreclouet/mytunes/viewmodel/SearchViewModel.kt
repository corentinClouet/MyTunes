package com.coreclouet.mytunes.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.coreclouet.mytunes.model.LoadingState
import com.coreclouet.mytunes.model.SearchResult
import com.coreclouet.mytunes.repository.SongRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchViewModel(private val repo: SongRepository) : ViewModel(), Callback<SearchResult> {

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
            repo.saveSearchResult(response.body())
            _loadingState.postValue(LoadingState.LOADED)
        } else {
            _loadingState.postValue(LoadingState.error(response.errorBody().toString()))
        }
    }
}