package com.coreclouet.mytunes.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.coreclouet.mytunes.model.Artist
import com.coreclouet.mytunes.model.Collection

@Dao
interface CollectionDao {
    @Query("SELECT * FROM collection")
    fun getAll(): List<Collection>

    @Query("SELECT * FROM collection WHERE id = :collectionId")
    fun loadById(collectionId: Long): Collection

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(collection: Collection)
}