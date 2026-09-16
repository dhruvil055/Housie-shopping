package com.housieshopping.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HousieApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
