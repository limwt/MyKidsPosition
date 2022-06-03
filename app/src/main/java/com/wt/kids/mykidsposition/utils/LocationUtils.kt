package com.wt.kids.mykidsposition.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationUtils @Inject constructor(
    @ApplicationContext private val context: Context,
    private val logger: Logger
) {
    private val logTag = this::class.java.simpleName
    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private var registered = false

    private val locationListener = LocationListener {
        val longitude = it.longitude
        val latitude = it.latitude
        //logger.logD(logTag, "update location : $longitude, $latitude")
        Toast.makeText(context, "Update location $longitude, $latitude", Toast.LENGTH_SHORT).show()
    }

    fun getLocationData(): Location? {
        logger.logD(logTag, "getLocationData")
        var location: Location? = null
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        }

        return location
    }

    fun registerLocationSrv() {
        if (registered) {
            return
        }

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            logger.logD(logTag, "registerLocationSrv")
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000L, 1.0f, locationListener)
            registered = !registered
        }
    }

    fun unregisterLocationSrv() {
        if (!registered) {
            return
        }

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            logger.logD(logTag, "unregisterLocationSrv")
            locationManager.removeUpdates(locationListener)
            registered = !registered
        }
    }
}