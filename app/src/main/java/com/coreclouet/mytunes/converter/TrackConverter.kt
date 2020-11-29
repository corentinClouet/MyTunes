package com.coreclouet.mytunes.converter

import com.coreclouet.mytunes.model.ArtistWithCollectionAndTracks
import com.coreclouet.mytunes.model.dto.SearchResult
import com.coreclouet.mytunes.model.dto.TrackDto

class TrackConverter {
    companion object {
        /**
         * Convert detail object ArtistWithCollectionAndTracks to List<TrackDto>
         */
        fun convertArtistWithCollectionsAndTracksToTrackDto(artistWithCollectionAndTracks: List<ArtistWithCollectionAndTracks>?): List<TrackDto> {
            val trackDtoList: MutableList<TrackDto> = ArrayList()
            if (artistWithCollectionAndTracks == null) return trackDtoList

            for (index in artistWithCollectionAndTracks.indices) {
                artistWithCollectionAndTracks[index].collections.forEach { collectionWithTracks ->
                    collectionWithTracks.tracks.forEach { track ->
                        val trackDto = TrackDto(
                            artistId = artistWithCollectionAndTracks[index].artist.id,
                            collectionId = collectionWithTracks.collection.id,
                            trackId = track.id,
                            trackName = track.name,
                            artistName = artistWithCollectionAndTracks[index].artist.name,
                            collectionName = collectionWithTracks.collection.name,
                            artworkUrl = collectionWithTracks.collection.artworkUrl100
                        )
                        trackDtoList.add(trackDto)
                    }
                }
            }
            return trackDtoList
        }

        /**
         * Convert SearchResult to List<TrackDto>
         */
        fun convertSearchResultToTrackDto(searchResult: SearchResult?): List<TrackDto> {
            val trackDtoList: MutableList<TrackDto> = ArrayList()
            searchResult?.results?.forEach start@{
                if (it == null) {
                    return@start
                }
                val trackDto = TrackDto(
                    artistId = it.artistId ?: 0,
                    collectionId = it.collectionId ?: 0,
                    trackId = it.trackId ?: 0,
                    trackName = it.trackName ?: "",
                    artistName = it.artistName ?: "",
                    collectionName = it.collectionName ?: "",
                    artworkUrl = it.artworkUrl100 ?: it.artworkUrl60 ?: it.artworkUrl30 ?: ""
                )
                trackDtoList.add(trackDto)
            }
            return trackDtoList
        }
    }
}