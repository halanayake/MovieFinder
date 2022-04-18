package com.example.moviefinder.service

import com.example.moviefinder.data.Movie
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class ApiCalls {

    private val baseUrl = "http://www.omdbapi.com/?apikey=577bdecf&"

    fun getMovieByName(name: String): Movie? {
        val stringBuilder = StringBuilder()
        val apiUrl = baseUrl + "t=" + name
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

    fun getMovieById(movieId: String): Movie? {
        val stringBuilder = StringBuilder()
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

    fun searchMovieByName(name: String): ArrayList<Movie>? {
        val stringBuilder = StringBuilder()
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