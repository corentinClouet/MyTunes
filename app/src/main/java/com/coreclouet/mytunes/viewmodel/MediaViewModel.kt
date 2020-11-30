package com.coreclouet.mytunes.viewmodel

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coreclouet.mytunes.util.LoadingState
import com.coreclouet.mytunes.model.dto.SearchResult
import com.coreclouet.mytunes.model.Track
import com.coreclouet.mytunes.model.dto.TrackDto
import com.coreclouet.mytunes.repository.SongRepository
import com.coreclouet.mytunes.util.MediaState
import com.coreclouet.mytunes.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class MediaViewModel(private val repo: SongRepository) : ViewModel() {

    private var mediaPlayer: MediaPlayer? = null
    private var timer = Timer()

    private val _selectedTrackPosition = MutableLiveData<Int>()
    val selectedTrackPosition: LiveData<Int>
        get() = _selectedTrackPosition

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

    private val _loadingStream = MutableLiveData<LoadingState>()
    val loadingStream: LiveData<LoadingState>
        get() = _loadingStream

    private val _mediaState = MutableLiveData<MediaState>()
    val mediaState: LiveData<MediaState>
        get() = _mediaState

    private val _data = MutableLiveData<List<TrackDto>>()
    val data: LiveData<List<TrackDto>>
        get() = _data

    private val _timeStart = MutableLiveData<Int>()
    val timeStart: LiveData<Int>
        get() = _timeStart

    private val _timeEnd = MutableLiveData<Int>()
    val timeEnd: LiveData<Int>
        get() = _timeEnd

    private val _seekBarprogress = MutableLiveData<Int>()
    val seekBarprogress: LiveData<Int>
        get() = _seekBarprogress

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
        releaseMediaPlayer()
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
        releaseMediaPlayer()
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

    /**
     * Toggle media start and start/release media player
     */
    fun toggleMediaState() {
        _mediaState.value?.let {
            if (it == MediaState.PLAYING) {
                _mediaState.value = MediaState.PAUSED
                pauseMediaPlayer()
            } else {
                _mediaState.value = MediaState.PLAYING
                startMediaPlayer()
            }
        } ?: run {
            _mediaState.value = MediaState.PLAYING
            startMediaPlayer()
        }
    }

    /**
     * Start audio streaming
     */
    private fun startMediaPlayer() {
        val position = _selectedTrackPosition.value ?: return
        val track = _data.value?.get(position) ?: return

        _loadingStream.postValue(LoadingState.LOADING)
        _mediaState.value = MediaState.PAUSED

        mediaPlayer?.let {
            _loadingStream.postValue(LoadingState.LOADED)
            _mediaState.value = MediaState.PLAYING
            it.start()
        } ?: run {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setOnCompletionListener { _mediaState.value = MediaState.PAUSED }
                setDataSource(track.previewUrl)
                prepare() // might take long! (for buffering, etc)
                _loadingStream.postValue(LoadingState.LOADED)
                _mediaState.value = MediaState.PLAYING
                initializeMediaPlayerSeekBar()
                start()
            }
        }
    }

    /**
     * Pause media player
     */
    private fun pauseMediaPlayer() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
            }
        }
        _mediaState.value = MediaState.PAUSED
    }

    /**
     * Stop media player if needed and reset it
     */
    private fun releaseMediaPlayer() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.reset()
        }
        mediaPlayer = null
        _mediaState.value = MediaState.PAUSED
    }

    private fun initializeMediaPlayerSeekBar() {
        _timeEnd.postValue(mediaPlayer?.duration ?: 0)
        val timerTask = object : TimerTask() {
            override fun run() {
                _seekBarprogress.postValue(mediaPlayer?.currentPosition ?: 0)
                _timeStart.postValue(mediaPlayer?.currentPosition ?: 0)
                val diff = (mediaPlayer?.duration ?: 0) - (mediaPlayer?.currentPosition ?: 0)
                _timeEnd.postValue(diff)
            }
        }
        timer = Timer()
        timer.schedule(timerTask, 0, 1000L)
    }

    /**
     * Release media player when view model is cleared
     */
    override fun onCleared() {
        super.onCleared()
        releaseMediaPlayer()
    }


}