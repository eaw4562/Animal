package com.example.animal.session

import android.content.Context

class SessionManager(private val context: Context) {
    private val PREF_NAME = "session_pref"
    private val KEY_IS_LOGGED_IN = "is_logged_in"

    fun setLoggedIn(loggedIn : Boolean) {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPref.edit().putBoolean(KEY_IS_LOGGED_IN, loggedIn).apply()
    }

    fun isLoggedIn() : Boolean {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPref.getBoolean(KEY_IS_LOGGED_IN, false)
    }
}