package com.crewmates.autolibodb.api
import com.crewmates.autolibodb.model.Location
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LocationUpdateApi {

    @POST("AddPosition")
    suspend fun addPosition(
        @Body location : Location
    ): Response<Location>
}