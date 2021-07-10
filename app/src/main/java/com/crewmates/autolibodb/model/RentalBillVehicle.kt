package com.crewmates.autolibodb.model

class RentalBillVehicle (
    val rental: Rental,
    val vehicle : VehicleRental,
    val bill: Bill,
    val diffjour:String,
    val diffheur:String,
    val diffminutes:String
)