package com.wt.kids.mykidsposition

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import io.reactivex.rxjava3.exceptions.UndeliverableException
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import java.io.IOException
import java.net.SocketException

@HiltAndroidApp
class JeffApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        setRxJavaPluginHandler()
    }

    private fun setRxJavaPluginHandler() {
        try {
            RxJavaPlugins.setErrorHandler { e ->
                var error = e
                if (error is UndeliverableException) {
                    error = e.cause!!
                }
                if (error is IOException || error is SocketException) {
                    // fine, irrelevant network problem or API that throws on cancellation
                    return@setErrorHandler
                }
                if (error is InterruptedException) {
                    // fine, some blocking code was interrupted by a dispose call
                    return@setErrorHandler
                }
                if (error is NullPointerException || error is IllegalArgumentException) {
                    // that's likely a bug in the application
                    Thread.currentThread().uncaughtExceptionHandler?.
                    uncaughtException(Thread.currentThread(), error)
                    return@setErrorHandler
                }
                // DO NOT MERGE
                if (error is IllegalStateException) {
                    // that's a bug in RxJava or in a custom operator
                    Thread.currentThread().uncaughtExceptionHandler?.
                    uncaughtException(Thread.currentThread(), error)
                    return@setErrorHandler
                }
                Log.w("Undeliverable exception received, not sure what to do", error)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}