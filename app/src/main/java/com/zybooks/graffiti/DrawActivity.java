package com.zybooks.graffiti;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import tech.picnic.fingerpaintview.FingerPaintImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class DrawActivity extends AppCompatActivity implements LocationListener{
    Spinner weight_spin, color_spin;
    FingerPaintImageView fpiv;
    final float small = (float) 5;
    final float medium = (float) 20;
    final float large = (float) 40;
    final float ex_large = (float) 70;
    double latitude;
    double longitude;
    Location location;
    protected LocationManager locMan;

    SqlDb mydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.ActivityTheme_Primary_Base_Light);
        if (AppCompatDelegate.getDefaultNightMode()
                ==AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.ActivityTheme_Primary_Base_Dark);
        }
        setContentView(R.layout.activity_draw);

        //find views
        weight_spin = findViewById(R.id.weightSpinner);
        color_spin = findViewById(R.id.colorSpinner);
        fpiv = findViewById(R.id.finger);
        fpiv.setStrokeWidth(small);
        locMan = (LocationManager) getSystemService(this.LOCATION_SERVICE);

        // Set canvas background to captured image
        Intent intent = getIntent();
        Bitmap bitmap2 = DbBitmapUtility.getImage(intent.getByteArrayExtra("NewCapture"));
        fpiv.setImageBitmap(bitmap2);

        //Got help from https://stackoverflow.com/questions/33415033/getting-current-location-in-android-studio-app
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

////////////////////// arm spinner listeners //////////////////////
        //weight spinner set up
        final List<String> weightList = Arrays.asList(getResources().getStringArray(R.array.weights_array));

        final ArrayAdapter<String> weightsArrayAdapter = new ArrayAdapter<String>(
                this, R.layout.spinner_item, weightList){};

        weightsArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        weight_spin.setAdapter(weightsArrayAdapter);

        //weight spinner onClick
        weight_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        fpiv.setStrokeWidth(small);
                        break;
                    case 1:
                        fpiv.setStrokeWidth(medium);
                        break;
                    case 2:
                        fpiv.setStrokeWidth(large);
                        break;
                    case 3:
                        fpiv.setStrokeWidth(ex_large);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //color spinner set up
        //see https://android--code.blogspot.com/2015/08/android-spinner-alternate-item-color.html
        final List<String> colorList = Arrays.asList(getResources().getStringArray(R.array.colors_array));

        final ArrayAdapter<String> colorsArrayAdapter = new ArrayAdapter<String>(
                this, R.layout.spinner_item, colorList){
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                switch (position) {
                    case 0:
                        tv.setTextColor(getResources().getColor(R.color.Black));
                        tv.setBackgroundColor(getResources().getColor(R.color.White));
                        break;
                    case 1:
                        tv.setTextColor(getResources().getColor(R.color.White));
                        tv.setBackgroundColor(getResources().getColor(R.color.Black));
                        break;
                    case 2:
                        tv.setTextColor(getResources().getColor(R.color.White));
                        tv.setBackgroundColor(getResources().getColor(R.color.Red));
                        break;
                    case 3:
                        tv.setTextColor(getResources().getColor(R.color.Black));
                        tv.setBackgroundColor(getResources().getColor(R.color.Blue));
                        break;
                    case 4:
                        tv.setTextColor(getResources().getColor(R.color.Black));
                        tv.setBackgroundColor(getResources().getColor(R.color.Green));
                        break;
                    case 5:
                        tv.setTextColor(getResources().getColor(R.color.Black));
                        tv.setBackgroundColor(getResources().getColor(R.color.Yellow));
                        break;
                    case 6:
                        tv.setTextColor(getResources().getColor(R.color.White));
                        tv.setBackgroundColor(getResources().getColor(R.color.Brown));
                        break;
                    case 7:
                        tv.setTextColor(getResources().getColor(R.color.Black));
                        tv.setBackgroundColor(getResources().getColor(R.color.Pink));
                        break;
                    case 8:
                        tv.setTextColor(getResources().getColor(R.color.Black));
                        tv.setBackgroundColor(getResources().getColor(R.color.Orange));
                        break;
                }
                return view;
            }
        };

        colorsArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        color_spin.setAdapter(colorsArrayAdapter);

        //color spinner onClick
        color_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) parent.getItemAtPosition(position);
                //Toast.makeText(DrawActivity.this, item, Toast.LENGTH_SHORT).show();
                switch (item) {
                    case "White":
                        fpiv.setStrokeColor(getResources().getColor(R.color.White));
                        break;
                    case "Black":
                        fpiv.setStrokeColor(getResources().getColor(R.color.Black));
                        break;
                    case "Red":
                        fpiv.setStrokeColor(getResources().getColor(R.color.Red));
                        break;
                    case "Blue":
                        fpiv.setStrokeColor(getResources().getColor(R.color.Blue));
                        break;
                    case "Green":
                        fpiv.setStrokeColor(getResources().getColor(R.color.Green));
                        break;
                    case "Yellow":
                        fpiv.setStrokeColor(getResources().getColor(R.color.Yellow));
                        break;
                    case "Brown":
                        fpiv.setStrokeColor(getResources().getColor(R.color.Brown));
                        break;
                    case "Pink":
                        fpiv.setStrokeColor(getResources().getColor(R.color.Pink));
                        break;
                    case "Orange":
                        fpiv.setStrokeColor(getResources().getColor(R.color.Orange));
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    ///// LOCATION FUNCTIONS /////

    @Override
    public void onLocationChanged(Location location) {
        if(location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            this.location = location;
        }
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
        Log.d("Latitude","status");
    }

    ///// OTHER FUNCTIONS & BUTTONS /////

    public void saveImage(Location location, Bitmap bit) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        mydb = SqlDb.getInstance(this);
        mydb.addImage(Double.toString(latitude) , Double.toString(longitude) , bit);
    }

    public void onCancelBtn(View v) {
        finish();
    }

    public void onSubmitBtn(View v) {
        Drawable d = fpiv.getDrawable();
        try {
            Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
            saveImage(location, bitmap);
        }
        catch (NullPointerException npe){
            npe.printStackTrace();
            Toast.makeText(this, "Unable to save Graffiti", Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}