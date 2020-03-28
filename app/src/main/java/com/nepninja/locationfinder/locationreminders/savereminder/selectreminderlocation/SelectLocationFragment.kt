package com.nepninja.locationfinder.locationreminders.savereminder.selectreminderlocation


import android.Manifest
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.nepninja.locationfinder.R
import com.nepninja.locationfinder.base.BaseFragment
import com.nepninja.locationfinder.databinding.FragmentSelectLocationBinding
import com.nepninja.locationfinder.locationreminders.savereminder.SaveReminderViewModel
import com.nepninja.locationfinder.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject

class SelectLocationFragment : BaseFragment() {

    //Use Koin to get the view model of the SaveReminder
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSelectLocationBinding
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)

        Dexter.withActivity(activity).withPermission(
            Manifest.permission.ACCESS_FINE_LOCATION
        ).withListener(object : PermissionListener {
            override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
                fusedLocationClient.lastLocation.addOnSuccessListener {
                    val latLng = LatLng(it.latitude, it.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                    mMap.addMarker(MarkerOptions().position(latLng))
                    mMap.uiSettings.setAllGesturesEnabled(true)
                    mMap.setOnMapClickListener {
                        mMap.clear()
                        mMap.addMarker(MarkerOptions().position(it))
                    }
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                permission: PermissionRequest?,
                token: PermissionToken?
            ) {
                showPermissionsRationale(token)
            }

            override fun onPermissionDenied(response: PermissionDeniedResponse?) {

            }
        }).check()

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.select_location_map) as SupportMapFragment
        mapFragment.getMapAsync {
            mMap = it
            try {
                val success =
                    mMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                            activity,
                            R.raw.map_style
                        )
                    )
            } catch (e: Resources.NotFoundException) {
            }
        }

//        TODO: add the map setup implementation
//        TODO: zoom to the user location after taking his permission
//        TODO: add style to the map
//        TODO: put a marker to location that the user selected


//        TODO: call this function after the user confirms on the selected location
        onLocationSelected()

        return binding.root
    }


    private fun onLocationSelected() {
        //        TODO: When the user confirms on the selected location,
        //         send back the selected location details to the view model
        //         and navigate back to the previous fragment to save the reminder and add the geofence
    }


    fun showPermissionsRationale(token: PermissionToken?) {
        AlertDialog.Builder(activity as Context).setTitle(R.string.permission_rationale_title)
            .setMessage(R.string.permission_rationale_message)
            .setNegativeButton(
                R.string.cancel
            ) { dialog, _ ->
                dialog.dismiss()
                token?.cancelPermissionRequest()
            }
            .setPositiveButton(
                R.string.ok
            ) { dialog, _ ->
                dialog.dismiss()
                token?.continuePermissionRequest()
            }
            .setOnDismissListener { token?.cancelPermissionRequest() }
            .show()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.normal_map -> {
            mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}
