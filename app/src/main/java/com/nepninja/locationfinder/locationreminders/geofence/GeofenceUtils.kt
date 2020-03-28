package com.nepninja.locationfinder.locationreminders.geofence

import android.content.Context
import android.location.Geocoder
import java.util.*

fun getAddress(context: Context, latitude: Double, longitude: Double): String {
    val geoCoder = Geocoder(context, Locale.getDefault())
    val addresses = geoCoder.getFromLocation(latitude, longitude, 1)
    return addresses[0].getAddressLine(0)
}