package com.crewmates.autolibodb.model

data class VehicleRental (
    val idVehicle: Int,
    val unitPricePerHour : Double,
    val unitPricePerDay: Double,
    val vehicleBrand : String
)
