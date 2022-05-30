package com.wt.kids.mykidsposition.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
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
    private val geocoder = Geocoder(context)

    private val locationListener = LocationListener {
        val longitude = it.longitude
        val latitude = it.latitude
        logger.logD(logTag, "update location : $longitude, $latitude")
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
        /*if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000L, 1.0f, locationListener)
        }*/
    }

    fun getGeocode(address: String): Pair<Double, Double> {
        val list = geocoder.getFromLocationName(address,1)

        return if (list.isEmpty()) {
            Pair(Double.MAX_VALUE, Double.MAX_VALUE)
        } else {
            val result = list[0]
            Pair(result.latitude, result.longitude)
        }
    }
}