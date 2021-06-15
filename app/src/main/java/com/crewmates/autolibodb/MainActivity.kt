package com.crewmates.autolibodb

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : FragmentActivity(), OnMapReadyCallback {
    var map: GoogleMap? = null
    private val REQUEST_CODE_LOCATION_PERMISSION = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        val mapFragment =
            (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)!!
        mapFragment.getMapAsync(this)
        startLocationUpdate.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_CODE_LOCATION_PERMISSION
                )
            } else {
                startLocationService()
            }
        }
        stopLocationUpdate.setOnClickListener {
            stopLocationService()
        }

    }

    override fun onMapReady(p0: GoogleMap) {
        map = p0
        val latLng = LatLng(36.704998, 3.173918)
        map!!.addMarker(MarkerOptions().position(latLng).title("Your position"))
        val zoomLevel = 16.0f //This goes up to 21
        map!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.size > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationService()
            } else {
                Toast.makeText(this, "Permission denied ", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isLocationServiceRunning(): Boolean {
        val activityManager =
            getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        if (activityManager != null) {
            for (service in activityManager.getRunningServices(
                Int.MAX_VALUE
            )) {
                if (LocationService::class.java.name == service.service.className) {
                    if (service.foreground) {
                        return true
                    }
                }
            }
            return false
        }
        return false
    }

    private fun startLocationService() {
        if (!isLocationServiceRunning()) {
            val intent = Intent(applicationContext, LocationService::class.java)
            intent.action = Constants.ACTION_START_LOCATION_SERVICE
            startService(intent)
            Toast.makeText(this, "Location service started", Toast.LENGTH_SHORT).show()

        }
    }

    private fun stopLocationService() {
        if (isLocationServiceRunning()) {
            val intent = Intent(applicationContext, LocationService::class.java)
            intent.action = Constants.ACTION_STOP_LOCATION_SERVICE
            startService(intent)
            Toast.makeText(this, "Location service stoped", Toast.LENGTH_SHORT).show()
        }
    }
}