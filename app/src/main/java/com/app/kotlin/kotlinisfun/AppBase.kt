package com.app.kotlin.kotlinisfun

import android.app.Application
import com.google.firebase.FirebaseApp

open class AppBase : Application() {

    companion object {
        var DEBUG = BuildConfig.DEBUG

        private var instance: AppBase? = null

        fun isDebug(): Boolean {
            return this.DEBUG
        }

        fun getInstance(): AppBase? {
            return instance
        }
    }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}