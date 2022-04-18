package com.example.moviefinder

import android.content.Context
import android.content.Intent
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
    private var movies: ArrayList<Movie>? = ArrayList()

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
            val rowView = inflater.inflate(R.layout.list_item, parent, false)
            val movie = getItem(position) as Movie
            rowView.findViewById<TextView>(R.id.list_name).text = movie.title
            if (movie.actors != null) {
                rowView.findViewById<TextView>(R.id.list_actors).text = movie.actors
            } else {
                rowView.findViewById<TextView>(R.id.list_actors).text = movie.year
            }
            return rowView
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        isOnline = intent.getBooleanExtra("isOnline", false)
        val searchInput = findViewById<EditText>(R.id.search_field)
        if (isOnline) {
            searchInput.hint = "Enter movie title"
        } else {
            searchInput.hint = "Enter actor name"
        }
        if (savedInstanceState != null) {
            val resultSize = savedInstanceState.getInt("result_length")
            if (resultSize != null && resultSize > 0) {
                movies = ArrayList()
                for (i in 0..resultSize) {
                    val id = savedInstanceState.getString("result_imdbId_$i")
                    if (id != null) {
                        movies!!.add(
                            Movie(
                                id,
                                savedInstanceState.getString("result_title_$i"),
                                savedInstanceState.getString("result_year_$i"),
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                            )
                        )
                    }
                }
                setDataToList(movies!!)
            }
        }

    }

    override fun onSaveInstanceState(state: Bundle) {
        super.onSaveInstanceState(state)
        if (movies != null && movies!!.isNotEmpty()) {
            state.putInt("result_length", movies!!.size)
            for ((index, value) in movies!!.withIndex()) {
                state.putString("result_imdbId_$index", value.imdbId)
                state.putString("result_title_$index", value.title)
                state.putString("result_year_$index", value.year)
            }
        }
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
            movies = ArrayList()
            movies!!.addAll(movieDao.searchActor(searchText))
            withContext(Dispatchers.Main) {
                setDataToList(movies!!)
            }
        }
    }

    private fun searchTitleOnline(searchText: String) {
        Util.showSpinner(this.layoutInflater)
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            val apiCalls = ApiCalls()
            try {
                movies = apiCalls.searchMovieByName(searchText)
                withContext(Dispatchers.Main) {
                    if (movies != null && movies!!.isNotEmpty()) {
                        setDataToList(movies!!)
                        Util.hideSpinner()
                    } else {
                        setDataToList(ArrayList())
                        Util.hideSpinner()
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
                    setDataToList(ArrayList())
                }
            }
        }
    }

    private fun setDataToList(movies: ArrayList<Movie>) {
        val listView = findViewById<ListView>(R.id.list_view)
        listView.adapter = MyAdapter(applicationContext, movies)
        listView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                val selectedMovie = parent.getItemAtPosition(position) as Movie
                val intent = Intent(this, SearchMovies::class.java)
                intent.putExtra("movie_id", selectedMovie.imdbId)
                startActivity(intent)
            }
        if (movies.isEmpty()) {
            Toast.makeText(applicationContext, "No movies found!", Toast.LENGTH_SHORT).show()
        }
        Util.hideSpinner()
    }

}