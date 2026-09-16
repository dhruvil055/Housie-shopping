package com.housieshopping.app.domain.model

data class User(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val profilePictureUrl: String? = null,
    val isEmailVerified: Boolean = false,
    val isPhoneVerified: Boolean = true,
    val token: String? = null
)
