package com.example.animal.session

import android.content.Context

class TokenManager(private val context : Context) {
    private val PREF_NAME = "token_pref"
    private val KEY_TOKEN = "token"

    fun saveToken(token : String){
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPref.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken() : String? {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPref.getString(KEY_TOKEN, null)
    }

    fun isTokenValid(token: String): Boolean {
        val savedToken = getToken()
        return savedToken != null && savedToken == token
    }
}