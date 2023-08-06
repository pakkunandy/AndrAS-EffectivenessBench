package com.andras.contactleakage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText editTextName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextName = findViewById(R.id.editTextName);

        Button buttonSend = findViewById(R.id.buttonSend);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("Range")
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString();
                String[] projection = new String[]{ContactsContract.Contacts._ID,
                        ContactsContract.Contacts.DISPLAY_NAME,
                        ContactsContract.Contacts.HAS_PHONE_NUMBER};
                String selection = ContactsContract.Contacts.DISPLAY_NAME + " = ?";
                String[] selectionArgs = new String[]{name};
                Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                        projection, selection, selectionArgs, null);
                if (cursor != null && cursor.moveToFirst()) {
                    String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    String phoneNumber = "";
                    String email = "";
                    int hasPhoneNumber = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                    if (hasPhoneNumber > 0) {
                        Cursor phoneCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{contactId},
                                null);
                        if (phoneCursor != null && phoneCursor.moveToFirst()) {
                            phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        }
                        phoneCursor.close();
                    }
                    Cursor emailCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                            new String[]{contactId},
                            null);
                    if (emailCursor != null && emailCursor.moveToFirst()) {
                        email = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    }
                    emailCursor.close();

                    Intent intent = new Intent(MainActivity.this, BluetoothActivity.class);
                    intent.putExtra("contactName", contactName);
                    intent.putExtra("phoneNumber", phoneNumber);
                    intent.putExtra("email", email);
                    startActivity(intent);

                    cursor.close();
                } else {
                    Toast.makeText(MainActivity.this, "Contact not found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}