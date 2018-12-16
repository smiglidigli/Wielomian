package com.example.m.wielomian;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button submitButton = findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                EditText textA = findViewById(R.id.textA);
                EditText textB = findViewById(R.id.textB);
                EditText textC = findViewById(R.id.textC);

                Integer paramA = Integer.parseInt(textA.getText().toString());
                Integer paramB = Integer.parseInt(textB.getText().toString());
                Integer paramC = Integer.parseInt(textC.getText().toString());

                Intent myIntent = new Intent(MainActivity.this, PolynomialActivity.class);

                myIntent.putExtra("A", paramA);
                myIntent.putExtra("B", paramB);
                myIntent.putExtra("C", paramC);

                startActivity(myIntent);
            }
        });

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new
                    String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            //return;
        }

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new
                    String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            //return;
        }
    }
}
