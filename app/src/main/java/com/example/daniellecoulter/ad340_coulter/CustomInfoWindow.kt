package com.example.daniellecoulter.ad340_coulter

import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.squareup.picasso.Picasso
import com.squareup.picasso.Callback


class CustomInfoWindow(var mContext: MapsActivity) : GoogleMap.InfoWindowAdapter {

    private val v: View;
    private var markerId = "";

    init {
        v = LayoutInflater.from(mContext).inflate(R.layout.activity_cam_layout, null)
    }


    override fun getInfoWindow(marker: Marker): View? {
        return null
    }

    override fun getInfoContents(marker: Marker): View? {


        return v

    }

    inner class InfoWindowRefresher(private val markerToRefresh: Marker) : Callback {

        override fun onSuccess() {
            if (!markerToRefresh.id.equals(markerId)) {
                markerId = markerToRefresh.id
                markerToRefresh.showInfoWindow()
            }
        }

        override fun onError(e: Exception) {}
    }

}

