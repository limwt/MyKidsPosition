package com.wt.kids.mykidsposition.utils

import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.telephony.SmsManager
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SmsUtils @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val logTag = this::class.java.simpleName

    fun sendSms() {
        val phoneNumber = "01057638077"
        val sms = "엄마 집에 왔어요! -아들이..."

        val sentPI = PendingIntent.getBroadcast(context, 0, Intent(phoneNumber), 0)
        val deliveredPI = PendingIntent.getBroadcast(context, 0, Intent(sms), 0)

        //---when the SMS has been sent---
        context.registerReceiver(object: BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                when (resultCode) {
                    Activity.RESULT_OK -> Timber.tag(logTag).d("Sms sent")
                    else -> {}
                }
            }
        }, IntentFilter(phoneNumber))

        val smsManager: SmsManager? = context.getSystemService(SmsManager::class.java)
        smsManager?.sendTextMessage(phoneNumber, null, sms, sentPI, deliveredPI)
    }
}