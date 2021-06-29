package com.crewmates.autolibodb.init

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.crewmates.autolibodb.MainActivity
import com.crewmates.autolibodb.R
import com.crewmates.autolibodb.model.VehicleState
import com.crewmates.autolibodb.repository.Repository
import com.crewmates.autolibodb.viewModel.MainViewModel
import com.crewmates.autolibodb.viewModel.MainViewModelFactory
import kotlinx.android.synthetic.main.activity_welcom.*

class WelcomAct : AppCompatActivity() {
    private lateinit var viewModel : MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcom)

        val initialDistance = findViewById<EditText>(R.id.initialDistance)
        val fuellevel = findViewById<EditText>(R.id.fuelLevel)
        val temperature = findViewById<EditText>(R.id.temperature)
        val chasis = findViewById<EditText>(R.id.numeroChassis)
       /* go.setOnClickListener {
            val dist = initialDistance.text.toString()
            val i = Intent(this@WelcomAct, MainActivity::class.java)
            i.putExtra("distance", java.lang.Double.valueOf(dist))
            i.putExtra("fuel", fuellevel.text.toString().toInt())

            i.putExtra(
                "temperature",
                temperature.text.toString().toInt()
            )
            startActivity(i)
        }*/

        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this,viewModelFactory)
            .get(MainViewModel::class.java)
      /*  viewModel.getState()
        viewModel.stateres.observe(this, Observer {
                response ->
            Log.d("Response", response.idRental.toString())
            go.setOnClickListener {

                val dist = initialDistance.text.toString()
                val i = Intent(this@WelcomAct, MainActivity::class.java)
                i.putExtra("distance", java.lang.Double.valueOf(dist))
                i.putExtra("fuel", fuellevel.text.toString().toInt())
                i.putExtra("idRental", response.idRental)
                i.putExtra(
                    "temperature",
                   temperature.text.toString().toInt()
                )

               // startActivity(i)
            }
        })*/
        go.setOnClickListener {
            createVs()
        }


    }
    private fun createVs(){
        viewModel.createVs("AF1LM2")
        viewModel.resStatCreated.observe(this, Observer {
                response ->
            if (response.isSuccessful){
                Log.d("State created", "success")
                viewModel.getRentalInfo("AF1LM2")
                viewModel.rentalRes.observe(this, Observer {
                        response ->
                   Log.d("response",response.tenantFirstName)
                })
            }else {
                Log.d("error", "state not uploaded")
            }
        })
    }
}