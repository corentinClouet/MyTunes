package com.coreclouet.mytunes.model

import androidx.room.Embedded
import androidx.room.Relation

data class ArtistWithCollectionAndTracks(
    @Embedded val artist: Artist,
    @Relation(
        entity = Collection::class,
        parentColumn = "id",
        entityColumn = "artistId"
    )
    val collections: List<CollectionWithTracks>
)