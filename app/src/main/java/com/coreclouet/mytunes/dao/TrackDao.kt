package com.coreclouet.mytunes.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.coreclouet.mytunes.model.Artist
import com.coreclouet.mytunes.model.Track

@Dao
interface TrackDao {
    @Query("SELECT * FROM track")
    suspend fun getAll(): List<Track>

    @Query("SELECT * FROM track WHERE id = :trackId")
    fun loadById(trackId: Long): Track

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(track: Track)
}