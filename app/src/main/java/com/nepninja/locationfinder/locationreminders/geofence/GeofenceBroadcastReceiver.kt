package com.nepninja.locationfinder.locationreminders.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import com.nepninja.locationfinder.locationreminders.data.dto.ReminderDTO
import com.nepninja.locationfinder.locationreminders.data.dto.Result
import com.nepninja.locationfinder.locationreminders.data.local.RemindersLocalRepository
import com.nepninja.locationfinder.locationreminders.reminderslist.ReminderDataItem
import com.nepninja.locationfinder.locationreminders.savereminder.SaveReminderFragment.Companion.ACTION_GEOFENCE_EVENT
import com.nepninja.locationfinder.utils.sendNotification
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.java.KoinJavaComponent.inject
import kotlin.coroutines.CoroutineContext

/**
 * Triggered by the Geofence.  Since we can have many Geofences at once, we pull the request
 * ID from the first Geofence, and locate it within the cached data in our Room DB
 *
 * Or users can add the reminders and then close the app, So our app has to run in the background
 * and handle the geofencing in the background.
 * To do that you can use https://developer.android.com/reference/android/support/v4/app/JobIntentService to do that.
 *
 */

class GeofenceBroadcastReceiver : BroadcastReceiver(), KoinComponent, CoroutineScope {
    val TAG = GeofenceBroadcastReceiver::class.java.simpleName

    private var coroutineJob: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + coroutineJob

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_GEOFENCE_EVENT) {
            val geofencingEvent = GeofencingEvent.fromIntent(intent)
            if (geofencingEvent.hasError()) {
                val errorMessage =
                    GeofenceStatusCodes.getStatusCodeString(geofencingEvent.errorCode)
                Log.e(TAG, errorMessage)
            }

            val geofenceTransition = geofencingEvent.geofenceTransition
            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER
                || geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT
            ) {
                val fenceId = when {
                    geofencingEvent.triggeringGeofences.isNotEmpty() -> geofencingEvent.triggeringGeofences[0].requestId
                    else -> return
                }

                val remindersLocalRepository: RemindersLocalRepository by inject()
                CoroutineScope(coroutineContext).launch(SupervisorJob()) {
                    val result = remindersLocalRepository.getReminder(fenceId)
                    if (result is Result.Success<ReminderDTO>) {
                        val reminderDTO = result.data
                        sendNotification(
                            context,
                            ReminderDataItem(
                                reminderDTO.title,
                                reminderDTO.description,
                                reminderDTO.location,
                                reminderDTO.latitude,
                                reminderDTO.longitude,
                                reminderDTO.id
                            )
                        )
                    }
                }
            }
        }
    }
}