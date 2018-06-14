package com.example.daniellecoulter.ad340_coulter

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.lang.UCharacter.GraphemeClusterBreak.L
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.example.daniellecoulter.ad340_coulter.R.id.map
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    override fun onMarkerClick(p0: Marker?): Boolean {
        /*
        Intent*/
        if(p0!!.tag != null){
            val tempCam = p0!!.tag as CameraObject
            DownloadImageTask(infoView).execute(tempCam.imageURL)
            //infoView.visibility = View.VISIBLE
        }
        p0!!.snippet = getAddress(p0!!.position)
        //p0!!.showInfoWindow()
        return false
    }

    //Add companion object to request premissions to access user's map/location
    //works with the lateninit vars to request continuous access to user's location
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        //private val MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9
        private const val REQUEST_CHECK_SETTINGS = 2
    }

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location //to request users last known location

    //Create location request: continuously receive updates of user's location
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var locationUpdateState = false
private lateinit var infoView: ImageView

    private var trafficCams: MutableList<CameraObject> = mutableListOf<CameraObject>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        infoView = findViewById(R.id.place_info)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        //Update lasLocation with new location and update map with new coordinates
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)

                lastLocation = p0.lastLocation
                placeMarkerOnMap(LatLng(lastLocation.latitude, lastLocation.longitude))
            }
        }//end locationCallback

        //Call for location updates
        //createLocationRequest()

    }//end onCreate

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map.setOnInfoWindowClickListener { infoView.visibility = View.VISIBLE }
        map.uiSettings.isZoomControlsEnabled = true
        map.setOnMarkerClickListener(this)
        map.setMinZoomPreference(10.0f)
        //map.setInfoWindowAdapter(CustomInfoWindow(this));
        //call request of user location
        setUpMap()
    }//end onMapReady

    //Method to enhance presence of user's blue dot location on map
    private fun placeMarkerOnMap(location: LatLng) {
        val markerOptions = MarkerOptions().position(location)

        val titleStr = getAddress(location)  // add these two lines
        markerOptions.title(titleStr)

        map.addMarker(markerOptions)
    }//end placeMarkerOnMap

    //Method to add camera markers
    private fun placeMarkerOnMap(location: LatLng, trafficCamera: CameraObject) {
//        val markerOptions = MarkerOptions().position(location)
//
//        val titleStr = getAddress(location)  // add these two lines
//        markerOptions.title(titleStr)

        map.addMarker(MarkerOptions().position(location).title(trafficCamera.name + "Click for picture")).tag=trafficCamera
    }//end placeMarkerOnMap



    //new method to check if ACCESS_FINE_LOCATION permissions were granted
    //if not, request it from the user
    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        //get user's current location by requesting last know location via Google Play services location APIs
        map.isMyLocationEnabled = true //#1: enables my-location layer and adds blue dot to map
        //#2: gives most recent location

        //Set Android Map API to hybrid type
        map.mapType = GoogleMap.MAP_TYPE_HYBRID

        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            // Got last known location. In some rare situations this can be null.
            // 3: If recent location success: move the camera to user's current location
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                placeMarkerOnMap(currentLatLng)//now add marker to the map
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
            }
        }

        //this is where we want to call the getJSON
        GetJSON(this).execute("https://web6.seattle.gov/Travelers/api/Map/Data?zoomId=13&type=2")

    }//end setUpMap

    //Add Geocoder. Show address of location when user click marker.
    //  take coordinates of location and returns readable address
    fun getAddress(latLng: LatLng): String {
        // 1
        val geocoder = Geocoder(this)
        val addresses: List<Address>?
        var addressText = "No Address Available"

        try {
            // 2
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            // 3
            if (null != addresses && !addresses.isEmpty()) {
                addressText = addresses[0].getAddressLine(0)
                return addressText
            }
        } catch (e: IOException) {
            Log.e("MapsActivity", e.localizedMessage)
        }

        return addressText
    }//end getAddress

    //Neccessary to find continuously user's location
//    private fun startLocationUpdates() {
//        //1
//        if (ActivityCompat.checkSelfPermission(this,
//                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
//                    LOCATION_PERMISSION_REQUEST_CODE)
//            return
//        }
//        //2
//        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null /* Looper */)
//    }//end startLocationUpdates

    //Add instance of LocationSettingsRequest.Builder to handle andy changes to user's location
