package com.crewmates.autolibodb

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Observer
import com.crewmates.autolibodb.MainActivity.Companion.temperatureDisplay
import com.crewmates.autolibodb.MainActivity.Companion.viewModel
import com.crewmates.autolibodb.model.Location
import com.crewmates.autolibodb.model.VehicleState
import com.crewmates.autolibodb.utils.Prefs
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlin.random.Random

class LocationService : Service() {

    companion object {
        @JvmStatic  var isMyServiceRunning = false

    }
    private fun updateState(state : VehicleState){
        viewModel.updateTechState(state)
        viewModel.stateResponse.observe(MainActivity.context, Observer {
                response ->
            if (response.isSuccessful){
                Log.d("State uploaded", "success")
            }else {
                Log.d("error", "state not uploaded")
            }
        })
    }

    private fun updateLocation(latitude: Double,longitude: Double){

        val location = Location(
            latitude, longitude,16)
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
        MainActivity.gmap!!.clear()
        val latLng = LatLng(latitude, longitude)
        MainActivity.gmap!!.addMarker(MarkerOptions().position(latLng).title("Your position").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon)))
        val zoomLevel = 16.0f //This goes up to 21
        MainActivity.gmap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel))

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
            Prefs.locations.add(locationResult.lastLocation)
            var speed = locationResult.lastLocation.speed
            Log.d("Total distance", "${(Prefs.distance)}"+"km")
            speed = (speed*3600)/1000
            Log.d("time", "$speed"+"s")
            Log.d("Distance", "${Prefs.locations.size}"+"locations")
            if ( Prefs.locations.size > 2) {
                val loc1 = Prefs.locations[Prefs.locations.size-2]
                val loc2 = Prefs.locations[Prefs.locations.size-1]
                val distance = loc1.distanceTo(loc2).toInt()
                Log.d("Distance", "${loc1.distanceTo(loc2)}"+"m")
                Prefs.distance += (loc1.distanceTo(loc2)/1000)
                val vidange = 210000
                Log.d("Kilos", "${Prefs.distance}"+"km")
                Log.d("Vidange", "${(vidange)}"+"km")
                if( Prefs.distance > vidange){
                    //Alert agents 3la lvidange
                    Log.d("Vidange", "Alert dir vidange")
                }
                updateState(VehicleState(updateTemp(),10,16,40,30,70,40,speed.toInt(),distance))

            }
            updateLocation(latitude,longitude)
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
    fun updateTemp() : Int{
        var countDownTimer: CountDownTimer? = null
        val rnds = Random.nextInt(50,60)
        countDownTimer = object : CountDownTimer(30000, 1000) {
            override fun onTick(l: Long) {

                temperatureDisplay.text=(rnds.toString()+"C°")
                Log.d("tick", "onTick: $l")

            }

            override fun onFinish() {
                //timeout

            }
        }
        countDownTimer.start()
        return rnds

    }


}