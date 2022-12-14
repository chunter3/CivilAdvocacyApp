package com.example.civiladvocacyapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class PhotoActivity extends AppCompatActivity {

    private ConstraintLayout constraintLayout;
    private TextView currLocation;
    private TextView photoName;
    private TextView photoOffice;
    private ImageView photoImage;
    private ImageView photoPartyLogo;
    private GovOfficial govOfficial;
    private String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        currLocation = findViewById(R.id.photoCurrLocation);
        photoName = findViewById(R.id.photoNameOfOfficial);
        photoOffice = findViewById(R.id.photoOfficeOfOfficial);
        photoImage = findViewById(R.id.photoImageOfOfficial);
        photoPartyLogo = findViewById(R.id.photoPartyLogo);
        constraintLayout = findViewById(R.id.PhotoActivityLayout);

        Intent officialInfo = getIntent();
        govOfficial = (GovOfficial) officialInfo.getSerializableExtra("OFFICIAL_INFO");
        location = officialInfo.getStringExtra("LOCATION");

        currLocation.setText(location);
        photoName.setText(govOfficial.getName());
        photoOffice.setText(govOfficial.getOffice());
        if (govOfficial.getPhotoURL().isEmpty()) {
            Glide.with(this).load(R.drawable.missing).error(R.drawable.brokenimage).into(photoImage);
        } else {
            Glide.with(this).load(govOfficial.getPhotoURL()).error(R.drawable.brokenimage).into(photoImage);
        }
        if (govOfficial.getParty().equals("Democratic Party") || govOfficial.getParty().equals("Democrat Party")) {
            constraintLayout.setBackgroundResource(R.color.blue);
            photoPartyLogo.setImageResource(R.drawable.dem_logo);
        } else if (govOfficial.getParty().equals("Republican Party")) {
            constraintLayout.setBackgroundResource(R.color.red);
            photoPartyLogo.setImageResource(R.drawable.rep_logo);
        } else {
            constraintLayout.setBackgroundResource(R.color.black);
        }
        this.setTitle("Civil Advocacy");
    }

    public void clickLogo(View view) {
        if (govOfficial.getParty().equals("Democratic Party") || govOfficial.getParty().equals("Democrat Party")) {
            Uri uri = Uri.parse("https://democrats.org");
            Intent uriIntent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(uriIntent);
        }
        else if (govOfficial.getParty().equals("Republican Party")) {
            Uri uri = Uri.parse("https://www.gop.com");
            Intent uriIntent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(uriIntent);
        }
    }
}