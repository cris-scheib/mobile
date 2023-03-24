package com.example.gps

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*


class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_PERMISSIONS_REQUEST_CODE = 1
    }

    private lateinit var button: Button
    private lateinit var textViewLatitude: TextView
    private lateinit var textViewLongitude: TextView
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var myLongitude: Double? = null
    private var myLatitude: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textViewLatitude = findViewById(R.id.latTextView)
        textViewLongitude = findViewById(R.id.lonTextView)

        button = findViewById(R.id.button)
        button.setOnClickListener {
            viewMap()
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val permissionState = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (permissionState != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_PERMISSIONS_REQUEST_CODE)
        }

        if (myLatitude == null || myLongitude == null) {
            getLocation()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSIONS_REQUEST_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    getLocation()
            }
        }
    }

    private fun viewMap() {
        val gmmIntentUri = Uri.parse("google.streetview:cbll=$myLatitude,$myLongitude")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)

        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location : Location? ->
            Log.i("LOG", "$myLongitude")
            location?.let {
                myLongitude = it.longitude
                myLatitude = it.latitude
                textViewLatitude.setText("$myLatitude")
                textViewLongitude.setText("$myLongitude")
            }
        }
    }
}