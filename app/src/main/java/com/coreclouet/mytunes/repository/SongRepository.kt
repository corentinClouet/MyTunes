package com.coreclouet.mytunes.repository

import com.coreclouet.mytunes.dao.ArtistDao
import com.coreclouet.mytunes.dao.CollectionDao
import com.coreclouet.mytunes.dao.TrackDao
import com.coreclouet.mytunes.model.*
import com.coreclouet.mytunes.model.Collection
import com.coreclouet.mytunes.remote.ItunesApi

class SongRepository(private val api: ItunesApi, private val artistDao: ArtistDao, private val collectionDao: CollectionDao, private val trackDao: TrackDao) {

    suspend fun searchTracks(term: String) = api.searchTracks(term)

    suspend fun getArtistWithCollectionsAndTracks(artistId: Long): ArtistWithCollectionAndTracks {
       return artistDao.getArtistWithCollectionsAndTracks(artistId)
    }

    suspend fun saveSearchResult(searchResult: SearchResult?) {
        if (searchResult?.tracks == null) return

        searchResult.tracks.forEach {
            if (it != null) {
                //save artist
                val artist = Artist(
                    it.artistId,
                    it.artistName,
                    it.artistViewUrl
                )
                artistDao.insert(artist)

                //save collection
                val collection = Collection(
                    it.collectionId,
                    it.artistId,
                    it.collectionName,
                    it.collectionViewUrl,
                    it.artworkUrl30,
                    it.artworkUrl60,
                    it.artworkUrl100,
                    it.releaseDate,
                    it.trackCount,
                    it.primaryGenreName
                )
                collectionDao.insert(collection)

                //save track
                val track = Track(
                    it.trackId,
                    it.collectionId,
                    it.trackName,
                    it.previewUrl,
                    it.trackNumber,
                    it.trackTimeMillis,
                    it.isStreamable
                )
                trackDao.insert(track)
            }

        }
    }
}