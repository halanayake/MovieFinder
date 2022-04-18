package com.example.moviefinder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.room.Room
import com.example.moviefinder.data.AppDatabase
import com.example.moviefinder.service.Util
import kotlinx.coroutines.*
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private var isSpinner = false
    private var isFeedback = false
    private val feedbackMsg = "Movies successfully added to the database."

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        try {
            val savedSpinner = savedInstanceState?.getBoolean("isSpinner")
            isSpinner = (savedSpinner != null && savedSpinner == true)
            val savedFeedback = savedInstanceState?.getBoolean("isFeedback")
            isFeedback = (savedFeedback != null && savedFeedback == true)
            val mainLayout = findViewById<LinearLayout>(R.id.main_layout)
            mainLayout.post {
                showPopups()
            }
        } catch (e:Exception) {
            Toast.makeText(this, "Application recovered from an error.", Toast.LENGTH_SHORT).show()
            Log.e("MANUAL_LOG", e.stackTraceToString())
        }
    }

    private fun showPopups() {
        if (isSpinner) {
            Util.showSpinner(this.layoutInflater)
        }
        if (isFeedback) {
            Util.showFeedback(this.layoutInflater, feedbackMsg)
        }
    }

    override fun onSaveInstanceState(state: Bundle) {
        super.onSaveInstanceState(state)
        state.putBoolean("isSpinner", Util.isSpinnerVisible())
        state.putBoolean("isFeedback", Util.isFeedbackVisible())
        Util.hideSpinner()
        Util.hideFeedback()
    }

    override fun onDestroy() {
        super.onDestroy()
        Util.hideSpinner()
        Util.hideFeedback()
    }

    fun saveMoviesToDb(view: View) {
        try {
            val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            Util.showSpinner(inflater)
            val db = Room.databaseBuilder(this, AppDatabase::class.java, "movie_database").build()
            val movieDao = db.movieDao()
            val scope = CoroutineScope(Dispatchers.IO)
            scope.launch {
                val source = applicationContext.assets.open("movies.json").bufferedReader()
                    .use { reader ->
                        reader.readLines()
                    }
                var jsonText = ""
                for (src: String in source) {
                    jsonText += src
                }
                val movieArray = Util.movieArrayJsonParser(jsonText)
                movieDao.insertAll(*movieArray)
                withContext(Dispatchers.Main) {
                    launch {
                        Util.hideSpinner()
                        Util.showFeedback(inflater, feedbackMsg)
                    }
                }
            }
        } catch (e: Exception) {
            Util.hideSpinner()
            Util.hideFeedback()
            Toast.makeText(this, "Application recovered from an error.", Toast.LENGTH_SHORT).show()
            Log.e("MANUAL_LOG", e.stackTraceToString())
        }
    }

    fun searchMovies(view: View) {
        val intent = Intent(this, SearchMovies::class.java)
        startActivity(intent)
    }

    fun searchActors(view: View) {
        val intent = Intent(this, SearchActivity::class.java)
        intent.putExtra("isOnline", false)
        startActivity(intent)
    }

    fun onlineSearch(view: View) {
        val intent = Intent(this, SearchActivity::class.java)
        intent.putExtra("isOnline", true)
        startActivity(intent)
    }

}