//    private fun createLocationRequest() {
//        // 1: If access permission has not yet been granted, request it now and return
//        locationRequest = LocationRequest()
//        // 2: if permission granted, request location updates
//        locationRequest.interval = 10000//specifices rates at which app receives updates
//        // 3
//        locationRequest.fastestInterval = 5000//rate at which app can handle updates
//        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//
//        val builder = LocationSettingsRequest.Builder()
//                .addLocationRequest(locationRequest)
//
//        // 4
//        val client = LocationServices.getSettingsClient(this)
//        val task = client.checkLocationSettings(builder.build())
//
//        // 5
//        task.addOnSuccessListener {
//            locationUpdateState = true
//            startLocationUpdates()
//        }
//        task.addOnFailureListener { e ->
//            // 6
//            if (e is ResolvableApiException) {
//                // Location settings are not satisfied, but this can be fixed
//                // by showing the user a dialog.
//                try {
//                    // Show the dialog by calling startResolutionForResult(),
//                    // and check the result in onActivityResult().
//                    e.startResolutionForResult(this@MapsActivity,
//                            REQUEST_CHECK_SETTINGS)
//                } catch (sendEx: IntentSender.SendIntentException) {
//                    // Ignore the error.
//                }
//            }
//        }
//    }//end createLocationRequest

    //Start update request
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                locationUpdateState = true
                //startLocationUpdates()
            }
        }
    }//end onActivityResult

    // Stop location update request
    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }//end onPause

    // Restart location update request
    public override fun onResume() {
        super.onResume()
        if (!locationUpdateState) {
            //startLocationUpdates()
        }//end onResume
    }

    //Begin JSON material for HW7: Locations and Maps

    fun setInvis(view: View){
        infoView.visibility = View.INVISIBLE
    }



    //reuse of GetJSON from HW6: Live Cams assignment
    private inner class GetJSON(var mContext: MapsActivity) : AsyncTask<String, String, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
            Toast.makeText(this@MapsActivity, "JSON data is downloading", Toast.LENGTH_LONG).show()
        }

        override fun doInBackground(vararg params: String): String {

            //create connection and reader
            var connection: HttpURLConnection? = null
            var reader: BufferedReader? = null

            try {
                val url = URL(params[0])

                connection = url.openConnection() as HttpURLConnection
                connection.connect()

                val inputStream = connection.getInputStream()

                reader = BufferedReader(InputStreamReader(inputStream))

                val buffer = StringBuffer()
                var jsonLine = reader.readLine()

                while ((jsonLine) != null) {
                    buffer.append(jsonLine + "\n")
                    jsonLine = reader.readLine()
                }//end of while
                return buffer.toString()

            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                if (connection != null) {
                    connection.disconnect()
                }
                try {
                    if (reader != null) {
                        reader.close()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }//end of finally

            return "" //return empty string with no connection or data

        }//end of doInBackground

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            var jObj: JSONObject = JSONObject(result)
            var coords: JSONArray = jObj.getJSONArray("Features")
            for(i in 0..(coords.length()-1)) {
                val tempJason = JSONObject(coords[i].toString())
                val cameraCount = JSONArray(tempJason.getString("Cameras")).length() - 1

                for(j in 0..cameraCount) {

                    var tempCam: CameraObject = CameraObject()
                    tempCam.coords = JSONObject(coords[i].toString()).getString("PointCoordinate")
                    //tempCam.name = JSONArray(tempJason.getString("Cameras")).getJSONObject(j).getString("Id")
                    tempCam.name = JSONArray(tempJason.getString("Cameras")).getJSONObject(j).getString("Description")

                    tempCam.type = JSONArray(tempJason.getString("Cameras")).getJSONObject(j).getString("Type")
                    if(tempCam.type.equals("sdot")){
                        tempCam.imageURL = "http://www.seattle.gov/trafficcams/images/" + JSONArray(tempJason.getString("Cameras")).getJSONObject(j).getString("ImageUrl")
                    }else{
                        tempCam.imageURL = "http://images.wsdot.wa.gov/nw/" + JSONArray(tempJason.getString("Cameras")).getJSONObject(j).getString("ImageUrl")
                    }

                    val lat = tempCam.coords.substring(1,tempCam.coords.indexOf(",")).toDouble()
                    val long = tempCam.coords.substring(tempCam.coords.indexOf(",")+1,tempCam.coords.length-1).toDouble()
                    placeMarkerOnMap(LatLng(lat,long), tempCam)
                }//end of for loop
            }
        }//end of onPostExecute
    }//end of GetJSON
}


class DownloadImageTask(internal var bmImage: ImageView) : AsyncTask<String, Void, Bitmap>() {

    override fun doInBackground(vararg urls: String): Bitmap? {
        val urldisplay = urls[0]
        var mIcon11: Bitmap? = null
        try {
            val `in` = java.net.URL(urldisplay).openStream()
            mIcon11 = BitmapFactory.decodeStream(`in`)
        } catch (e: Exception) {
            //Log.e("Error", e.message)
            e.printStackTrace()
        }

        return mIcon11
    }

    override fun onPostExecute(result: Bitmap?) {
        if(result == null){
            bmImage.setImageResource(R.mipmap.ic_launcher)
        }else {
            bmImage.setImageBitmap(result)
        }
    }
}



