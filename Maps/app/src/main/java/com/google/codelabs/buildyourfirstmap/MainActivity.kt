package com.google.codelabs.buildyourfirstmap

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*


class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        private const val REQUEST_PERMISSIONS_REQUEST_CODE = 1
        private const val UPDATE_INTERVAL_IN_MILLISECONDS = 10000L
        private const val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2
    }

    var mGoogleMap: GoogleMap? = null
    private var myLongitude: Double? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var defaultLatLng: LatLng = LatLng(0.0, 0.0)
    private var lastLatLng: LatLng = defaultLatLng
    private var newLatLng: LatLng = defaultLatLng

    private val mCallBack = object: LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {

            if(lastLatLng == defaultLatLng || newLatLng == defaultLatLng){
                lastLatLng = LatLng(locationResult.lastLocation.latitude, locationResult.lastLocation.longitude)
                newLatLng = LatLng(locationResult.lastLocation.latitude, locationResult.lastLocation.longitude)
            }

            newLatLng = LatLng(locationResult.lastLocation.latitude, locationResult.lastLocation.longitude)
            Log.i("LOG:maps", "$mGoogleMap")
            if(mGoogleMap != null){
                addPolyline(mGoogleMap, lastLatLng, newLatLng)
            }
            lastLatLng = newLatLng
            Log.i("LOG:longitude", "${locationResult.lastLocation.longitude}")
            Log.i("LOG:latitude", "${locationResult.lastLocation.latitude}")

            super.onLocationResult(locationResult)
        }

        override fun onLocationAvailability(locationAvailability: LocationAvailability) {
            super.onLocationAvailability(locationAvailability)
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val permissionState = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (permissionState != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_PERMISSIONS_REQUEST_CODE)
        }

       requestLocationUpdates()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }


    private fun addPolyline(googleMap: GoogleMap?, lastLatLng: LatLng, newLatLng: LatLng) {
        val polyline = googleMap?.addPolyline(
            PolylineOptions()
                .add(
                    lastLatLng,
                    newLatLng
                )
                .color(ContextCompat.getColor(applicationContext,R.color.cyan))
        )

        val bounds = LatLngBounds.builder()
        bounds.include(newLatLng)
        mGoogleMap?.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 20))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSIONS_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    requestLocationUpdates()
            }
        }
    }


    @SuppressLint("MissingPermission")
    private fun getLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location : Location? ->
            location?.let {
                myLongitude = it.longitude
               /*
                latLngIni = LatLng(it.latitude, it.longitude)
                */
            }
            Log.i("LOG:ini", "$myLongitude")
        }
    }

    private fun createLocationRequest() = LocationRequest.create().apply {
        interval = UPDATE_INTERVAL_IN_MILLISECONDS
        fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }


    @SuppressLint("MissingPermission")
    fun requestLocationUpdates() {
        try {
            Looper.myLooper()?.let {
                fusedLocationClient.requestLocationUpdates(
                    createLocationRequest(),
                    mCallBack, it
                )
            }
        } catch (ex: SecurityException) {
            Log.e("LOG", "Lost location permission. Could not request updates. $ex")
        }
    }

    private fun removeLocationUpdates() {
        try {
            fusedLocationClient.removeLocationUpdates(mCallBack)

        } catch (ex : SecurityException) {
            Log.e("LOG", "Lost location permission. Could not remove updates. $ex")
        }
    }
}
