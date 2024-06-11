package com.example.week6permissions

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class LatLon(var lat: Double, var lon: Double)
class LatLonViewModel: ViewModel(){
    var latLon = LatLon(51.05, -0.72)
        set(newValue){
            field = newValue
            latLonLiveData.value = newValue
        }
    var latLonLiveData = MutableLiveData<LatLon>()
}