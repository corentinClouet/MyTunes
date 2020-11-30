package com.coreclouet.mytunes.view

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.coreclouet.mytunes.R
import com.coreclouet.mytunes.util.LoadingState
import com.coreclouet.mytunes.view.adapter.TrackAdapter
import com.coreclouet.mytunes.viewmodel.SearchViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    private val trackViewModel by viewModel<SearchViewModel>()
    private var mAdapter: TrackAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchViewTerm.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(text: String?): Boolean {
                text?.let {
                    hideKeyboard(searchViewTerm)
                    trackViewModel.fetchData(text)
                }
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return true
            }
        })

        trackViewModel.data.observe(this, Observer { trackDtoList ->
            recyclerViewTracks.layoutManager = LinearLayoutManager(this);
            // only create and set a new adapter if there isn't already one
            mAdapter?.let {
                mAdapter?.updateData(trackDtoList)
            } ?: run {
                mAdapter = TrackAdapter(this, trackDtoList)
                recyclerViewTracks.adapter = mAdapter
            }
        })

        trackViewModel.loadingState.observe(this, Observer {
            manageLoading(it.status)
        })
    }

    /**
     * Manage visibilities of views depending on loading status
     */
    private fun manageLoading(state: LoadingState.Status) {
        when (state) {
            LoadingState.Status.LOADING -> {
                textViewTracks.visibility = View.GONE
                recyclerViewTracks.visibility = View.GONE
                progressBarSearch.visibility = View.VISIBLE
            }
            LoadingState.Status.SUCCESS -> {
                textViewTracks.visibility = View.VISIBLE
                recyclerViewTracks.visibility = View.VISIBLE
                progressBarSearch.visibility = View.GONE
            }
            LoadingState.Status.ERROR -> {
                textViewTracks.visibility = View.VISIBLE
                recyclerViewTracks.visibility = View.VISIBLE
                progressBarSearch.visibility = View.GONE
            }
        }
    }

    /**
     * Hide Keyboard
     */
    fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
