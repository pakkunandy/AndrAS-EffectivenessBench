package com.andras.contactleakage;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class BluetoothActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;

    private TextView textViewContactInfo;

    private String contactName;
    private String phoneNumber;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        textViewContactInfo = findViewById(R.id.textViewContactInfo);

        Intent intent = getIntent();
        contactName = intent.getStringExtra("contactName");
        phoneNumber = intent.getStringExtra("phoneNumber");
        email = intent.getStringExtra("email");

        String contactInfo = "Name: " + contactName + "\nPhone number: " + phoneNumber + "\nEmail: " + email;
        textViewContactInfo.setText(contactInfo);

        Button buttonSendBluetooth = findViewById(R.id.buttonSendBluetooth);
        buttonSendBluetooth.setOnClickListener(view -> {
            if (bluetoothAdapter.isEnabled()) {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
                if (pairedDevices.size() > 0) {
                    for (BluetoothDevice device : pairedDevices) {
                        if (device.getName().equals("Your Bluetooth Device Name")) {
                            try {
                                UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
                                bluetoothSocket = device.createRfcommSocketToServiceRecord(uuid);
                                bluetoothSocket.connect();

                                OutputStream outputStream = bluetoothSocket.getOutputStream();
                                outputStream.write(contactInfo.getBytes());
                                outputStream.close();

                                Toast.makeText(BluetoothActivity.this, "Contact sent via Bluetooth", Toast.LENGTH_SHORT).show();

                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(BluetoothActivity.this, "Bluetooth connection error", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        }
                    }
                }
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        });

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth not supported", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            bluetoothSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
