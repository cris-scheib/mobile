package com.google.codelabs.buildyourfirstmap

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import android.location.Location
import android.os.Build
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.*



class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        private const val REQUEST_PERMISSIONS_REQUEST_CODE = 1
        private const val UPDATE_INTERVAL_IN_MILLISECONDS = 10000L
        private const val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2
    }


    private lateinit var button: Button
    private var mGoogleMap: GoogleMap? = null
    private var myLongitude: Double? = null
    private var routeId: Int = 0
    private lateinit var db: DBHelper
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
            if(routeId == 0) setRouteId();
            db.addData(newLatLng.latitude, newLatLng.longitude, routeId);

            /*
            logDatabase()
            */

            newLatLng = LatLng(locationResult.lastLocation.latitude, locationResult.lastLocation.longitude)
            if(mGoogleMap != null){
                addPolyline(mGoogleMap, lastLatLng, newLatLng)
            }
            lastLatLng = newLatLng

            super.onLocationResult(locationResult)
        }

        override fun onLocationAvailability(locationAvailability: LocationAvailability) {
            super.onLocationAvailability(locationAvailability)
        }

    }

    fun logDatabase(){
        val cursor: Cursor = db.getData();
        var data = cursor.moveToFirst()
        while (data) {
            val lng = cursor.getDouble(cursor.getColumnIndex("longitude"))
            val lat = cursor.getDouble(cursor.getColumnIndex("latitude"))
            Log.i("LOG:long", "$lng")
            Log.i("LOG:lat", "$lat")
            data = cursor.moveToNext()
        }
        cursor.close()
    }

    fun setRouteId(){
        val cursor: Cursor = db.getLastData();
        cursor.moveToFirst()
        val index = cursor.getColumnIndex("routeId")
        routeId =  if (index < 0) 1 else cursor.getInt(index) + 1
        cursor.close()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = DBHelper(this, null)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val permissionState = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (permissionState != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_PERMISSIONS_REQUEST_CODE)
        }

       requestLocationUpdates()

        button = findViewById(R.id.button)
        button.setOnClickListener {
            val intent = Intent(this, ListActivity::class.java)
            startActivity(intent);
        }

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
