package com.example.moviefinder.data

import androidx.room.Database
import androidx.room.RoomDatabase

// Database class export a single dao as only one table is available.
@Database(entities = [Movie::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
}