package com.wt.kids.mykidsposition.utils

import android.content.Context
import android.content.pm.PackageManager
import android.util.Base64
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppSignatureHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    private val logger: Logger
) {
    private val logTag = AppSignatureHelper::class.java.simpleName
    private val HASH_TYPE = "SHA-256"
    private val NUM_HASHED_BYTES = 9
    private val NUM_BASE64_CHAR = 11

    // For each signature create a compatible hash
    /**
     * Get all the app signatures for the current package
     * @return
     */
    val appSignatures: ArrayList<String>
        get() {
            val appCodes = ArrayList<String>()
            try {
                // Get all package signatures for the current package
                val packageName = context.packageName
                val packageManager = context.packageManager
                val signatures = packageManager.getPackageInfo(
                    packageName,
                    PackageManager.GET_SIGNATURES
                ).signatures

                // For each signature create a compatible hash
                for (signature in signatures) {
                    val hash = hash(packageName, signature.toCharsString())
                    if (hash != null) {
                        appCodes.add(String.format("%s", hash))
                    }
                }
            } catch (e: PackageManager.NameNotFoundException) {
                Log.e(logTag, "Unable to find package to obtain hash.", e)
            }
            return appCodes
        }

    private fun hash(packageName: String, signature: String): String? {
        val appInfo = "$packageName $signature"
        try {
            val messageDigest = MessageDigest.getInstance(HASH_TYPE)
            messageDigest.update(appInfo.toByteArray(StandardCharsets.UTF_8))
            var hashSignature = messageDigest.digest()

            // truncated into NUM_HASHED_BYTES
            hashSignature = Arrays.copyOfRange(hashSignature, 0, NUM_HASHED_BYTES)
            // encode into Base64
            var base64Hash =
                Base64.encodeToString(hashSignature, Base64.NO_PADDING or Base64.NO_WRAP)
            base64Hash = base64Hash.substring(0, NUM_BASE64_CHAR)
            Log.d(logTag, String.format("pkg: %s -- hash: %s", packageName, base64Hash))
            return base64Hash
        } catch (e: NoSuchAlgorithmException) {
            Log.e(logTag, "hash:NoSuchAlgorithm", e)
        }
        return null
    }
}