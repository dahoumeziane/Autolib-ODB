package com.crewmates.autolibodb.utils

import android.location.Location
import java.util.*

object Prefs {
    var locations =
        ArrayList<Location>()
    var distance : Double = 209999.7
    var oilChange : Double = 0.0
    var idRental : Int = 20
    var idBorn : Int = 0
    var fuelLevel : Int = 0
    var temperature : Int = 0
    var oilPressure : Int = 30
    var batteryCharge : Int = 70
    var brakeFluid : Int = 49

}