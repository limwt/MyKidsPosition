package com.wt.kids.mykidsposition.utils

import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Logger @Inject constructor() {
    private val logTag = "[Jeff]"

    fun logD(tag: String = "", msg: String) {
        Log.d(logTag + tag, msg)
    }
}