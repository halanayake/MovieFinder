package com.example.moviefinder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.room.Room
import com.example.moviefinder.data.AppDatabase
import com.example.moviefinder.data.Movie
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.json.JSONObject
import java.nio.charset.Charset
import kotlin.reflect.typeOf

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = Room.databaseBuilder(this, AppDatabase::class.java, "movie_database").build()
        val movieDao = db.movieDao()

        val source = applicationContext.assets.open("movies.json").bufferedReader().use {
            reader -> reader.readLines()
        }
        var text = ""
        for (src: String in source) {
            text += src
        }

        Log.e("XXX>>", text)

        val test = JSONArray(text)
        for (i in 0 until test.length()) {
            val jsonObj = test[i] as JSONObject;
            Log.e("XXX>>", jsonObj.getString("Plot"))
        }

        runBlocking {
            launch {
                val movies = movieDao.getAllMovies();
                Log.e("XXX>", movies.toString());
            }
        }

    }
}