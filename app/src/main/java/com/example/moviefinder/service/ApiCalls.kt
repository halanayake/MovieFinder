package com.example.moviefinder.service

import android.util.Log
import com.example.moviefinder.data.Movie
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class ApiCalls {

    private val BASE_API = "http://www.omdbapi.com/?apikey=577bdecf&"

    suspend fun getMovieByName(name: String): Movie? {
        val stringBuilder = StringBuilder()
        val apiUrl = BASE_API + "t=" + name
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

}