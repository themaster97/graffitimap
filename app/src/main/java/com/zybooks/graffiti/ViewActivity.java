package com.zybooks.graffiti;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.graphics.Bitmap;
import android.provider.MediaStore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class ViewActivity  extends AppCompatActivity {

    private final int requestCode = 20;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.ActivityTheme_Primary_Base_Light);
        if (AppCompatDelegate.getDefaultNightMode()
                ==AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.ActivityTheme_Primary_Base_Dark);
        }
        setContentView(R.layout.view_mode);

        Button drawButton = findViewById(R.id.drawBut);

        drawButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cInt = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cInt,requestCode);
            }


        });


        Button viewClose = findViewById(R.id.backBut);

        viewClose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent viewMode = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(viewMode);
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(this.requestCode == requestCode && resultCode == RESULT_OK){
            Bitmap bitmap = (Bitmap)data.getExtras().get("data");
            Intent drawMode = new Intent(getApplicationContext(), DrawActivity.class);

            drawMode.putExtra("NewCapture", DbBitmapUtility.getBytes(bitmap));
            startActivity(drawMode);
            finish();
        }
    }

}
