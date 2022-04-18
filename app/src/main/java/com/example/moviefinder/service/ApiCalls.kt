package com.example.moviefinder.service

import com.example.moviefinder.data.Movie
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class ApiCalls {

    // OMDBApi url with token
    private val baseUrl = "http://www.omdbapi.com/?apikey=577bdecf&"

    // Accept a string and pass it as the movie title parameter to get matching movies from webservice
    fun getMovieByName(name: String): Movie? {
        val stringBuilder = StringBuilder()
        // 't' is the url parameter for movie titles
        val apiUrl = baseUrl + "t=" + name
        val url = URL(apiUrl)
        val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
        try {
            // response code must be 200
            if (urlConnection.responseCode == 200) {
                val reader = BufferedReader(InputStreamReader(urlConnection.inputStream))
                // Read response line by line and build a complete string
                var responseStr: String? = reader.readLine()
                while (responseStr != null) {
                    stringBuilder.append(responseStr + "\n")
                    responseStr = reader.readLine()
                }
                // Create a JSONObject using the string
                val jsonObject = JSONObject(stringBuilder.toString())
                // Api adds a field called "Response" to mark successful responses
                return if (jsonObject.getBoolean("Response")) {
                    // Map to a movie object using a common method and return
                    Util.jsonObjToMovie(jsonObject)
                } else {
                    null
                }
            } else {
                // Throw an error if the response code is not 200
                throw Exception("Server responded with status : " + urlConnection.responseCode)
            }
        } finally {
            urlConnection.disconnect()
        }
    }

    // Return a specific movie which has the specific movie/imdb id
    fun getMovieById(movieId: String): Movie? {
        val stringBuilder = StringBuilder()
        // 'i' is the url parameter for movie imdbIds
        val apiUrl = baseUrl + "i=" + movieId
        val url = URL(apiUrl)
        val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
        try {
            if (urlConnection.responseCode == 200) {
                val reader = BufferedReader(InputStreamReader(urlConnection.inputStream))
                var responseStr: String? = reader.readLine()
                while (responseStr != null) {
                    stringBuilder.append(responseStr + "\n")
                    responseStr = reader.readLine()
                }
                val jsonObject = JSONObject(stringBuilder.toString())
                return if (jsonObject.getBoolean("Response")) {
                    Util.jsonObjToMovie(jsonObject)
                } else {
                    null
                }
            } else {
                throw Exception("Server responded with status : " + urlConnection.responseCode)
            }
        } finally {
            urlConnection.disconnect()
        }
    }

    // Return a list movies which matches the parameter as a part of / as the title.
    fun searchMovieByName(name: String): ArrayList<Movie>? {
        val stringBuilder = StringBuilder()
        // 'type=movie' to get only movies. 's' is the parameter for search
        // OMDBApi doesn't support leading wildcard
        // hence only trailing wildcard works. Source - https://github.com/omdbapi/OMDb-API/issues/108
        val apiUrl = baseUrl + "type=movie&s=*$name*"
        val url = URL(apiUrl)
        val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
        try {
            if (urlConnection.responseCode == 200) {
                val reader = BufferedReader(InputStreamReader(urlConnection.inputStream))
                var responseStr: String? = reader.readLine()
                while (responseStr != null) {
                    stringBuilder.append(responseStr + "\n")
                    responseStr = reader.readLine()
                }
                val jsonObject = JSONObject(stringBuilder.toString())
                return if (jsonObject.getBoolean("Response")) {
                    // This api return only a limited number of attributes hence a special parser is used
                    val temp = Util.shortMovieArrayJsonParser(jsonObject.getString("Search"))
                    val array = ArrayList<Movie>()
                    array.addAll(temp)
                    array
                } else {
                    null
                }
            } else {
                throw Exception("Server responded with status : " + urlConnection.responseCode)
            }
        } finally {
            urlConnection.disconnect()
        }
    }

}