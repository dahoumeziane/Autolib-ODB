package com.crewmates.autolibodb

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.crewmates.autolibodb.repository.Repository
import com.crewmates.autolibodb.utils.Prefs
import com.crewmates.autolibodb.viewModel.MainViewModel
import com.crewmates.autolibodb.viewModel.MainViewModelFactory
import com.crewmates.autolibodb.viewModel.RentalViewModel
import com.crewmates.autolibodb.viewModel.RentalViewModelFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_main.*
import java.util.logging.Logger

class MainActivity : FragmentActivity(), OnMapReadyCallback {
    companion object {
        @JvmStatic lateinit var viewModel: MainViewModel
        @JvmStatic lateinit var context : LifecycleOwner
        @JvmStatic lateinit var temperatureDisplay : TextView
        @JvmStatic lateinit var speedDisplay : TextView
        @JvmStatic lateinit var distanceDisplay : TextView
        @JvmStatic var gmap : GoogleMap? = null

    }



    private val REQUEST_CODE_LOCATION_PERMISSION = 1
    var latitude = 0.0
    var longitude = 0.0


    override fun onStart() {
        super.onStart()
        context=this
        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this,viewModelFactory)
            .get(MainViewModel::class.java)
        temperatureDisplay= findViewById(R.id.tempDisplay)
        speedDisplay=findViewById(R.id.speed)
        distanceDisplay=findViewById(R.id.distance)
        //Initialization of fields
        val distance = intent.getIntExtra("distance", 0)
        val fuel = intent.getIntExtra("fuel", 0)
        val temp = intent.getIntExtra("temperature", 0)
        val idRental = intent.getIntExtra("idRental", 0)
        val oilChange = intent.getIntExtra("nextOilChange", 0)
        val idBorn = intent.getIntExtra("IdBorne", 0)
        Prefs.idRental = idRental
        Prefs.oilChange = oilChange
        Prefs.temperature= temp
        Prefs.fuelLevel = fuel
        Prefs.distance = distance
        Prefs.idBorn = idBorn
        fullname.text = "Good morning "+intent.getStringExtra("fullName")
        getbillrental(idRental)


    }
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

        prixpDisplay.setOnClickListener {
            getbillrental(2)
        }

    }


    override fun onMapReady(p0: GoogleMap) {
        gmap = p0
        val latLng : LatLng = if (longitude> 0){
            LatLng(latitude, longitude)
        }else {
            LatLng(36.704998, 3.173918)
        }



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

        return LocationService.isMyServiceRunning

    }

    private fun startLocationService() {
        if (!isLocationServiceRunning()) {
            val intent = Intent(applicationContext, LocationService::class.java)
            intent.action = Constants.ACTION_START_LOCATION_SERVICE
            startService(intent)
            LocationService.isMyServiceRunning = true
            Toast.makeText(this, "Location service started", Toast.LENGTH_SHORT).show()

        }
    }

    private fun stopLocationService() {
        if (isLocationServiceRunning()) {
            LocationService.isMyServiceRunning = false
            val intent = Intent(applicationContext, LocationService::class.java)
            intent.action = Constants.ACTION_STOP_LOCATION_SERVICE
            startService(intent)
            Toast.makeText(this, "Location service stopped", Toast.LENGTH_SHORT).show()
        }
    }
    private fun getbillrental(idUser:Int){

        var p=""
        val repository=Repository()
        val RviewModelFactory= RentalViewModelFactory(repository)
        val  viewM=ViewModelProvider(this,RviewModelFactory).get(RentalViewModel::class.java)

        viewM.getRental(idUser)

        viewM.rentalbillRes.observe(MainActivity.context , Observer {
                response ->
            if (response.isSuccessful){
                Toast.makeText(this, "AVANT", Toast.LENGTH_SHORT).show()
                prixpDisplay.text=response.body()?.bill?.totalRate.toString()
                time_id.text=response.body()?.diffjour?.toString()+"j"+response.body()?.diffheur?.toString()+"H"+response.body()?.diffminutes?.toString()+"m"
            }else {
                Toast.makeText(this, "UNE ERREUR S'EST PRODUITE", Toast.LENGTH_SHORT).show()
            }
        })


    }
}