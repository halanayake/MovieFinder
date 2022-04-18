package com.example.moviefinder

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.room.Room
import com.example.moviefinder.data.AppDatabase
import com.example.moviefinder.data.Movie
import com.example.moviefinder.service.ApiCalls
import com.example.moviefinder.service.Util
import kotlinx.coroutines.*

class SearchActivity : AppCompatActivity() {

    private var isOnline = false

    class MyAdapter(
        private val context: Context,
        private val dataSource: ArrayList<Movie>
    ) : BaseAdapter() {

        private val inflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        override fun getCount(): Int {
            return dataSource.size
        }

        override fun getItem(position: Int): Any {
            return dataSource[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            return if (convertView == null) {
                val rowView = inflater.inflate(R.layout.list_item, parent, false)
                val movie = getItem(position) as Movie
                rowView.findViewById<TextView>(R.id.list_name).text = movie.title
                if (movie.actors != null) {
                    rowView.findViewById<TextView>(R.id.list_actors).text = movie.actors
                } else {
                    rowView.findViewById<TextView>(R.id.list_actors).text = movie.year
                }
                rowView
            } else {
                convertView
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        isOnline = intent.getBooleanExtra("isOnline", false)
    }

    override fun onDestroy() {
        super.onDestroy()
        Util.hideSpinner()
    }

    fun searchMovie(view: View) {
        val inputMethodManager =
            this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        val searchText = findViewById<EditText>(R.id.search_field).text.toString()
        if (searchText != null && searchText.trim() != "") {
            if (isOnline) {
                searchTitleOnline(searchText)
            } else {
                searchActors(searchText)
            }
        } else {
            Toast.makeText(this, "Please enter a valid search term.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun searchActors(searchText: String) {
        val scope = CoroutineScope(Dispatchers.IO)
        val db = Room.databaseBuilder(this, AppDatabase::class.java, "movie_database").build()
        val movieDao = db.movieDao()
        Util.showSpinner(layoutInflater)
        scope.launch {
            val movies = ArrayList<Movie>()
            movies.addAll(movieDao.searchActor(searchText))
            withContext(Dispatchers.Main) {
                setDataToList(movies)
            }
        }
    }

    private fun searchTitleOnline(searchText: String) {
        Util.showSpinner(this.layoutInflater)
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            val apiCalls = ApiCalls()
            try {
                val movies = apiCalls.searchMovieByName(searchText)
                withContext(Dispatchers.Main) {
                    if (movies != null && movies.isNotEmpty()) {
                        setDataToList(movies)
                        Util.hideSpinner()
                    } else {
                        setDataToList(ArrayList<Movie>())
                        Util.hideSpinner()
                        Toast.makeText(
                            applicationContext,
                            "No movie found for : $searchText",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("MANUAL_LOG", e.stackTraceToString())
                withContext(Dispatchers.Main) {
                    Util.hideSpinner()
                    Toast.makeText(
                        applicationContext,
                        "Error when contacting server.",
                        Toast.LENGTH_SHORT
                    ).show()
                    setDataToList(ArrayList<Movie>())
                }
            }
        }
    }

    private suspend fun setDataToList(movies: ArrayList<Movie>) {
        val listView = findViewById<ListView>(R.id.list_view)
        listView.adapter = MyAdapter(applicationContext, movies)
        listView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                val selectedMovie = parent.getItemAtPosition(position) as Movie
                Log.e("XXX", selectedMovie.imdbId)
            }
        withContext(Dispatchers.Main) {
            if (movies.isEmpty()) {
                Toast.makeText(applicationContext, "No movies found!", Toast.LENGTH_SHORT).show()
            }
            Util.hideSpinner()
        }
    }

}