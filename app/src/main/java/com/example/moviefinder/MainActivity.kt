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
            // Load values from state and set to variables
            val savedSpinner = savedInstanceState?.getBoolean("isSpinner")
            isSpinner = (savedSpinner != null && savedSpinner == true)
            val savedFeedback = savedInstanceState?.getBoolean("isFeedback")
            isFeedback = (savedFeedback != null && savedFeedback == true)
            val mainLayout = findViewById<LinearLayout>(R.id.main_layout)
            // Wait until layout becomes available to display popups
            mainLayout.post {
                showPopups()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Application recovered from an error.", Toast.LENGTH_SHORT).show()
            Log.e("MANUAL_LOG", e.stackTraceToString())
        }
    }

    private fun showPopups() {
        // Check variables and add popup windows accordingly
        if (isSpinner) {
            // Call util method to display spinner
            Util.showSpinner(this.layoutInflater)
        }
        if (isFeedback) {
            Util.showFeedback(this.layoutInflater, feedbackMsg)
        }
    }

    // Save instance before destroy
    override fun onSaveInstanceState(state: Bundle) {
        super.onSaveInstanceState(state)
        state.putBoolean("isSpinner", Util.isSpinnerVisible())
        state.putBoolean("isFeedback", Util.isFeedbackVisible())
        // Remove popupWindows to prevent leaks
        Util.hideSpinner()
        Util.hideFeedback()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Remove popupWindows to prevent leaks
        Util.hideSpinner()
        Util.hideFeedback()
    }

    // Called by Add Movies to DB button
    fun saveMoviesToDb(view: View) {
        try {
            val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            Util.showSpinner(inflater)
            // Create database objects to access database
            val db = Room.databaseBuilder(this, AppDatabase::class.java, "movie_database").build()
            val movieDao = db.movieDao()
            // Create a coroutine scope in IO thread. Without this UI will freeze
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
                // After executing on IO thread switch back to main thread to update UI
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

    // called by Search for Movies button
    fun searchMovies(view: View) {
        val intent = Intent(this, SearchMovies::class.java)
        startActivity(intent)
    }

    // called by Search for Actors button.
    fun searchActors(view: View) {
        val intent = Intent(this, SearchActivity::class.java)
        // An intent extra is added as Search Movies Online also uses the same activity
        intent.putExtra("isOnline", false)
        startActivity(intent)
    }

    // called by Search Movies Online button.
    fun onlineSearch(view: View) {
        val intent = Intent(this, SearchActivity::class.java)
        intent.putExtra("isOnline", true)
        startActivity(intent)
    }

}