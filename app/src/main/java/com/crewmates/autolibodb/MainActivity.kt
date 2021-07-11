package com.crewmates.autolibodb

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.*
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.io.IOException
import java.net.URISyntaxException
import java.util.*

var bluetoothAdapter: BluetoothAdapter? = null

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


    private lateinit var mSocket: Socket
    private var nameTablet: String? = null

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
         fullname.text = "En attente d'une association"
         getbillrental(2)


     }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mapFragment = (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)!!
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

        val filter = IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        }
        registerReceiver(receiver, filter)


        try {
            val opts = IO.Options()
            opts.port = 8000
            opts.path = "/socket"
            mSocket = IO.socket("http://54.37.87.85:7001", opts)
            //mSocket = IO.socket("http://54.37.87.85:7001", opts)
        } catch(e: URISyntaxException) {
            e.printStackTrace()
        }

        mSocket.on(Socket.EVENT_CONNECT, onConnected)
        mSocket.on("error", onError)
        mSocket.on("connect_error", onError)
        mSocket.on("launch discovery", onLinkStarted)
        mSocket.on("stop association", onStopAssociation)
        mSocket.on("disconnect", onDisconnect)
        mSocket.connect()

    }


    override fun onMapReady(p0: GoogleMap) {
        gmap = p0

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        gmap!!.isMyLocationEnabled = true

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

        var p = ""
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

    private val onLinkStarted: Emitter.Listener = Emitter.Listener {
        val data: JSONObject = it[0] as JSONObject
        nameTablet = data.getString("nomBluetooth")
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        // Location Permission
        /*if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(this)
                    .setTitle("Location Permission Needed")
                    .setMessage("This app needs the Location permission, please accept to use location functionality")
                    .setPositiveButton(
                        "OK"
                    ) { _, _ ->
                        //Prompt the user once explanation has been shown
                        requestLocationPermission()
                    }
                    .create()
                    .show()
            } else {
                // No explanation needed, we can request the permission.
                requestLocationPermission()
            }
        }

        // Bluetooth enabling permission
        if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, 100)
        }

        // Discoverability permission
        val requestCode = 1
        val discoverableIntent: Intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
            putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
        }
        startActivityForResult(discoverableIntent, requestCode)

        */
        this.runOnUiThread(Runnable {Toast.makeText(this, "Recherche ...", Toast.LENGTH_SHORT).show()})
        bluetoothAdapter?.startDiscovery()

    }



    private val onConnected: Emitter.Listener = Emitter.Listener {
        val obj = JSONObject()
        obj.put("id", 5)
        mSocket.emit("connected vehicule", obj)
        this.runOnUiThread(Runnable {Toast.makeText(this, "OnConnected Event", Toast.LENGTH_SHORT).show()})
    }

    private val onStopAssociation: Emitter.Listener = Emitter.Listener {
        this.runOnUiThread(Runnable {
            Toast.makeText(this, "Association arrétée", Toast.LENGTH_SHORT).show()
            bluetoothAdapter = null
            val associationStatus = findViewById<TextView>(R.id.fullname)
            associationStatus.text = "En attente d'une association"
        })
    }



    private val onDisconnect: Emitter.Listener = Emitter.Listener {
        this@MainActivity.runOnUiThread(Runnable {
            Toast.makeText(this@MainActivity, "Diconnected!", Toast.LENGTH_SHORT).show()
        })
    }

    fun getSocket(): Socket {
        return mSocket
    }

    override fun onDestroy() {
        super.onDestroy()

        unregisterReceiver(receiver)
        mSocket.disconnect()
        mSocket.off("error", onError)
        mSocket.off("connect_error", onError)
    }

    private val onError: Emitter.Listener = Emitter.Listener {
        this@MainActivity.runOnUiThread(Runnable {
            try {
                val data: java.lang.Exception = it[0] as java.lang.Exception
                Toast.makeText(this@MainActivity, data.message, Toast.LENGTH_SHORT).show()
            } catch (e: java.lang.Exception) {
                val data: JSONObject = it[0] as JSONObject

            }

        })
    }

    private fun requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ),
                100
            )
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                100
            )
        }
    }

    private val receiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            when(intent.action!!) {

                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                    Toast.makeText(this@MainActivity, "Recherche commencée ...", Toast.LENGTH_SHORT).show()

                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    Toast.makeText(this@MainActivity, "Recherche terminée ...", Toast.LENGTH_SHORT).show()

                }

                BluetoothDevice.ACTION_FOUND -> {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    val deviceName = device?.name
                    if (deviceName?.compareTo(nameTablet!!) == 0) {
                        val connectThread = ConnectThread(device!!,this@MainActivity)
                        connectThread.start()
                    }
                }

            }
        }
    }

}

private class ConnectThread(val device: BluetoothDevice, val activity: Activity) : Thread() {

    private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
        device.createRfcommSocketToServiceRecord(UUID(100, 200))
    }

    public override fun run() {
        // Cancel discovery because it otherwise slows down the connection.
        bluetoothAdapter?.cancelDiscovery()

        mmSocket?.let { socket ->
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            try {
                socket.connect()
            } catch (exception: Exception) {
                activity.runOnUiThread { Toast.makeText(activity, "Exception on connect ${exception.localizedMessage}", Toast.LENGTH_SHORT).show() }
            }


            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            activity.runOnUiThread {
                val associationStatus = activity.findViewById<TextView>(R.id.fullname)
                associationStatus.text = "Associé avec le locataire ${socket.remoteDevice.name}"
            }


            try {
                mmSocket?.close()
            } catch (e: IOException) {
                Log.e(ContentValues.TAG, "Could not close the client socket", e)
            }
        }
    }
}