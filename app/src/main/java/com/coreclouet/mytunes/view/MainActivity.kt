package com.coreclouet.mytunes.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import com.coreclouet.mytunes.R
import com.coreclouet.mytunes.viewmodel.TrackViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val trackViewModel by viewModel<TrackViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        trackViewModel.data.observe(this, Observer {
            // Populate the UI
            textview_hello_world.text = it.tracks?.get(0)?.artistName
        })

        trackViewModel.loadingState.observe(this, Observer {
            // Observe the loading state
        })

        trackViewModel.fetchData("drake")
    }
}