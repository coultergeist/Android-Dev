package com.example.daniellecoulter.ad340_coulter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.widget.ImageView

class CameraObject(type:String = "", description:String = "", longitude:Double = 0.0, latitude:Double = 0.0, coords: String = "", imageURL: String = "" ) {

    var type = type
    var name = description
    var long = longitude
    var lat = latitude
    var coords = coords
    var imageURL = imageURL

}
