package com.crewmates.autolibodb.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crewmates.autolibodb.model.*
import com.crewmates.autolibodb.repository.Repository
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response

class MainViewModel(private val repository: Repository): ViewModel() {
    val stateres: MutableLiveData<Vehicle> = MutableLiveData()
    val taskRes: MutableLiveData<Response<Task>> = MutableLiveData()
    val rentalRes: MutableLiveData<RentalInfo> = MutableLiveData()
    val panneRes: MutableLiveData<Message> = MutableLiveData()

    val locationResponse: MutableLiveData<Response<Location>> = MutableLiveData()
    val stateResponse: MutableLiveData<Response<VehicleState>> = MutableLiveData()
    val resStatCreated: MutableLiveData<Response<StateResponse>> = MutableLiveData()

    fun getState(chassisNumber: String){
        viewModelScope.launch {
            val response: Vehicle = repository.getState(chassisNumber)
            stateres.value = response
        }
    }
    fun addPosition(location: Location){
        viewModelScope.launch {
            val response: Response<Location> = repository.addPosition(location)
            locationResponse.value = response
        }
    }
    fun updateTechState(state: VehicleState){
        viewModelScope.launch {
            val response: Response<VehicleState> = repository.updateVehicleState(state)
            stateResponse.value = response
        }
    }
    fun createVs(chassisNumber : String){
        viewModelScope.launch {
            val response: Response<StateResponse> = repository.createVs(chassisNumber)
            resStatCreated.value = response
        }
    }
    fun getRentalInfo(chassisNumber : String){
        viewModelScope.launch {
            val response: RentalInfo = repository.getRentalInfo(chassisNumber)
            rentalRes.value = response
        }
    }

    fun alertOilChange(task: Task){
        viewModelScope.launch {
            val response: Response<Task> = repository.alertOilChange(task)
            taskRes.value = response
        }
    }

    fun detectPanne(panne: PannesData){
        viewModelScope.launch {
            val response: Message = repository.detectPannes(panne)
            panneRes.value = response
        }
    }

}