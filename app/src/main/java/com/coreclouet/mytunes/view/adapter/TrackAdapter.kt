package com.coreclouet.mytunes.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.coreclouet.mytunes.R
import com.coreclouet.mytunes.model.dto.TrackDto
import com.squareup.picasso.Picasso

class TrackAdapter(private val context: Context, private var dataSet: List<TrackDto>) :
    RecyclerView.Adapter<TrackAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.textViewTrackTitleItem)
        val artistCollection: TextView = view.findViewById(R.id.textViewTrackArtistCollectionItem)
        val collectionImg: ImageView = view.findViewById(R.id.imageViewCollectionItem)
        val playImg: ImageView = view.findViewById(R.id.imageViewPlayItem)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.track_row_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.title.text = dataSet[position].trackName
        viewHolder.artistCollection.text = context.getString(
            R.string.artist_collection,
            dataSet[position].artistName,
            dataSet[position].collectionName
        )
        Picasso.get()
            .load(dataSet[position].artworkUrl)
            .placeholder(R.drawable.ic_music_note)
            .error(R.drawable.ic_music_note)
            .into(viewHolder.collectionImg)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int = dataSet.size

    fun updateData(data: List<TrackDto>) {
        dataSet = data
        notifyDataSetChanged()
    }

}