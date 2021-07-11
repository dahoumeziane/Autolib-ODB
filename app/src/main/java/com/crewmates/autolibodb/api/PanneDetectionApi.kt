package com.crewmates.autolibodb.api
import com.crewmates.autolibodb.model.Location
import com.crewmates.autolibodb.model.Message
import com.crewmates.autolibodb.model.PannesData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface PanneDetectionApi {

    @POST("detect")
    suspend fun detectPanne(
        @Body panne : PannesData
    ): Message
}