package com.coreclouet.mytunes.repository

import com.coreclouet.mytunes.converter.TrackConverter
import com.coreclouet.mytunes.dao.ArtistDao
import com.coreclouet.mytunes.dao.CollectionDao
import com.coreclouet.mytunes.dao.TrackDao
import com.coreclouet.mytunes.model.*
import com.coreclouet.mytunes.model.Collection
import com.coreclouet.mytunes.model.dto.ResultItem
import com.coreclouet.mytunes.model.dto.SearchResult
import com.coreclouet.mytunes.model.dto.TrackDto
import com.coreclouet.mytunes.remote.ItunesApi
import com.coreclouet.mytunes.util.Resource
import retrofit2.Response
import java.util.ArrayList

class SongRepository(
    private val api: ItunesApi,
    private val artistDao: ArtistDao,
    private val collectionDao: CollectionDao,
    private val trackDao: TrackDao
) {

    /**
     * Search tracks corresponding to the term
     */
    suspend fun searchTracks(term: String): Resource<List<TrackDto>> {
        val artistsWithCollectionAndTracks: List<ArtistWithCollectionAndTracks>? = artistDao.loadByTerm(term)
        //if data exist in databse, convert data and return result
        if (artistsWithCollectionAndTracks != null && artistsWithCollectionAndTracks.isNotEmpty()) {
            val trackDtoList: List<TrackDto> =
                TrackConverter.convertArtistWithCollectionsAndTracksToTrackDto(
                    artistsWithCollectionAndTracks
                )
            return Resource(Resource.Status.SUCCESS, trackDtoList, null)
        }
        //else launch remote search
        val searchResult: Response<SearchResult> = api.searchTracks(term)
        return if (searchResult.isSuccessful && searchResult.body() != null) {
            saveSearchResult(searchResult.body(), term)
            val trackDtoList: List<TrackDto> =
                TrackConverter.convertSearchResultToTrackDto(searchResult.body())
            Resource(Resource.Status.SUCCESS, trackDtoList, null)
        } else {
            Resource(Resource.Status.ERROR, ArrayList(), searchResult.message())
        }
    }

    /**
     * Save search result by parsing the result to artist, collection and tracks
     */
    private suspend fun saveSearchResult(searchResult: SearchResult?, term: String) {
        if (searchResult?.results == null) return

        searchResult.results.forEach start@{
            if (it == null || verifyMissingEssentialsData(it)) {
                return@start
            }

            //save artist
            val artist = Artist(
                id = it.artistId!!,
                name = it.artistName!!,
                viewUrl = it.artistViewUrl ?: "",
                searchTerm = term
            )
            artistDao.insert(artist)

            //save collection
            val collection = Collection(
                id = it.collectionId!!,
                artistId = it.artistId,
                name = it.collectionName!!,
                viewUrl = it.collectionViewUrl ?: "",
                artworkUrl30 = it.artworkUrl30 ?: "",
                artworkUrl60 = it.artworkUrl60 ?: "",
                artworkUrl100 = it.artworkUrl100 ?: "",
                releaseDate = it.releaseDate ?: "",
                trackCount = it.trackCount ?: 0,
                primaryGenreName = it.primaryGenreName ?: ""
            )
            collectionDao.insert(collection)

            //save track
            val track = Track(
                id = it.trackId!!,
                collectionId = it.collectionId,
                name = it.trackName!!,
                previewUrl = it.previewUrl!!,
                number = it.trackNumber ?: 0,
                timeMillis = it.trackTimeMillis!!,
                streamable = it.isStreamable!!
            )
            trackDao.insert(track)
        }
    }

    /**
     * Verify essentials data before saving track
     */
    private fun verifyMissingEssentialsData(result: ResultItem?): Boolean {
        return (result?.artistId == null ||
                result.artistName == null ||
                result.collectionId == null ||
                result.collectionName == null ||
                result.trackId == null ||
                result.trackName == null ||
                result.previewUrl == null ||
                result.trackTimeMillis == null ||
                result.isStreamable == null
                )
    }
}