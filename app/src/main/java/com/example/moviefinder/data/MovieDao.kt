package com.example.moviefinder.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MovieDao {

    /**
     * Select movies where a given string is contained in actor column
     * with ignore case using COLLATE NOCASE
     * @References
     * || - https://www.sqlitetutorial.net/sqlite-string-functions/sqlite-concat/
     * NOCASE - https://www.w3resource.com/sqlite/sqlite-collating-function-or-sequence.php
     */
    @Query("SELECT * FROM movies WHERE actors LIKE '%' || :term || '%' COLLATE NOCASE")
    suspend fun searchActor(term: String): List<Movie>

    // Insert one or multiple records. Replace if already exists
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg movie: Movie)

}