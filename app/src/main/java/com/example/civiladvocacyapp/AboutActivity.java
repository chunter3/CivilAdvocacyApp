package com.example.civiladvocacyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    public void goToCivicInformation(View view) {
        Uri uri = Uri.parse("https://developers.google.com/civic-information/");
        Intent uriIntent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(uriIntent);
    }
}