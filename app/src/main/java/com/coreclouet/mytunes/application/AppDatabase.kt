package com.coreclouet.mytunes.application

import androidx.room.Database
import androidx.room.RoomDatabase
import com.coreclouet.mytunes.dao.ArtistDao
import com.coreclouet.mytunes.dao.CollectionDao
import com.coreclouet.mytunes.dao.TrackDao
import com.coreclouet.mytunes.model.Artist
import com.coreclouet.mytunes.model.Collection
import com.coreclouet.mytunes.model.Track

@Database(entities = [Artist::class, Collection::class, Track::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract val artistDao: ArtistDao
    abstract val collectionDao: CollectionDao
    abstract val trackDao: TrackDao
}