package com.pascal.tokoku.common.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.app.ActivityCompat


class CurrentLocation {

    fun getLocationWithCheckNetworkAndGPS(mContext: Context): Location? {
        val lm = (mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager)
        val isGpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkLocationEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        var networkLoacation: Location? = null
        var gpsLocation: Location? = null
        var finalLoc: Location? = null
        if (isGpsEnabled) if (ActivityCompat.checkSelfPermission(
                mContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                mContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return null
        }
        gpsLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        if (isNetworkLocationEnabled) networkLoacation =
            lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        if (gpsLocation != null && networkLoacation != null) {

            //smaller the number more accurate result will
            return if (gpsLocation.accuracy > networkLoacation.accuracy) networkLoacation.also {
                finalLoc = it
            } else gpsLocation.also { finalLoc = it }
        } else {
            if (gpsLocation != null) {
                return gpsLocation.also { finalLoc = it }
            } else if (networkLoacation != null) {
                return networkLoacation.also { finalLoc = it }
            }
        }
        return finalLoc
    }
}