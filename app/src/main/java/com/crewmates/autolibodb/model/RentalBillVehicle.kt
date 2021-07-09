package com.crewmates.autolibodb.model

data class RentalBillVehicle (
    val rental: Rental,
    val vehicle : VehicleRental,
    val bill: Bill
)