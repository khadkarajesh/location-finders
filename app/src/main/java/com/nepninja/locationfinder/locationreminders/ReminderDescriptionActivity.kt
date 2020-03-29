package com.nepninja.locationfinder.locationreminders

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.nepninja.locationfinder.R
import com.nepninja.locationfinder.databinding.ActivityReminderDescriptionBinding
import com.nepninja.locationfinder.locationreminders.reminderslist.ReminderDataItem

/**
 * Activity that displays the reminder details after the user clicks on the notification
 */
class ReminderDescriptionActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_ReminderDataItem = "EXTRA_ReminderDataItem"

        //        receive the reminder object after the user clicks on the notification
        fun newIntent(context: Context, reminderDataItem: ReminderDataItem): Intent {
            val intent = Intent(context, ReminderDescriptionActivity::class.java)
            intent.putExtra(EXTRA_ReminderDataItem, reminderDataItem)
            return intent
        }
    }

    private lateinit var binding: ActivityReminderDescriptionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_reminder_description
        )

        val reminderDataItem =
            intent.getSerializableExtra(EXTRA_ReminderDataItem) as ReminderDataItem
        binding.reminderDataItem = reminderDataItem

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map_reminder_description) as SupportMapFragment
        mapFragment.getMapAsync { map ->
            val latLng = LatLng(reminderDataItem.latitude!!, reminderDataItem.longitude!!)
            map.addMarker(
                MarkerOptions().position(
                    LatLng(
                        reminderDataItem.latitude!!,
                        reminderDataItem.longitude!!
                    )
                )
            )
            val cameraPosition = CameraPosition
                .Builder()
                .target(latLng)
                .zoom(13f)
                .build()
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }
    }
}
