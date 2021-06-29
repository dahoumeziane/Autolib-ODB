package com.crewmates.autolibodb.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crewmates.autolibodb.model.Location
import com.crewmates.autolibodb.model.StateResponse
import com.crewmates.autolibodb.model.Task
import com.crewmates.autolibodb.model.VehicleState
import com.crewmates.autolibodb.repository.Repository
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response

class MainViewModel(private val repository: Repository): ViewModel() {
    val stateres: MutableLiveData<VehicleState> = MutableLiveData()
    val taskRes: MutableLiveData<Response<Task>> = MutableLiveData()

    val locationResponse: MutableLiveData<Response<Location>> = MutableLiveData()
    val stateResponse: MutableLiveData<Response<VehicleState>> = MutableLiveData()
    val resStatCreated: MutableLiveData<Response<StateResponse>> = MutableLiveData()

    fun getState(){
        viewModelScope.launch {
            val response: VehicleState = repository.getState()
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

    fun alertOilChange(task: Task){
        viewModelScope.launch {
            val response: Response<Task> = repository.alertOilChange(task)
            taskRes.value = response
        }
    }
}