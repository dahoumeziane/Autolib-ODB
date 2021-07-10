package com.crewmates.autolibodb.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crewmates.autolibodb.MainActivity
import com.crewmates.autolibodb.model.RentalBillVehicle
import com.crewmates.autolibodb.model.RentalInfo
import com.crewmates.autolibodb.repository.Repository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Response
import java.util.logging.Logger

class RentalViewModel (private val repository: Repository): ViewModel() {
val rentalbillRes: MutableLiveData<Response<RentalBillVehicle>> = MutableLiveData()

    fun getRental(idUser:Int){
    viewModelScope.launch {
        Logger.getLogger(MainActivity::class.java.name).warning("Hello22222222222222222222AA")

        val response: Response<RentalBillVehicle> = repository.getRentalUser(idUser)
        Logger.getLogger(MainActivity::class.java.name).warning("Hello22222222222222222222")

        rentalbillRes.value = response
    }
}


}