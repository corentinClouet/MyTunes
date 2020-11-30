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

class MediaViewModel(private val repo: SongRepository) : ViewModel() {

    private val _selectedTrackPosition = MutableLiveData<Int>()
    val selectedTrackPosition: LiveData<Int>
        get() = _selectedTrackPosition

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

    private val _data = MutableLiveData<List<TrackDto>>()
    val data: LiveData<List<TrackDto>>
        get() = _data

    /**
     * Fetch data corresponding to the term
     */
    fun fetchData(artistId: Long, trackId: Long) = viewModelScope.launch {
        _loadingState.postValue(LoadingState.LOADING)
        //search data corresponding to artist id
        val response: Resource<List<TrackDto>> = repo.searchArtistWithCollectionsAndTracks(artistId)
        if (response.status == Resource.Status.SUCCESS) {
            _data.value = response.data
            initTrackPosition(trackId)
            _loadingState.postValue(LoadingState.LOADED)
        } else {
            _loadingState.postValue(LoadingState.error(response.message))
        }
    }

    /**
     * Set track position depending on trackid
     */
    private fun initTrackPosition(trackId: Long) {
        _loadingState.postValue(LoadingState.LOADING)
        val list: List<TrackDto> = _data.value ?: ArrayList()
        for (index in list.indices) {
            if (list[index].trackId == trackId) {
                _selectedTrackPosition.postValue(index)
                _loadingState.postValue(LoadingState.LOADED)
                return
            }
        }
        _loadingState.postValue(LoadingState.error(null))
    }

    /**
     * Skip to next track
     */
    fun skipNext() {
        _loadingState.postValue(LoadingState.LOADING)
        val listSize = _data.value?.size ?: 0
        val position = _selectedTrackPosition.value ?: -1
        if (listSize < 1) {
            _loadingState.postValue(LoadingState.error(null))
            return
        }

        if (position + 1 == listSize) {
            //end of the list, restart to 0
            _selectedTrackPosition.postValue(0)
        } else {
            //increment +1
            _selectedTrackPosition.postValue(position + 1)
        }
        _loadingState.postValue(LoadingState.LOADED)
    }

    /**
     * Skip to previous track
     */
    fun skipPrevious() {
        _loadingState.postValue(LoadingState.LOADING)
        val listSize = _data.value?.size ?: 0
        val position = _selectedTrackPosition.value ?: -1
        if (listSize < 1 || position == -1) {
            _loadingState.postValue(LoadingState.error(null))
            return
        }

        if (position - 1 == -1) {
            //start of the list, go to end
            _selectedTrackPosition.postValue(listSize - 1)
        } else {
            //decrement -1
            _selectedTrackPosition.postValue(position - 1)
        }
        _loadingState.postValue(LoadingState.LOADED)
    }
}