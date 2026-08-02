package com.example.ezroom.util

import android.content.Context
import android.content.SharedPreferences
import com.example.ezroom.EzRoomApplication
import com.example.ezroom.domain.model.User
import com.google.gson.Gson

object TokenManager {
    private const val PREFS_NAME = "EzRoomPrefs"
    private const val KEY_TOKEN = "jwt_token"
    private const val KEY_USER = "current_user"
    private const val KEY_REMEMBER_ME = "remember_me"

    private val sharedPreferences: SharedPreferences by lazy {
        EzRoomApplication.getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    private val gson = Gson()

    fun saveToken(token: String) {
        sharedPreferences.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString(KEY_TOKEN, null)
    }

    fun saveRememberMe(rememberMe: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_REMEMBER_ME, rememberMe).apply()
    }

    fun isRememberMe(): Boolean {
        return sharedPreferences.getBoolean(KEY_REMEMBER_ME, false)
    }

    fun saveUser(user: User) {
        val userJson = gson.toJson(user)
        sharedPreferences.edit().putString(KEY_USER, userJson).apply()
    }

    fun getUser(): User? {
        val userJson = sharedPreferences.getString(KEY_USER, null) ?: return null
        return try {
            // Parse the raw JSON to handle both old (_id) and new (id) formats
            val jsonObj = com.google.gson.JsonParser.parseString(userJson).asJsonObject
            // Migrate _id → id if needed
            if (!jsonObj.has("id") || jsonObj.get("id").isJsonNull) {
                if (jsonObj.has("_id")) {
                    jsonObj.addProperty("id", jsonObj.get("_id").asString)
                    jsonObj.remove("_id")
                } else {
                    // Corrupt/stale data — clear it
                    clear()
                    return null
                }
            }
            gson.fromJson(jsonObj, User::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun clear() {
        sharedPreferences.edit()
            .remove(KEY_TOKEN)
            .remove(KEY_USER)
            .remove(KEY_REMEMBER_ME)
            .apply()
    }
}
