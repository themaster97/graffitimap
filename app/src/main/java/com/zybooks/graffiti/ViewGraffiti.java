package com.zybooks.graffiti;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;

import java.util.ArrayList;

public class ViewGraffiti extends Activity {

    SqlDb db;
    ImageView imageHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.ActivityTheme_Primary_Base_Light);
        if (AppCompatDelegate.getDefaultNightMode()
                ==AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.ActivityTheme_Primary_Base_Dark);
        }
        setContentView(R.layout.image_view);

        db = SqlDb.getInstance(getApplicationContext());

        imageHolder = findViewById(R.id.imageView);

        Intent intent = getIntent();
        String i = intent.getStringExtra("ImgID");


        ArrayList<Image> images = db.getImages();

        Image P = images.get(Integer.valueOf(i) - 1);
        imageHolder.setImageBitmap(P.getBitImage());


    }
}