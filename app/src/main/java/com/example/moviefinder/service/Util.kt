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
    companion object {

        private var spinnerPopupWindow: PopupWindow? = null
        private var feedbackPopupWindow: PopupWindow? = null

        fun showSpinner(inflater: LayoutInflater) {
            if (!isSpinnerVisible()) {
                val popupView: View = inflater.inflate(R.layout.spinner, null)
                val width = LinearLayout.LayoutParams.MATCH_PARENT
                val height = LinearLayout.LayoutParams.MATCH_PARENT
                spinnerPopupWindow = PopupWindow(popupView, width, height)
                spinnerPopupWindow!!.showAtLocation(popupView, Gravity.CENTER, 0, 0)
            }
        }

        fun hideSpinner() {
            spinnerPopupWindow?.dismiss()
        }

        fun isSpinnerVisible(): Boolean {
            return if (spinnerPopupWindow != null) {
                spinnerPopupWindow!!.isShowing
            } else {
                false
            }
        }

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

        fun hideFeedback() {
            feedbackPopupWindow?.dismiss()
        }

        fun isFeedbackVisible(): Boolean {
            return if (feedbackPopupWindow != null) {
                feedbackPopupWindow!!.isShowing
            } else {
                false
            }
        }

        fun movieJsonParser(json: String): Movie {
            val jsonObject = JSONObject(json)
            return jsonObjToMovie(jsonObject)
        }

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

    }
}