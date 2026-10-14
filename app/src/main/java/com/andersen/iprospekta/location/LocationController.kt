package com.andersen.iprospekta.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.core.content.ContextCompat

/**
 * Acces GPS via le LocationManager du framework (sans dependance Google Play
 * Services). Fournit la derniere position connue et des mises a jour continues.
 */
class LocationController(context: Context) {

    private val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    fun hasPermission(context: Context): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED

    @SuppressLint("MissingPermission")
    fun lastKnown(): Location? {
        val providers = listOf(
            LocationManager.GPS_PROVIDER,
            LocationManager.NETWORK_PROVIDER
        )
        return providers
            .mapNotNull { if (lm.isProviderEnabled(it)) runCatching { lm.getLastKnownLocation(it) }.getOrNull() else null }
            .maxByOrNull { it.time }
    }

    @SuppressLint("MissingPermission")
    fun startUpdates(onLocation: (Location) -> Unit): LocationListener {
        val listener = LocationListener { onLocation(it) }
        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000L, 1f, listener)
        }
        if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000L, 5f, listener)
        }
        lastKnown()?.let(onLocation)
        return listener
    }

    fun stopUpdates(listener: LocationListener) {
        runCatching { lm.removeUpdates(listener) }
    }
}
