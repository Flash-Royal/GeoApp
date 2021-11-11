package com.example.geoapp

import com.google.gson.annotations.SerializedName

class GeoResp {
    @SerializedName("type")
    var type: String? = null
    @SerializedName("features")
    var features = ArrayList<FeatureResp>()
}

class FeatureResp {
    @SerializedName("type")
    var type: String? = null
//    @SerializedName("properties")
//    var properties = ArrayList<PropertyResp>()
    @SerializedName("geometry")
    var geometry: Geometry? = null
}

//class PropertyResp {
//
//}
class Geometry {
    @SerializedName("type")
    var type: String? = null
    @SerializedName("coordinates")
    var data: Array<Array<Array<Array<Float>>>>? = null
}