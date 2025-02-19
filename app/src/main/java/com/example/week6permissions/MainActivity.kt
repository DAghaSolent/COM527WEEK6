package com.example.week6permissions

import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import com.example.week6permissions.ui.theme.Week6PermissionsTheme
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import android.util.Log

class MainActivity : ComponentActivity(), LocationListener{

    val viewModel : LatLonViewModel  by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.addPOI(LatLon(50.91, -1.36, "Home Location"))
        viewModel.addPOI(LatLon(50.91, -1.39, "Southampton Football Club"))
        viewModel.addPOI(LatLon(50.92, -1.43, "Utilita Shirley Branch"))

        setContent {
            Week6PermissionsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MapComposable(geoPoint = GeoPoint(51.05, -0.72))
                }
            }
        }
        checkPermissions()
    }

    fun checkPermissions(){
        val requiredPermission = android.Manifest.permission.ACCESS_FINE_LOCATION

        if(checkSelfPermission(requiredPermission) == PackageManager.PERMISSION_GRANTED){
            // Here I will write code to start the GPS startGPS()
            startGPS()
        } else{
            // Requesting Location Permissions from the user
            val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ isGranted ->
                if(isGranted){
                    // Start the GPS Function here startGPS()
                    startGPS()
                } else{
                    // Permissions not granted from User, present a toast message back to the user.
                    Toast.makeText(this, "GPS permission not granted", Toast.LENGTH_LONG).show()
                }
            }
            permissionLauncher.launch(requiredPermission)
        }
    }

    fun startGPS(){
        val mgr = getSystemService(LOCATION_SERVICE) as LocationManager
        mgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0f, this)
    }

    override fun onLocationChanged(newLoc: Location){
        viewModel.latLon.lat = newLoc.latitude
        viewModel.latLon.lon = newLoc.longitude
        viewModel.latLon = LatLon(newLoc.latitude, newLoc.longitude, "")

        Toast.makeText(this, "Location=${newLoc.latitude},${newLoc.longitude}", Toast.LENGTH_LONG).show()
    }

    override fun onProviderEnabled(provider: String){
        Toast.makeText(this, "GPS Provider Enabled", Toast.LENGTH_LONG).show()
    }

    override fun onProviderDisabled(provider: String){
        Toast.makeText(this, "GPS Provider Disabled", Toast.LENGTH_LONG).show()
    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle){

    }

    @Composable
    fun MapComposable(geoPoint: GeoPoint) {
        var latlon by remember { mutableStateOf(LatLon(50.91, -1.36, "Home Location")) }
        var latLonList by remember { mutableStateOf(listOf<LatLon>()) }

        viewModel.latLonLiveData.observe(this) {
            latlon = it
        }

        viewModel.liveLatLonList.observe(this){
            latLonList = viewModel.latLonList

            if(latLonList.isEmpty()){
                Log.d("latlon", "Lat Lon List is empty.")
            }else{
                Log.d("latlon", "Lat Lon List is not empty")
            }
        }


        Box(modifier = Modifier.fillMaxSize()) {
            Row(modifier = Modifier
                    .fillMaxWidth()
                    .zIndex(2.0f)
            ) {
                Text("Latitude: ${latlon.lat}  Longitude: ${latlon.lon}")
            }

            AndroidView(
                factory = { ctx ->
                    // This line sets the user agent, a requirement to download OSM maps
                    Configuration.getInstance()
                        .load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))

                    val map1 = MapView(ctx).apply {
                            setClickable(true)
                            setMultiTouchControls(true)
                            setTileSource(TileSourceFactory.MAPNIK)
                            controller.setZoom(14.0)
                    }

                    map1
                },

                update = { view ->
                    view.controller.setCenter(GeoPoint(latlon.lat, latlon.lon))

                    for(poi in latLonList){
                        val marker = Marker(view)
                        marker.apply{
                            position = GeoPoint(poi.lat, poi.lon)
                            title = poi.title
                            icon = getDrawable(R.drawable.utilita)
                        }
                        view.overlays.add(marker)
                    }
                }
            )
        }
    }
}



