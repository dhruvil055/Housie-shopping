package com.housieshopping.app.domain.model

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: String,
    val isRead: Boolean = false,
    val deepLinkRoute: String? = null,
    val type: String = "GENERAL"
)
