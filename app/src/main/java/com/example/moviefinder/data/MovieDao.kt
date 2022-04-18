package com.example.moviefinder.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MovieDao {

    // https://www.sqlitetutorial.net/sqlite-string-functions/sqlite-concat/
    // https://www.w3resource.com/sqlite/sqlite-collating-function-or-sequence.php
    @Query("SELECT * FROM movies WHERE actors LIKE '%' || :term || '%' COLLATE NOCASE")
    suspend fun searchActor(term: String): List<Movie>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg movie: Movie)

}