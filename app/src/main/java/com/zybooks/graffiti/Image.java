package com.zybooks.graffiti;

import android.graphics.Bitmap;


public class Image{
    private String id;
    private String lat;
    private String lon;
    private Bitmap img;

    public Image(String id, String lat, String lon, Bitmap img) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.img = img;
    }

    public String getImgId(){ return this.id; }

    public String getImgLat(){ return this.lat;
    }
    public String getImgLon(){ return this.lon; }

    public Bitmap getBitImage(){
        return this.img;
    }


    public void setImgId(String id) {
        this.id = id;
    }

    public void setImgLat(String lat) {
        this.lat = lat;
    }

    public void setImgLon(String lon) {
        this.lat = lon;
    }

    public void setBitImage(Bitmap img) { this.img = img; }
}
