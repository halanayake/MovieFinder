package com.example.moviefinder.util

import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import com.example.moviefinder.R

class Util {
     companion object {

         private var spinnerPopupWindow: PopupWindow? = null
         private var feedbackPopupWindow: PopupWindow? = null

         fun showSpinner(inflater: LayoutInflater) {
             val popupView: View = inflater.inflate(R.layout.spinner, null)
             val width = LinearLayout.LayoutParams.MATCH_PARENT
             val height = LinearLayout.LayoutParams.MATCH_PARENT
             val focusable = true
             spinnerPopupWindow = PopupWindow(popupView, width, height, focusable)
             spinnerPopupWindow!!.showAtLocation(popupView, Gravity.CENTER, 0, 0)
         }

         fun hideSpinner() {
             spinnerPopupWindow?.dismiss()
         }

         fun showFeedback(inflater: LayoutInflater, message: String) {
             val popupView: View = inflater.inflate(R.layout.feedback, null)
             val width = LinearLayout.LayoutParams.MATCH_PARENT
             val height = LinearLayout.LayoutParams.MATCH_PARENT
             val focusable = true
             feedbackPopupWindow = PopupWindow(popupView, width, height, focusable)
             feedbackPopupWindow!!.contentView.findViewById<TextView>(R.id.feedback_message).text = message
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

    }
}