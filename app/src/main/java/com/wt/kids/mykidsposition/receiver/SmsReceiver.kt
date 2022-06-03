package com.wt.kids.mykidsposition.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.annotation.CallSuper
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import com.wt.kids.mykidsposition.constants.MessageConstants
import com.wt.kids.mykidsposition.utils.LocationUtils
import com.wt.kids.mykidsposition.utils.Logger
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

abstract class HiltBroadcastReceiver : BroadcastReceiver() {
    @CallSuper
    override fun onReceive(context: Context, intent: Intent) {
    }
}

@AndroidEntryPoint
class SmsReceiver : HiltBroadcastReceiver() {
    private val logTag = this::class.java.simpleName
    @Inject lateinit var logger: Logger
    @Inject lateinit var locationUtils: LocationUtils

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
            val extras = intent.extras
            val status = extras?.get(SmsRetriever.EXTRA_STATUS) as Status
            logger.logD(logTag, "onReceive : ${status.statusCode}")

            when (status.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    val message = (extras.get(SmsRetriever.EXTRA_SMS_MESSAGE) as String).replace(" ", "")
                    logger.logD(logTag, "onReceive : $message")

                    if (message.contains(MessageConstants.MSG_START_TRACKING.msg)) {
                        locationUtils.registerLocationSrv()
                    } else if (message.contains(MessageConstants.MSG_STOP_TRACKING.msg)) {
                        locationUtils.unregisterLocationSrv()
                    }
                }
                CommonStatusCodes.TIMEOUT -> logger.logD(logTag, "onReceive - Error : TimeOut")
            }
        }
    }
}