package com.zybooks.graffiti;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class MapActivity extends Activity implements LocationListener {
    Location location;
    protected LocationManager locMan;

    SqlDb db;
    MapView map;
    ArrayList<Image> images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.ActivityTheme_Primary_Base_Light);
        if (AppCompatDelegate.getDefaultNightMode()
                ==AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.ActivityTheme_Primary_Base_Dark);
        }
        setContentView(R.layout.activity_map);

        locMan = (LocationManager) getSystemService(this.LOCATION_SERVICE);
        //Got help from https://stackoverflow.com/questions/33415033/getting-current-location-in-android-studio-app
        //Checks permissions for location
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                        == PackageManager.PERMISSION_GRANTED) {
            locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        }
        else{
            Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show();
            finish();
        }

        db = SqlDb.getInstance(getApplicationContext());

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);

        IMapController mapController = map.getController();
        mapController.setZoom(20);
        location = locMan.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        GeoPoint startPoint = new GeoPoint(location.getLatitude(),  location.getLongitude());
        mapController.setCenter(startPoint);

        images = db.getImages();

        RadiusMarkerClusterer clusterer = new RadiusMarkerClusterer(this);
        Drawable clusterIconD = getResources().getDrawable(R.drawable.graffiti_map_small);
        Bitmap clusterIcon = ((BitmapDrawable)clusterIconD).getBitmap();

        clusterer.setIcon(clusterIcon);
        map.getOverlays().add(clusterer);

        // Set all image markers on the map
        for (int i = 0; i < images.size(); i++) {
            Marker marker = new Marker(map);
            final Image P = images.get(i);
            marker.setPosition(new GeoPoint(Double.valueOf(P.getImgLon()), Double.valueOf(P.getImgLat())));
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
            marker.setIcon(getResources().getDrawable(R.mipmap.graffiti_marker));
            marker.setTitle(P.getImgId());
            marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker, MapView mapView) {
                    Intent intent = new Intent(getApplicationContext(), ViewGraffiti.class);
                    intent.putExtra("ImgID", P.getImgId());
                    startActivity(intent);

                    return true;
                }
            });

            clusterer.add(marker);
        }


        Button drawScreen = findViewById(R.id.mapDraw);
        drawScreen.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent drawScreen = new Intent(getApplicationContext(), ViewActivity.class);
                startActivity(drawScreen);
            }
        });

        Button back = findViewById(R.id.mapBack);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }

        });

    }

    public void onResume(){
        super.onResume();

        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up

        //checking for new graffiti
        if(db.getImages().size() != images.size()){
            map.getOverlays().clear();

            images = db.getImages();

            RadiusMarkerClusterer clusterer = new RadiusMarkerClusterer(this);
            Drawable clusterIconD = getResources().getDrawable(R.drawable.graffiti_map_small);
            Bitmap clusterIcon = ((BitmapDrawable)clusterIconD).getBitmap();

            clusterer.setIcon(clusterIcon);
            map.getOverlays().add(clusterer);

            // Set all image markers on the map
            for (int i = 0; i < images.size(); i++) {
                Marker marker = new Marker(map);
                final Image P = images.get(i);
                marker.setPosition(new GeoPoint(Double.valueOf(P.getImgLon()), Double.valueOf(P.getImgLat())));
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
                marker.setIcon(getResources().getDrawable(R.mipmap.graffiti_marker));

                marker.setTitle(P.getImgId());
                marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker, MapView mapView) {
                        Intent intent = new Intent(getApplicationContext(), ViewGraffiti.class);
                        intent.putExtra("ImgID", P.getImgId());
                        startActivity(intent);

                        return true;
                    }
                });

                clusterer.add(marker);
            }
        }
    }

    public void onPause(){
        super.onPause();

        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    ///// LOCATION FUNCTIONS /////

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        GeoPoint startPoint = new GeoPoint(location.getLatitude(),  location.getLongitude());
        IMapController mapController = map.getController();
        mapController.setCenter(startPoint);

        locMan.removeUpdates(this);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude","disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude","enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude", "status");
    }

}
