package com.example.week6permissions

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class LatLon(var lat: Double, var lon: Double, var title:String)
class LatLonViewModel: ViewModel(){
    var latLon = LatLon(50.91, -1.36,"Home Location")
        set(newValue){
            field = newValue
            latLonLiveData.value = newValue
        }
    var latLonLiveData = MutableLiveData<LatLon>()

    var latLonList = mutableListOf<LatLon>()
    fun addPOI(poi: LatLon){
        latLonList.add(poi)
        liveLatLonList.value = latLonList
    }

    var liveLatLonList = MutableLiveData<List<LatLon>>()
}