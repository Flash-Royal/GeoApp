package com.example.geoapp

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    lateinit var googleMap: GoogleMap
    var url = "https://waadsu.com/api/"
    var data: Array<Array<Array<Array<Float>>>>? = null
    var lengthOfFigures = ArrayList<Double>()
    var length: Double = 0.toDouble()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        getGeoInfo()
    }

    //Получение координат с сервера и их обработка
    fun getGeoInfo() {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(GeoInterface::class.java)
        val call = service.getCurrentGeoData()

        call.enqueue(object : Callback<GeoResp> {
            override fun onResponse(call: Call<GeoResp>, response: Response<GeoResp>) {
                if (response.isSuccessful) {
                    var geoInfo = response.body()!!
                    data = geoInfo.features[0].geometry?.data
                    var options = ArrayList<PolylineOptions>()
                    var pointsOfPolygon = ArrayList<ArrayList<LatLng>>()
                    data!!.forEach { polygon ->
                        val option = PolylineOptions()
                        val coordinates = ArrayList<LatLng>()
                        polygon.forEach {  points ->
                            points.forEach {  point ->
                                var coord = LatLng(point[1].toDouble(), point[0].toDouble())
                                coordinates.add(coord)
                                option.add(coord)
                            }
                        }
                        pointsOfPolygon.add(coordinates)
                        options.add(option)
                    }

                    //Подсчет длину каждой фигуры
                    pointsOfPolygon.forEach { figure ->
                        var lengthOfFigure: Double = 0.toDouble()
                        figure.forEachIndexed { index, latLng ->
                            if (index > 0) {
                                lengthOfFigure += distance(latLng.latitude, latLng.longitude, figure[index - 1].latitude, figure[index - 1].longitude)
                            }
                        }
                        lengthOfFigures.add(lengthOfFigure)
                    }
                    //Подсчет общей длины полигонов
                    for (lengthOfFigure in lengthOfFigures) {
                        length += lengthOfFigure
                    }

                    Log.d("Info", "Summary length: ${length}")
                    findViewById<TextView>(R.id.len).text = "Общая длина пути: ${length} км."
                    //Отрисовка линий на карте
                    for (option in options) {
                        googleMap.addPolyline(option)
                    }
                }
            }

            override fun onFailure(call: Call<GeoResp>?, t: Throwable?) {
                TODO("Not yet implemented")
            }
        })
    }

    //Длина между двумя координатами
    fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val theta = lon1 - lon2
        var dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta))
        dist = Math.acos(dist)
        dist = rad2deg(dist)
        dist = dist * 60 * 1.1515
        dist = dist * 1.609344
        return dist
    }
    //Перевод градусов в радианы
    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }
    //Перевод радионов в градусы
    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }

    override fun onMapReady(p0: GoogleMap) {
        googleMap = p0
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(65.toDouble(), 100.toDouble())))
    }
}