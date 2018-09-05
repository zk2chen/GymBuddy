package com.zkcdev.gymbuddy.models;

import com.google.android.gms.maps.model.LatLng;

public class PlaceInfo {

    private LatLng latLng;

    public PlaceInfo(LatLng latLng) {
        this.latLng = latLng;
    }

    public PlaceInfo(){

    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    @Override
    public String toString() {
        return "PlaceInfo{" +
                "latLng=" + latLng +
                '}';
    }
}
