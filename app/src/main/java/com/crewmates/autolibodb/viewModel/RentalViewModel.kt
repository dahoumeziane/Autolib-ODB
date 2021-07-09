package com.crewmates.autolibodb.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crewmates.autolibodb.model.RentalBillVehicle
import com.crewmates.autolibodb.model.RentalInfo
import com.crewmates.autolibodb.repository.Repository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class RentalViewModel (private val repository: Repository): ViewModel() {
val rentalRes: MutableLiveData<RentalBillVehicle> = MutableLiveData()

    fun getRental(idUser:Int){
    viewModelScope.launch {
        val response: RentalBillVehicle = repository.getRentalUser(idUser)
        rentalRes.value = response
    }
}


}