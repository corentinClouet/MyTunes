package com.coreclouet.mytunes.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coreclouet.mytunes.util.LoadingState
import com.coreclouet.mytunes.model.dto.SearchResult
import com.coreclouet.mytunes.model.Track
import com.coreclouet.mytunes.model.dto.TrackDto
import com.coreclouet.mytunes.repository.SongRepository
import com.coreclouet.mytunes.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class SearchViewModel(private val repo: SongRepository) : ViewModel() {

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

    private val _data = MutableLiveData<List<TrackDto>>()
    val data: LiveData<List<TrackDto>>
        get() = _data

    /**
     * Fetch data corresponding to the term
     */
    fun fetchData(term: String) = viewModelScope.launch {
        _loadingState.postValue(LoadingState.LOADING)
        //search data corresponding to term
        val response: Resource<List<TrackDto>> = repo.searchTracks(term)
        if (response.status == Resource.Status.SUCCESS) {
            _data.postValue(response.data)
            _loadingState.postValue(LoadingState.LOADED)
        } else {
            _loadingState.postValue(LoadingState.error(response.message))
        }
    }
}