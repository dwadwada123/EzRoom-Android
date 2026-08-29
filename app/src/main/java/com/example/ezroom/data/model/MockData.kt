package com.example.ezroom.data.model

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import com.example.ezroom.EzRoomApplication
import com.example.ezroom.domain.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object MockData {
    private const val PREFS_NAME = "EzRoomLocalPrefs"
    private val prefs by lazy {
        try {
            EzRoomApplication.getContext().getSharedPreferences(PREFS_NAME, android.content.Context.MODE_PRIVATE)
        } catch (e: Exception) {
            null
        }
    }
    private val gson = Gson()

    // Initial Properties
    val properties = mutableStateListOf<Property>()

    // Initial Rooms linked to Properties
    val rooms = mutableStateListOf<Room>()

    // Local collections that need persistence
    val appointments = mutableStateListOf<Appointment>()
    val renterReviews = mutableStateListOf<RenterReview>()
    val conversations = mutableStateListOf<Conversation>()
    val messages = mutableStateListOf<Message>()
    val notifications = mutableStateListOf<NotificationItem>()
    val paymentAccounts = mutableStateListOf<PaymentAccount>()
    val favoriteRoomIds = mutableStateListOf<String>()

    val invoices = mutableStateListOf<Invoice>()
    val contracts = mutableStateListOf<Contract>()

    var currentUser = User(
        id = "",
        name = "",
        email = "",
        phone = "",
        avatarUrl = "",
        role = "RENTER",
        isEkycVerified = false,
        creditScore = 5.0f
    )

    init {
        loadAll()
    }

    private fun loadAll() {
        val p = prefs ?: return
        // Load Favorite Room IDs
        val favJson = p.getString("favoriteRoomIds", null)
        if (favJson != null) {
            val type = object : TypeToken<List<String>>() {}.type
            val list = gson.fromJson<List<String>>(favJson, type) ?: emptyList()
            favoriteRoomIds.clear()
            favoriteRoomIds.addAll(list)
        }

        // Load Appointments
        val apptsJson = p.getString("appointments", null)
        if (apptsJson != null) {
            val type = object : TypeToken<List<Appointment>>() {}.type
            val list = gson.fromJson<List<Appointment>>(apptsJson, type) ?: emptyList()
            appointments.clear()
            appointments.addAll(list)
        }

        // Load Renter Reviews
        val reviewsJson = p.getString("renterReviews", null)
        if (reviewsJson != null) {
            val type = object : TypeToken<List<RenterReview>>() {}.type
            val list = gson.fromJson<List<RenterReview>>(reviewsJson, type) ?: emptyList()
            renterReviews.clear()
            renterReviews.addAll(list)
        }

        // Load Conversations
        val convsJson = p.getString("conversations", null)
        if (convsJson != null) {
            val type = object : TypeToken<List<Conversation>>() {}.type
            val list = gson.fromJson<List<Conversation>>(convsJson, type) ?: emptyList()
            conversations.clear()
            conversations.addAll(list)
        }

        // Load Messages
        val msgsJson = p.getString("messages", null)
        if (msgsJson != null) {
            val type = object : TypeToken<List<Message>>() {}.type
            val list = gson.fromJson<List<Message>>(msgsJson, type) ?: emptyList()
            messages.clear()
            messages.addAll(list)
        }

        // Load Notifications
        val notifsJson = p.getString("notifications", null)
        if (notifsJson != null) {
            val type = object : TypeToken<List<NotificationItem>>() {}.type
            val list = gson.fromJson<List<NotificationItem>>(notifsJson, type) ?: emptyList()
            notifications.clear()
            notifications.addAll(list)
        }

        // Load Payment Accounts
        val payJson = p.getString("paymentAccounts", null)
        if (payJson != null) {
            val type = object : TypeToken<List<PaymentAccount>>() {}.type
            val list = gson.fromJson<List<PaymentAccount>>(payJson, type) ?: emptyList()
            paymentAccounts.clear()
            paymentAccounts.addAll(list)
        }
    }

    // Save triggers
    fun saveAppointments() {
        prefs?.edit()?.putString("appointments", gson.toJson(appointments.toList()))?.apply()
    }

    fun saveReviews() {
        prefs?.edit()?.putString("renterReviews", gson.toJson(renterReviews.toList()))?.apply()
    }

    fun saveConversations() {
        prefs?.edit()?.putString("conversations", gson.toJson(conversations.toList()))?.apply()
    }

    fun saveMessages() {
        prefs?.edit()?.putString("messages", gson.toJson(messages.toList()))?.apply()
    }

    fun saveNotifications() {
        prefs?.edit()?.putString("notifications", gson.toJson(notifications.toList()))?.apply()
    }

    fun savePaymentAccounts() {
        prefs?.edit()?.putString("paymentAccounts", gson.toJson(paymentAccounts.toList()))?.apply()
    }

    fun saveFavoriteRoomIds() {
        prefs?.edit()?.putString("favoriteRoomIds", gson.toJson(favoriteRoomIds.toList()))?.apply()
    }
}
