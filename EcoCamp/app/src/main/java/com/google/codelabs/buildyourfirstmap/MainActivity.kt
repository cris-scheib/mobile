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
import kotlin.math.abs


class MainActivity : AppCompatActivity(), GoogleMap.OnMarkerClickListener, OnMapReadyCallback {

    companion object {
        private const val REQUEST_PERMISSIONS_REQUEST_CODE = 1
        private const val ZOOM_MAP = .001
        private const val PRECIS_TOL = .00002
        private const val UPDATE_INTERVAL_IN_MILLISECONDS = 10000L
        private const val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2
    }

    private lateinit var buttonNew: Button
    private lateinit var buttonAbout: Button
    private var mGoogleMap: GoogleMap? = null
    private lateinit var db: DBHelper
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var defaultLatLng: LatLng = LatLng(0.0, 0.0)
    private var newLatLng: LatLng = defaultLatLng

    private var latesteLat: Double = 0.0
    private var latesteLong: Double = 0.0


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

        buttonNew = findViewById(R.id.button_new_comp)
        buttonNew.setOnClickListener {
            val intent = Intent(this, ActionActivity::class.java)
            intent.putExtra("latitude", "$latesteLat")
            intent.putExtra("longitude", "$latesteLong")
            startActivity(intent);
        }

        buttonAbout = findViewById(R.id.button_about)
        buttonAbout.setOnClickListener {
            val intent = Intent(this, AboutActivity::class.java)
            startActivity(intent);
        }
/*
        button_list = findViewById(R.id.button_list)
        button_list.setOnClickListener {
            if(!list_route){
                setRouteId()
                requestLocationUpdates()
                button_list.setText("Finalizar rota");
                button_action.setText("Criar alerta");
            }else{
                removeLocationUpdates()
                routeId = 0
                button_list.setText("Iniciar rota");
                button_action.setText("Histórico");
            }
            list_route = !list_route
        }
       */

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

    }

    private val mCallBack = object: LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            val precisionLat = locationResult.lastLocation.latitude - latesteLat
            val precisionLong = locationResult.lastLocation.longitude - latesteLong
            if(abs(precisionLat) > PRECIS_TOL && abs(precisionLong) > PRECIS_TOL){
                latesteLat = locationResult.lastLocation.latitude
                latesteLong = locationResult.lastLocation.longitude
                if(mGoogleMap != null){
                    updateLocal(latesteLat, latesteLong)
                }
            }
            if(mGoogleMap != null){
                addMarkers(mGoogleMap)
            }

            super.onLocationResult(locationResult)
        }

        override fun onLocationAvailability(locationAvailability: LocationAvailability) {
            super.onLocationAvailability(locationAvailability)
        }

    }

    /*fun loadData(){
        val cursor: Cursor = db.getData();
        var data = cursor.moveToFirst()
        while (data) {
            val lng = cursor.getDouble(cursor.getColumnIndex("longitude"))
            val lat = cursor.getDouble(cursor.getColumnIndex("latitude"))
            val id = cursor.getDouble(cursor.getColumnIndex("id"))

            val marker = googleMap.addMarker(
                MarkerOptions()
                    .title(place.name)
                    .position(LatLng(lat, lng) )
                    .icon(bicycleIcon)
            )

            // Set place as the tag on the marker object so it can be referenced within
            // MarkerInfoWindowAdapter
            marker.tag = place


            Log.i("LOG:long", "$lng")
            Log.i("LOG:lat", "$lat")
            Log.i("LOG:ID", "$id")
            data = cursor.moveToNext()
        }
        cursor.close()
    }*/

    /*fun setRouteId(){
        val cursor: Cursor = db.getLastData("routes_table");
        if(cursor.moveToFirst()) {
            val index = cursor.getColumnIndex("id")
            routeId = if (index < 0) 1 else cursor.getInt(index) + 1
            Log.i("LOG:route", "$routeId")
        }else{
            routeId = 1
        }
        cursor.close()
        db.addDataRoute(routeId, "Route $routeId");
    }*/

   /* fun getLastPositionId(){
        val cursor: Cursor = db.getLastData("positions_table");
        if(cursor.moveToFirst()) {
            val index = cursor.getColumnIndex("id")
            lastPositionId = if (index < 0) 0 else cursor.getInt(index)
        }else{
            lastPositionId = 0
        }
        cursor.close()
    }*/

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
    }

    private val complaintIcon: BitmapDescriptor by lazy {
        val color = ContextCompat.getColor(this, R.color.sec)
        BitmapHelper.vectorToBitmap(this, R.drawable.ic_directions_eco_24dp, color)
    }


    private fun addMarkers(googleMap: GoogleMap?){
        googleMap?.clear()
        val cursor: Cursor = db.getData();
        var data = cursor.moveToFirst()
        while (data) {

            val lng = cursor.getDouble(cursor.getColumnIndex("longitude"))
            val lat = cursor.getDouble(cursor.getColumnIndex("latitude"))
            val id = cursor.getDouble(cursor.getColumnIndex("id"))

            val marker = googleMap?.addMarker(
                MarkerOptions()
                    .position(LatLng(lat, lng))
                    .icon(complaintIcon)
            )

            marker?.tag = id
            data = cursor.moveToNext()
        }
        googleMap?.setOnMarkerClickListener(this)
        cursor.close()
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val intent = Intent(this, DetailActivity::class.java)

        val position = marker.getPosition()
        intent.putExtra("latitude", "${position.latitude}")
        intent.putExtra("longitude", "${position.longitude}")
        startActivity(intent);
        return false;
    }

    /*
    private fun addPolyline(googleMap: GoogleMap?, lastLatLng: LatLng, newLatLng: LatLng) {
        val polyline = googleMap?.addPolyline(
            PolylineOptions()
                .add(
                    lastLatLng,
                    newLatLng
                )
                .color(ContextCompat.getColor(applicationContext,R.color.sec))
        )

        val bounds = LatLngBounds.builder()
        bounds.include(newLatLng)
        mGoogleMap?.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 2))
    }

     */

    private fun updateLocal(latitude: Double, longitude: Double) {
        val bounds = LatLngBounds.builder()
        bounds.include(LatLng(latitude, longitude + ZOOM_MAP))
        bounds.include(LatLng(latitude + ZOOM_MAP, longitude))
        bounds.include(LatLng(latitude, longitude - ZOOM_MAP))
        bounds.include(LatLng(latitude - ZOOM_MAP, longitude))

        Log.i("LOG:bounds", "Response is: $bounds")
        Log.i("LOG:mGoogleMap", "Response is: $mGoogleMap")
        mGoogleMap?.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 2))
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

    override fun onRestart() {
        super.onRestart()
        finish()
        startActivity(intent)
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

    /*private fun buildRoute(routeId: Int) {

        val cursor: Cursor = db.getDataWhere("positions_table", " routeId  = $routeId");
        var data = cursor.moveToFirst()
        while (data) {
            val lng = cursor.getDouble(cursor.getColumnIndex("longitude"))
            val lat = cursor.getDouble(cursor.getColumnIndex("latitude"))


            Log.i("LOG:long", "$lng")
            Log.i("LOG:lat", "$lat")
            data = cursor.moveToNext()
        }
        cursor.close()
    }*/

    private fun removeLocationUpdates() {
        try {
            fusedLocationClient.removeLocationUpdates(mCallBack)

        } catch (ex : SecurityException) {
            Log.e("LOG", "Lost location permission. Could not remove updates. $ex")
        }
    }
}
