package com.crewmates.autolibodb.init

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.crewmates.autolibodb.MainActivity
import com.crewmates.autolibodb.R
import com.crewmates.autolibodb.repository.Repository
import com.crewmates.autolibodb.viewModel.MainViewModel
import com.crewmates.autolibodb.viewModel.MainViewModelFactory
import kotlinx.android.synthetic.main.activity_welcom.*

class WelcomAct : AppCompatActivity() {
    private lateinit var viewModel : MainViewModel
    private  var idBorne : Int = 0
    private var loading: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcom)
        loading = ProgressDialog(this)
        loading!!.setMessage("Please wait while we are making the association")
        loading!!.setTitle("Association in progress")


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

        go.setOnClickListener {
            loading!!.show()
            createVs()
        }


    }
    private fun createVs(){
        viewModel.createVs(numeroChassis.text.toString())
        viewModel.resStatCreated.observe(this, Observer {
                response ->
            Log.d("response", response.toString())
            Log.d("response", response.isSuccessful.toString())
            if (response.isSuccessful){
                Log.d("State created", "success")
                viewModel.getRentalInfo(numeroChassis.text.toString())
                val i = Intent(this@WelcomAct, MainActivity::class.java)
                viewModel.rentalRes.observe(this, Observer {
                        response ->
                    Log.d("response",response.tenantFirstName)
                    i.putExtra("fullName", "${response.tenantFirstName} ${response.tenantLastName}")
                    i.putExtra("idVehicule", response.idVehicle)
                    val dist = initialDistance.text.toString()

                    i.putExtra("distance", dist.toInt())
                    i.putExtra("fuel", fuelLevel.text.toString().toInt())
                    i.putExtra("idRental", response.idRental)
                    i.putExtra("nextOilChange", nextOilChange.text.toString().toInt())


                    Log.d("response", response.idRental.toString())
                    i.putExtra(
                        "temperature",
                        temperature.text.toString().toInt()
                    )
                    viewModel.getState(numeroChassis.text.toString())
                    viewModel.stateres.observe(this, Observer {
                            response ->
                        i.putExtra("IdBorne", response.idBorne)
                        Log.d("response borne", idBorne.toString())
                        startActivity(i)
                        loading!!.dismiss()
                    })


                })
            }else {
                loading!!.dismiss()
                Log.d("error", "state not uploaded")
            }
        })
    }
}