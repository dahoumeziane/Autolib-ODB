package com.crewmates.autolibodb

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.crewmates.autolibodb.MainActivity.Companion.viewModel
import com.crewmates.autolibodb.model.Location
import com.crewmates.autolibodb.repository.Repository
import com.crewmates.autolibodb.viewModel.MainViewModel
import com.crewmates.autolibodb.viewModel.MainViewModelFactory
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

class LocationService : Service() {


    private fun updateLocation(){


        val location = Location(
            23.2, 23.5,10)
        viewModel.addPosition(location)
        viewModel.locationResponse.observe(MainActivity.context, Observer {
                response ->
            if (response.isSuccessful){
                Log.e("Push",response.body().toString())
                Log.e("Push",response.code().toString())
                Log.e("Push",response.message())

                Log.d("Location uploaded", "success")
            }else {
                Log.d("error", "location not uploaded")
            }
        })
    }

    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            val latitude = locationResult.lastLocation.latitude
            val longitude = locationResult.lastLocation.longitude
           /* MainActivity.latitude = latitude
            MainActivity.longitude = longitude

            MainActivity.updateLocation()*/
            Log.d("Location update", "$latitude, $longitude")
            updateLocation()
        }
    }


    override fun onBind(intent: Intent): IBinder? {
        throw UnsupportedOperationException("not yet implemented")
    }

    private fun startLocationService() {
        val channelId = "location_notification_channel"
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val resultIntent = Intent()
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            resultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val builder = NotificationCompat.Builder(
            applicationContext,
            channelId
        )
        builder.setSmallIcon(R.mipmap.ic_launcher)
        builder.setContentTitle("Location service")
        builder.setDefaults(NotificationCompat.DEFAULT_ALL)
        builder.setContentText("Running")
        builder.setContentIntent(pendingIntent)
        builder.setAutoCancel(false)

        builder.priority = NotificationCompat.PRIORITY_MAX
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager.getNotificationChannel(channelId) == null
            ) {
                val notificationChannel = NotificationChannel(
                    channelId,
                    "Location Service",
                    NotificationManager.IMPORTANCE_HIGH
                )
                notificationChannel.description = "This channel is used by location service"
                notificationManager.createNotificationChannel(notificationChannel)
                val locationRequest = LocationRequest.create()
                locationRequest.interval = 4000
                locationRequest.fastestInterval = 2000
                locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                LocationServices.getFusedLocationProviderClient(this)
                    .requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        Looper.getMainLooper()
                    )
                startForeground(
                    Constants.LOCATION_SERVICE_ID,
                    builder.build()
                )
            }
        }
    }

    private fun stopLocationService() {
        LocationServices.getFusedLocationProviderClient(this)
            .removeLocationUpdates(locationCallback)
        stopForeground(true)
        stopSelf()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val action = intent.action
        if (action != null) {
            if (action == Constants.ACTION_START_LOCATION_SERVICE) {
                startLocationService()
            } else if (action == Constants.ACTION_STOP_LOCATION_SERVICE) {
                stopLocationService()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }
}