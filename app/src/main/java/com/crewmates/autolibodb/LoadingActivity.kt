package com.crewmates.autolibodb

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject
import java.lang.Exception
import java.net.URISyntaxException

class LoadingActivity : AppCompatActivity() {

    private lateinit var viewModel: SharedViewModel
    private lateinit var mSocket: Socket

    private val onConnected: Emitter.Listener = Emitter.Listener {

        Log.e("error", "connected!")

        startActivity(Intent(this, MainActivity::class.java))

    }

    private val onError: Emitter.Listener = Emitter.Listener {
        this@LoadingActivity.runOnUiThread(Runnable {
            try {
                val data: Exception = it[0] as Exception
                Toast.makeText(this@LoadingActivity, data.message, Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                val data: JSONObject = it[0] as JSONObject

            }

        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_loading)

        try {
            val opts = IO.Options()
            opts.path = "/socket"


            mSocket = IO.socket("http://54.37.87.85:7001/", opts)
            mSocket.on("connected", onConnected)
            mSocket.on("error", onError)
            mSocket.on("connect_error", onError)
            mSocket.connect()

            val obj = JSONObject()
            obj.put("id", 3)
            mSocket.emit("connected vehicule", obj)
        } catch(e: URISyntaxException) {
            e.printStackTrace()
        }



    }

    override fun onDestroy() {
        super.onDestroy()
        mSocket.off("connected", onConnected)
    }
}