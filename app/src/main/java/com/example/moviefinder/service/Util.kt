package com.example.moviefinder.service

import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import com.example.moviefinder.R
import com.example.moviefinder.data.Movie
import org.json.JSONArray
import org.json.JSONObject

class Util {
    // Create companion object so methods can be called without creating an object
    companion object {

        private var spinnerPopupWindow: PopupWindow? = null
        private var feedbackPopupWindow: PopupWindow? = null

        // Create and display a spinner if currently not showing
        fun showSpinner(inflater: LayoutInflater) {
            if (!isSpinnerVisible()) {
                val popupView: View = inflater.inflate(R.layout.spinner, null)
                val width = LinearLayout.LayoutParams.MATCH_PARENT
                val height = LinearLayout.LayoutParams.MATCH_PARENT
                spinnerPopupWindow = PopupWindow(popupView, width, height)
                spinnerPopupWindow!!.showAtLocation(popupView, Gravity.CENTER, 0, 0)
            }
        }

        // Hide spinner if showing
        fun hideSpinner() {
            spinnerPopupWindow?.dismiss()
        }

        // Check if spinner is visible
        fun isSpinnerVisible(): Boolean {
            return if (spinnerPopupWindow != null) {
                spinnerPopupWindow!!.isShowing
            } else {
                false
            }
        }

        // Create and display a feedback with a custom message if currently not showing
        fun showFeedback(inflater: LayoutInflater, message: String) {
            if (!isFeedbackVisible()) {
                val popupView: View = inflater.inflate(R.layout.feedback, null)
                val width = LinearLayout.LayoutParams.MATCH_PARENT
                val height = LinearLayout.LayoutParams.MATCH_PARENT
                val focusable = true
                feedbackPopupWindow = PopupWindow(popupView, width, height, focusable)
                feedbackPopupWindow!!.contentView.findViewById<TextView>(R.id.feedback_message).text =
                    message
                popupView.setOnTouchListener { v, event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            v.performClick()
                        }
                    }
                    feedbackPopupWindow!!.dismiss()
                    true
                }
                feedbackPopupWindow!!.showAtLocation(popupView, Gravity.CENTER, 0, 0)
            }
        }

        // Hide feedback if showing
        fun hideFeedback() {
            feedbackPopupWindow?.dismiss()
        }

        // Check if feedback is visible
        fun isFeedbackVisible(): Boolean {
            return if (feedbackPopupWindow != null) {
                feedbackPopupWindow!!.isShowing
            } else {
                false
            }
        }

        // Parse a json array of objects and return a movie array
        fun movieArrayJsonParser(json: String): Array<Movie> {
            val jsonArray = JSONArray(json)
            val movieArray: MutableList<Movie> = ArrayList()
            for (i in 0 until jsonArray.length()) {
                val jsonObj = jsonArray[i] as JSONObject
                val movie = jsonObjToMovie(jsonObj)
                movieArray.add(movie)
            }
            return movieArray.toTypedArray()
        }

        // Parse a json array of objects with limited attributes and return a movie array
        fun shortMovieArrayJsonParser(json: String): Array<Movie> {
            val jsonArray = JSONArray(json)
            val movieArray: MutableList<Movie> = ArrayList()
            for (i in 0 until jsonArray.length()) {
                val jsonObj = jsonArray[i] as JSONObject
                val movie = jsonObjToMovieShort(jsonObj)
                movieArray.add(movie)
            }
            return movieArray.toTypedArray()
        }

        // Map a jsonObject to a Movie object
        fun jsonObjToMovie(jsonObject: JSONObject): Movie {
            return Movie(
                jsonObject.getString("imdbID"),
                jsonObject.getString("Title"),
                jsonObject.getString("Year"),
                jsonObject.getString("Rated"),
                jsonObject.getString("Released"),
                jsonObject.getString("Runtime"),
                jsonObject.getString("Genre"),
                jsonObject.getString("Director"),
                jsonObject.getString("Writer"),
                jsonObject.getString("Actors"),
                jsonObject.getString("Plot")
            )
        }

        // Map a jsonObject to a Movie object with limited attributes
        private fun jsonObjToMovieShort(jsonObject: JSONObject): Movie {
            return Movie(
                jsonObject.getString("imdbID"),
                jsonObject.getString("Title"),
                jsonObject.getString("Year"),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
            )
        }

    }
}