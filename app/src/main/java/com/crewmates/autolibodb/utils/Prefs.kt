package com.crewmates.autolibodb.utils

import android.location.Location
import java.util.*

object Prefs {
    var locations =
        ArrayList<Location>()
    var distance = 0.0

    fun distance_on_geoid(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {

        // Convert degrees to radians
        var lat1 = lat1
        var lon1 = lon1
        var lat2 = lat2
        var lon2 = lon2
        lat1 = lat1 * Math.PI / 180.0
        lon1 = lon1 * Math.PI / 180.0
        lat2 = lat2 * Math.PI / 180.0
        lon2 = lon2 * Math.PI / 180.0

        // radius of earth in metres
        val r = 6378100.0

        // P
        val rho1 = r * Math.cos(lat1)
        val z1 = r * Math.sin(lat1)
        val x1 = rho1 * Math.cos(lon1)
        val y1 = rho1 * Math.sin(lon1)

        // Q
        val rho2 = r * Math.cos(lat2)
        val z2 = r * Math.sin(lat2)
        val x2 = rho2 * Math.cos(lon2)
        val y2 = rho2 * Math.sin(lon2)

        // Dot product
        val dot = x1 * x2 + y1 * y2 + z1 * z2
        val cos_theta = dot / (r * r)
        val theta = Math.acos(cos_theta)

        // Distance in Metres
        return r * theta
    }

    fun calcSpeed(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double,
        t1: Int,
        t2: Int
    ): Double {
        val dist = distance_on_geoid(lat1, lon1, lat2, lon2)
        val time_s = (t2 - t1) / 1000.0
        val speed_mps = dist / time_s
        return speed_mps * 3600.0 / 1000.0
    }
}