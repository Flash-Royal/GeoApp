package com.example.geoapp

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GeoInterface {
    @GET("russia.geo.json")
    fun getCurrentGeoData(): Call<GeoResp>
}