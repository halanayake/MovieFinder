package com.example.moviefinder.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// Entity class which represent a database table.
// imdbId is the primary key as every movie has a unique one.
@Entity(tableName = "movies")
data class Movie(
    @PrimaryKey val imdbId: String,
    val title: String?,
    val year: String?,
    val rated: String?,
    val released: String?,
    val runtime: String?,
    val genre: String?,
    val director: String?,
    val writer: String?,
    val actors: String?,
    val plot: String?
)