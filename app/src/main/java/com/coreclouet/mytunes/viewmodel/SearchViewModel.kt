package com.coreclouet.mytunes.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coreclouet.mytunes.model.ArtistWithCollectionAndTracks
import com.coreclouet.mytunes.util.LoadingState
import com.coreclouet.mytunes.model.SearchResult
import com.coreclouet.mytunes.repository.SongRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.invoke
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchViewModel(private val repo: SongRepository) : ViewModel() {

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

    private val _data = MutableLiveData<SearchResult>()
    val data: LiveData<SearchResult>
        get() = _data

    private val _dataDetail = MutableLiveData<ArtistWithCollectionAndTracks>()
    val dataDetail: LiveData<ArtistWithCollectionAndTracks>
        get() = _dataDetail

    /**
     * Fetch data corresponding to the term
     */
    fun fetchData(term: String) = viewModelScope.launch {
        _loadingState.postValue(LoadingState.LOADING)
        //search data corresponding to term
        val response: Response<SearchResult> = repo.searchTracks(term)
        if (response.isSuccessful) {
            //set livedata and save data in database
            _data.postValue(response.body())
            repo.saveSearchResult(response.body())
            _loadingState.postValue(LoadingState.LOADED)
        } else {
            _loadingState.postValue(LoadingState.error(response.errorBody().toString()))
        }
    }

    fun getArtistDetail(artistId: Long) = viewModelScope.launch{
        _dataDetail.postValue(repo.getArtistWithCollectionsAndTracks(artistId))
    }
}