package com.coreclouet.mytunes.model

import androidx.room.Embedded
import androidx.room.Relation

data class CollectionWithTracks(
    @Embedded val collection: Collection,
    @Relation(
        parentColumn = "id",
        entityColumn = "collectionId"
    )
    val tracks: List<Track>
)