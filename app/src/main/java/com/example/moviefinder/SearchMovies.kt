package com.example.moviefinder

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.room.Room
import com.example.moviefinder.data.AppDatabase
import com.example.moviefinder.data.Movie
import com.example.moviefinder.service.ApiCalls
import com.example.moviefinder.service.Util
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchMovies : AppCompatActivity() {

    private var movie: Movie? = null
    private val feedbackMsg = "Movie successfully added to the database."
    private var isSpinner = false
    private var isFeedback = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_movies)
        val savedSpinner = savedInstanceState?.getBoolean("isSpinner")
        isSpinner = (savedSpinner != null && savedSpinner == true)
        val savedFeedback = savedInstanceState?.getBoolean("isFeedback")
        isFeedback = (savedFeedback != null && savedFeedback == true)
        val mainLayout = findViewById<LinearLayout>(R.id.activity_search_movies)

        val imdbId = savedInstanceState?.getString("movie_imdbId")
        if (imdbId != null && imdbId.trim() != "") {
            movie = Movie(
                imdbId,
                savedInstanceState.getString("movie_title"),
                savedInstanceState.getString("movie_year"),
                savedInstanceState.getString("movie_rated"),
                savedInstanceState.getString("movie_released"),
                savedInstanceState.getString("movie_runtime"),
                savedInstanceState.getString("movie_genre"),
                savedInstanceState.getString("movie_director"),
                savedInstanceState.getString("movie_writer"),
                savedInstanceState.getString("movie_actors"),
                savedInstanceState.getString("movie_plot")
            )
            val layout = findViewById<LinearLayout>(R.id.movie_detail_layout)
            val saveBtn = findViewById<Button>(R.id.save_to_db)
            layout.visibility = View.VISIBLE
            saveBtn.isEnabled = true
            setViewFields()
        }

        mainLayout.post {
            showPopups()
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

    override fun onDestroy() {
        super.onDestroy()
        Util.hideSpinner()
        Util.hideFeedback()
    }

    override fun onSaveInstanceState(state: Bundle) {
        super.onSaveInstanceState(state)
        state.putBoolean("isSpinner", Util.isSpinnerVisible())
        state.putBoolean("isFeedback", Util.isFeedbackVisible())
        if (movie != null) {
            state.putString("movie_imdbId", movie!!.imdbId)
            state.putString("movie_title", movie!!.title)
            state.putString("movie_year", movie!!.year)
            state.putString("movie_rated", movie!!.rated)
            state.putString("movie_released", movie!!.released)
            state.putString("movie_runtime", movie!!.runtime)
            state.putString("movie_genre", movie!!.genre)
            state.putString("movie_director", movie!!.director)
            state.putString("movie_writer", movie!!.writer)
            state.putString("movie_actors", movie!!.actors)
            state.putString("movie_plot", movie!!.plot)
            setViewFields()
        }
        Util.hideSpinner()
        Util.hideFeedback()
    }

    fun searchMovie(view: View) {
        val inputMethodManager =
            this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)

        val movieName = findViewById<EditText>(R.id.movie_search_field).text.toString()
        if (movieName != null && movieName.trim() != "") {
            Util.showSpinner(this.layoutInflater)
            val scope = CoroutineScope(Dispatchers.IO)
            scope.launch {
                val apiCalls = ApiCalls()
                try {
                    movie = apiCalls.getMovieByName(movieName)
                    val layout = findViewById<LinearLayout>(R.id.movie_detail_layout)
                    val saveBtn = findViewById<Button>(R.id.save_to_db)
                    withContext(Dispatchers.Main) {
                        if (movie != null) {
                            layout.visibility = View.VISIBLE
                            saveBtn.isEnabled = true
                            setViewFields()
                            Util.hideSpinner()
                        } else {
                            layout.visibility = View.INVISIBLE
                            saveBtn.isEnabled = false
                            movie = null
                            Util.hideSpinner()
                            Toast.makeText(
                                applicationContext,
                                "No movie found for : $movieName",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } catch (e: Exception) {
                    Log.e("MANUAL_LOG", e.stackTraceToString())
                    withContext(Dispatchers.Main) {
                        val layout = findViewById<LinearLayout>(R.id.movie_detail_layout)
                        val saveBtn = findViewById<Button>(R.id.save_to_db)
                        layout.visibility = View.INVISIBLE
                        saveBtn.isEnabled = false
                        movie = null
                        Util.hideSpinner()
                        Toast.makeText(
                            applicationContext,
                            "Error when contacting server.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        } else {
            Toast.makeText(this, "Please enter a valid movie title.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setViewFields() {
        val title = findViewById<TextView>(R.id.movie_title)
        title.text = movie!!.title
        val year = findViewById<TextView>(R.id.movie_year)
        year.text = movie!!.year
        val rated = findViewById<TextView>(R.id.movie_rated)
        rated.text = movie!!.rated
        val released = findViewById<TextView>(R.id.movie_released)
        released.text = movie!!.released
        val runtime = findViewById<TextView>(R.id.movie_runtime)
        runtime.text = movie!!.runtime
        val genre = findViewById<TextView>(R.id.movie_genre)
        genre.text = movie!!.genre
        val director = findViewById<TextView>(R.id.movie_director)
        director.text = movie!!.director
        val writer = findViewById<TextView>(R.id.movie_writer)
        writer.text = movie!!.writer
        val actors = findViewById<TextView>(R.id.movie_actors)
        actors.text = movie!!.actors
        val plot = findViewById<TextView>(R.id.movie_plot)
        plot.text = movie!!.plot
    }

    fun saveMovieToDatabase(view: View) {
        if (movie != null) {
            try {
                Util.showSpinner(layoutInflater)
                val db =
                    Room.databaseBuilder(this, AppDatabase::class.java, "movie_database").build()
                val movieDao = db.movieDao()
                val scope = CoroutineScope(Dispatchers.IO)
                scope.launch {
                    movieDao.insertAll(movie!!)
                    withContext(Dispatchers.Main) {
                        launch {
                            Util.hideSpinner()
                            Util.showFeedback(layoutInflater, feedbackMsg)
                        }
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Error occurred when saving", Toast.LENGTH_SHORT).show()
                Log.e("MANUAL_LOG", e.stackTraceToString())
            }
        } else {
            // Movie should always be not null as button is disabled when it's null
            Log.e("MANUAL_LOG", "Save to db called with null movie object")
        }
    }

}