package com.crewmates.autolibodb.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crewmates.autolibodb.model.Location
import com.crewmates.autolibodb.model.VehicleState
import com.crewmates.autolibodb.repository.Repository
import kotlinx.coroutines.launch
import retrofit2.Response

class MainViewModel(private val repository: Repository): ViewModel() {

    val locationResponse: MutableLiveData<Response<Location>> = MutableLiveData()
    val stateResponse: MutableLiveData<Response<VehicleState>> = MutableLiveData()

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
}