package com.coreclouet.mytunes.view.activity

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.coreclouet.mytunes.R
import com.coreclouet.mytunes.application.EXTRA_ARTIST_ID
import com.coreclouet.mytunes.application.EXTRA_TRACK_ID
import com.coreclouet.mytunes.converter.TimeConverter
import com.coreclouet.mytunes.util.LoadingState
import com.coreclouet.mytunes.util.MediaState
import com.coreclouet.mytunes.viewmodel.MediaViewModel
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import kotlinx.android.synthetic.main.activity_media.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaActivity : AppCompatActivity() {

    private val mediaViewModel by viewModel<MediaViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)

        //manage current track
        mediaViewModel.selectedTrackPosition.observe(this, Observer { trackPosition ->
            updateTrackInfo(trackPosition)
        })

        //manage loading state (loading, loaded or error)
        mediaViewModel.loadingState.observe(this, Observer {
            manageLoading(it)
        })

        //manage media state (button play or pause)
        mediaViewModel.mediaState.observe(this, Observer {
            manageMediaState(it)
        })

        //manage time start
        mediaViewModel.timeStart.observe(this, Observer {
            textViewTimeStart.text = TimeConverter.convertTimeInMillisToMediaFormat(it)
        })

        //manage time end
        mediaViewModel.timeEnd.observe(this, Observer {
            if (seekBarTime.max == 0) {
                seekBarTime.max = it
            }
            textViewTimeEnd.text = TimeConverter.convertTimeInMillisToMediaFormat(it)
        })

        //manage time end
        mediaViewModel.seekBarprogress.observe(this, Observer {
            seekBarTime.progress = it
        })

        //toggle media state
        imageViewPlayPause.setOnClickListener {
            mediaViewModel.toggleMediaState()
        }

        //skip to next track
        imageViewSkipNext.setOnClickListener {
            mediaViewModel.skipNext()
        }

        //skip to previous track
        imageViewSkipPrevious.setOnClickListener {
            mediaViewModel.skipPrevious()
        }

        //finish activity
        imageViewArrowBack.setOnClickListener {
            finish()
        }

        //first launch
        if (mediaViewModel.selectedTrackPosition.value == null) {
            val artistId = intent.getLongExtra(EXTRA_ARTIST_ID, -1)
            val trackId = intent.getLongExtra(EXTRA_TRACK_ID, -1)
            mediaViewModel.fetchData(artistId, trackId)
        }
    }

    /**
     * Update track info
     */
    private fun updateTrackInfo(trackPosition: Int) {
        val trackList = mediaViewModel.data.value ?: ArrayList()
        if (trackList.isEmpty()) return

        val selectedTrack = trackList[trackPosition]
        Picasso.get()
            .load(selectedTrack.artworkUrl)
            .placeholder(R.drawable.ic_music_note)
            .error(R.drawable.ic_music_note)
            .transform(RoundedCornersTransformation(10, 0))
            .into(imageViewCollection)
        imageViewPlayPause.setImageResource(R.drawable.ic_play_arrow)
        textViewTrackTitle.text = selectedTrack.trackName
        textViewTrackArtistCollection.text = getString(
            R.string.artist_collection,
            selectedTrack.artistName,
            selectedTrack.collectionName
        )
        textViewTimeStart.text = "0:00"
        textViewTimeEnd.text = "???"
        seekBarTime.max = 0
    }

    /**
     * Manage visibilities of views depending on loading status
     */
    private fun manageLoading(state: LoadingState) {
        when (state.status) {
            LoadingState.Status.LOADING -> {
                progressBarPlaylist.visibility = View.VISIBLE
                imageViewCollection.visibility = View.GONE
                seekBarTime.visibility = View.GONE
                imageViewPlayPause.visibility = View.GONE
                imageViewSkipPrevious.visibility = View.GONE
                imageViewSkipNext.visibility = View.GONE
                textViewTrackTitle.visibility = View.GONE
                textViewTrackArtistCollection.visibility = View.GONE
                textViewTimeStart.visibility = View.GONE
                textViewTimeEnd.visibility = View.GONE
                textViewTimeEnd.visibility = View.GONE
            }
            LoadingState.Status.SUCCESS -> {
                progressBarPlaylist.visibility = View.GONE
                imageViewCollection.visibility = View.VISIBLE
                seekBarTime.visibility = View.VISIBLE
                imageViewPlayPause.visibility = View.VISIBLE
                imageViewSkipPrevious.visibility = View.VISIBLE
                imageViewSkipNext.visibility = View.VISIBLE
                textViewTrackTitle.visibility = View.VISIBLE
                textViewTrackArtistCollection.visibility = View.VISIBLE
                textViewTimeStart.visibility = View.VISIBLE
                textViewTimeEnd.visibility = View.VISIBLE
                textViewTimeEnd.visibility = View.VISIBLE
            }
            LoadingState.Status.ERROR -> {
                progressBarPlaylist.visibility = View.GONE
                imageViewCollection.visibility = View.VISIBLE
                seekBarTime.visibility = View.VISIBLE
                imageViewPlayPause.visibility = View.VISIBLE
                imageViewSkipPrevious.visibility = View.VISIBLE
                imageViewSkipNext.visibility = View.VISIBLE
                textViewTrackTitle.visibility = View.VISIBLE
                textViewTrackArtistCollection.visibility = View.VISIBLE
                textViewTimeStart.visibility = View.VISIBLE
                textViewTimeEnd.visibility = View.VISIBLE
                showErrorMessage(state.msg)
            }
        }
    }

    /**
     * Manage UI depending on media state (PLAYING or PAUSED)
     */
    private fun manageMediaState(mediaState: MediaState) {
        if (mediaState == MediaState.PLAYING) {
            imageViewPlayPause.setImageResource(R.drawable.ic_pause)
        } else {
            imageViewPlayPause.setImageResource(R.drawable.ic_play_arrow)
        }
    }

    /**
     * Show error message and finish activity
     */
    private fun showErrorMessage(message: String?) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(message ?: getString(R.string.default_error))
            .setPositiveButton(
                android.R.string.ok
            ) { dialog, _ ->
                dialog.dismiss()
                finish()
            }
        // Create the AlertDialog object and show it
        val dialog: Dialog = builder.create()
        dialog.show()
    }
}