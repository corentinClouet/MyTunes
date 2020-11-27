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

    @Transaction
    @Query("SELECT * FROM artist WHERE id = :artistId")
    fun getArtistWithCollectionsAndTracks(artistId: Long): List<ArtistWithCollectionAndTracks>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(artist: Artist)
}