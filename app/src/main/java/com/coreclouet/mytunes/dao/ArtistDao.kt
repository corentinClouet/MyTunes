package com.coreclouet.mytunes.dao

import androidx.room.*
import com.coreclouet.mytunes.model.Artist
import com.coreclouet.mytunes.model.ArtistWithCollectionAndTracks

@Dao
interface ArtistDao {
    @Query("SELECT * FROM artist")
    fun getAll(): List<Artist>

    @Query("SELECT * FROM artist WHERE id = :artistId")
    fun loadById(artistId: Long): Artist

    @Query("SELECT * FROM artist WHERE search_term = :term")
    suspend fun loadByTerm(term: String): List<ArtistWithCollectionAndTracks>?

    @Transaction
    @Query("SELECT * FROM artist WHERE id = :artistId")
    suspend fun getArtistWithCollectionsAndTracks(artistId: Long): ArtistWithCollectionAndTracks

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(artist: Artist)
}