package com.nepninja.locationfinder.locationreminders.savereminder

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.nepninja.locationfinder.R
import com.nepninja.locationfinder.base.BaseFragment
import com.nepninja.locationfinder.base.NavigationCommand
import com.nepninja.locationfinder.databinding.FragmentSaveReminderBinding
import com.nepninja.locationfinder.locationreminders.geofence.GeoFenceConstant.GEOFENCE_EXPIRATION_IN_MILLISECONDS
import com.nepninja.locationfinder.locationreminders.geofence.GeoFenceConstant.GEOFENCE_RADIUS_IN_METERS
import com.nepninja.locationfinder.locationreminders.geofence.GeofenceBroadcastReceiver
import com.nepninja.locationfinder.locationreminders.reminderslist.ReminderDataItem
import com.nepninja.locationfinder.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject

class SaveReminderFragment : BaseFragment() {
    //Get the view model this time as a single to be shared with the another fragment
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSaveReminderBinding
    private val TAG = SaveReminderFragment::class.java.simpleName

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_save_reminder, container, false)

        setDisplayHomeAsUpEnabled(true)

        binding.viewModel = _viewModel

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.lifecycleOwner = this
        binding.selectLocation.setOnClickListener {
            //            Navigate to another fragment to get the user location
            _viewModel.navigationCommand.value =
                NavigationCommand.To(SaveReminderFragmentDirections.actionSaveReminderFragmentToSelectLocationFragment())
        }

        binding.saveReminder.setOnClickListener {
            val title = _viewModel.reminderTitle.value
            val description = _viewModel.reminderDescription.value
            val location = _viewModel.reminderSelectedLocationStr.value
            val latitude = _viewModel.latitude.value
            val longitude = _viewModel.longitude.value

            val reminderDataItem =
                ReminderDataItem(title, description, location, latitude, longitude)
            _viewModel.saveReminder(reminderDataItem)

            val geoFenceClient = LocationServices.getGeofencingClient(activity as Context)

            val geoFence = Geofence.Builder()
                .setRequestId(reminderDataItem.id)
                .setCircularRegion(latitude!!, longitude!!, GEOFENCE_RADIUS_IN_METERS)
                .setExpirationDuration(GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .build()

            val geoFenceRequest = GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(geoFence)
                .build()

            geoFenceClient?.addGeofences(geoFenceRequest, geofencePendingIntent)?.run {
                addOnSuccessListener {
                    Log.d(TAG, "GeoFence request added successfully")
                }
                addOnFailureListener {
                    Log.d(TAG, "GeoFence request failed to add")
                }
            }

//            TODO: use the user entered reminder details to:
//             1) add a geofencing request
//             2) save the reminder to the local db
        }
    }

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(activity, GeofenceBroadcastReceiver::class.java)
        intent.action = ACTION_GEOFENCE_EVENT
        PendingIntent.getBroadcast(activity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    override fun onDestroy() {
        super.onDestroy()
        //make sure to clear the view model after destroy, as it's a single view model.
        _viewModel.onClear()
    }

    companion object {
        internal const val ACTION_GEOFENCE_EVENT = "ACTION_GEOFENCE_EVENT"
    }
}