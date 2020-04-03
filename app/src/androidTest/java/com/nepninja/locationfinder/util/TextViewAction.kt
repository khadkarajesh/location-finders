package com.nepninja.locationfinder.util

import android.view.View
import android.widget.TextView
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import org.hamcrest.Matchers.allOf


fun setTextOnTextView(value: String?): ViewAction? {
    return object : ViewAction {
        override fun perform(uiController: androidx.test.espresso.UiController?, view: View?) {
            (view as TextView).text = value
        }

        override fun getDescription(): String {
            return "replace text"
        }

        override fun getConstraints(): org.hamcrest.Matcher<View> {
            return allOf(isDisplayed(), isAssignableFrom(TextView::class.java))
        }
    }
}