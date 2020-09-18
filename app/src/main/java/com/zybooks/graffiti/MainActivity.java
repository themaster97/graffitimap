package com.zybooks.graffiti;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

import static android.Manifest.permission.ACCESS_BACKGROUND_LOCATION;
import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity {

////////////////////// GPS LOCATION ///////////////////////////////////////////////////
    private final String TAG = "MainActivity";
    private FusedLocationProviderClient mClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissions = new ArrayList<>();
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private static final int ALL_PERMISSIONS_RESULT = 1011;
    public static boolean loggedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.ActivityTheme_Primary_Base_Light);
        if (AppCompatDelegate.getDefaultNightMode()
                ==AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.ActivityTheme_Primary_Base_Dark);
        }
        setContentView(R.layout.activity_main);

        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);

        permissionsToRequest = permissionsToRequest(permissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0) {
                requestPermissions(permissionsToRequest.toArray(
                        new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
            }
        }

        // Create location request
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(3000); //Update (get) location every 3 seconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Create location callback
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    for (Location location : locationResult.getLocations()) {
                        Log.d(TAG, "Location: " + location);
                    }
                }
            }
        };

        mClient = LocationServices.getFusedLocationProviderClient(this);


//////////////////////////////// BUTTONS  /////////////////////////////////////
        Button menuView = findViewById(R.id.drawBtn);

        menuView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent viewMode = new Intent(getApplicationContext(), ViewActivity.class);
                startActivity(viewMode);
            }

        });

        Button mapActivity = findViewById(R.id.exploreBtn);

        mapActivity.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent mapMode = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(mapMode);
            }
        });

        Button quitBtn = findViewById(R.id.quitBtn);

        quitBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        Switch themeSwitch = findViewById(R.id.switch1);

        themeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
                else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                ViewGroup vg = findViewById (R.id.main_layout);
                vg.invalidate();
            }
        });

    }


    //Found this method here: https://thecodelander.com/2019/android-studio/android-location-permissions/
    boolean hasLocationPermission() {
        //Check if Permissions are NOT granted...
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }
    }

    private ArrayList<String> permissionsToRequest(ArrayList<String> wantedPermissions) {
        ArrayList<String> result = new ArrayList<>();
        for (String perm : wantedPermissions) {
            if(!hasPermissions(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermissions(String permission) {
        if (Build.VERSION.SDK_INT >+ Build.VERSION_CODES.M) {
            return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

/////////////////////////////////// OTHER ACTIVITY STATES //////////////////////////////////////
    @Override
    protected void onPause() {
        super.onPause();
        mClient.removeLocationUpdates(mLocationCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if ( hasLocationPermission() ) {
            mClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        }
    }


}
