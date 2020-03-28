package com.nepninja.locationfinder.locationreminders.geofence

internal object GeoFenceConstant {
    const val GEOFENCE_RADIUS_IN_METERS = 15f
    val GEOFENCE_EXPIRATION_IN_MILLISECONDS: Long =
        java.util.concurrent.TimeUnit.HOURS.toMillis(1)
}