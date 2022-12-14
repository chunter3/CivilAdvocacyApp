package com.example.civiladvocacyapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;


public class OfficialActivity extends AppCompatActivity {

    private ConstraintLayout constraintLayout;
    private TextView currLocation;
    private TextView officialName;
    private TextView officialOffice;
    private TextView officialParty;
    private TextView officialAddress;
    private TextView officialPhoneNum;
    private TextView officialEmail;
    private TextView officialWebsite;
    private ImageView officialImage;
    private ImageView officialPartyLogo;
    private ImageView facebookLogo;
    private ImageView youtubeLogo;
    private ImageView twitterLogo;
    private GovOfficial govOfficial;
    private String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official);

        constraintLayout = findViewById(R.id.OfficialActivityLayout);
        currLocation = findViewById(R.id.officialCurrLocation);
        officialName = findViewById(R.id.nameOfOfficial);
        officialOffice = findViewById(R.id.officeOfOfficial);
        officialParty = findViewById(R.id.partyOfOffical);
        officialAddress = findViewById(R.id.addressOfOfficial);
        officialPhoneNum = findViewById(R.id.phoneOfOfficial);
        officialEmail = findViewById(R.id.emailOfOfficial);
        officialWebsite = findViewById(R.id.websiteOfOfficial);
        officialImage = findViewById(R.id.imageOfOfficial);
        officialPartyLogo = findViewById(R.id.officialPartyLogo);
        facebookLogo = findViewById(R.id.facebookLogo);
        youtubeLogo = findViewById(R.id.youtubeLogo);
        twitterLogo = findViewById(R.id.twitterLogo);

        Intent officialInfo = getIntent();
        govOfficial = (GovOfficial) officialInfo.getSerializableExtra("OFFICIAL_INFO");
        location = officialInfo.getStringExtra("LOCATION");

        currLocation.setText(location);
        officialName.setText(govOfficial.getName());
        officialOffice.setText(govOfficial.getOffice());
        officialParty.setText(govOfficial.getParty());
        officialAddress.setText(govOfficial.getAddress());
        officialPhoneNum.setText(govOfficial.getPhoneNum());
        officialEmail.setText(govOfficial.getEmail());
        officialWebsite.setText(govOfficial.getWebsite());
        if (govOfficial.getPhotoURL().isEmpty()) {
            Glide.with(this).load(R.drawable.missing).error(R.drawable.brokenimage).into(officialImage);
        } else {
            Glide.with(this).load(govOfficial.getPhotoURL()).error(R.drawable.brokenimage).into(officialImage);
        }
        if (govOfficial.getParty().equals("Democratic Party") || govOfficial.getParty().equals("Democrat Party")) {
            constraintLayout.setBackgroundResource(R.color.blue);
            officialPartyLogo.setImageResource(R.drawable.dem_logo);
        }
        else if (govOfficial.getParty().equals("Republican Party")) {
            constraintLayout.setBackgroundResource(R.color.red);
            officialPartyLogo.setImageResource(R.drawable.rep_logo);
        }
        else {
            constraintLayout.setBackgroundResource(R.color.black);
        }
        if (govOfficial.getFacebookID().equals("")) {
            facebookLogo.setImageBitmap(null);
        }
        if (govOfficial.getYoutubeID().equals("")) {
            youtubeLogo.setImageBitmap(null);
        }
        if (govOfficial.getTwitterID().equals("")) {
            twitterLogo.setImageBitmap(null);
        }
        this.setTitle("Civil Advocacy");
    }

    public void implicitAddressIntent(View view) {
        Intent addressIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + Uri.encode(govOfficial.getAddress())));
        if (addressIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(addressIntent);
        } else {
            makeErrorAlert("No Application found that handles ACTION_VIEW (geo) intents");
        }
    }

    public void implicitPhoneIntent(View view) {
        String phoneNum = govOfficial.getPhoneNum();
        Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNum)); // "tel:" prefix prevents java.lang.IllegalStateException
        if (phoneIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(phoneIntent);
        } else {
            makeErrorAlert("No Application found that handles ACTION_DIAL (tel) intents");
        }
    }

    public void implicitEmailIntent(View view) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{govOfficial.getEmail()});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
        if (emailIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(emailIntent, "Send mail via..."));
        }
        else {
            makeErrorAlert("No Application found that handles SENDTO (mailto) intents");
        }
    }

    public void implicitWebsiteIntent(View view) {
        Intent websiteIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(govOfficial.getWebsite()));
        if (websiteIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(websiteIntent);
        }
        else {
            makeErrorAlert("No Application found that handles ACTION_VIEW (https) intents");
        }
    }

    public void clickFacebook(View view) {
        if (govOfficial.getFacebookID().isEmpty()) {
            return;
        }
        String FACEBOOK_URL = "https://www.facebook.com/" + govOfficial.getFacebookID();
        Intent fbIntent;
        // Check if Facebook is installed, if not use the browser instead
        if (isPackageInstalled("com.facebook.katana")) {
            String urlToUse = "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            fbIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlToUse));
        } else {
            fbIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(FACEBOOK_URL));
        }
        // Check if there is an app that can handle fb or https intents
        if (fbIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(fbIntent);
        } else {
            makeErrorAlert("No Application found that handles ACTION_VIEW (fb/https) intents");
        }
    }

    public void clickTwitter(View view) {
        if (govOfficial.getTwitterID().isEmpty()) {
            return;
        }
        String twitterAppUrl = "twitter://user?screen_name=" + govOfficial.getTwitterID();
        String twitterWebUrl = "https://twitter.com/" + govOfficial.getTwitterID();
        Intent twitIntent;
        // Check if Twitter is installed, if not we'll use the browser
        if (isPackageInstalled("com.twitter.android")) {
            twitIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitterAppUrl));
        } else {
            twitIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitterWebUrl));
        }
        if (twitIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(twitIntent);
        } else {
            makeErrorAlert("No Application found that handles ACTION_VIEW (twitter/https) intents");
        }
    }

    public void clickYouTube(View view) {
        if (govOfficial.getYoutubeID().isEmpty()) {
            return;
        }
        Intent ytIntent;
        try {
            ytIntent = new Intent(Intent.ACTION_VIEW);
            ytIntent.setPackage("com.google.android.youtube");
            ytIntent.setData(Uri.parse("https://www.youtube.com/" + govOfficial.getYoutubeID()));
            startActivity(ytIntent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/" + govOfficial.getYoutubeID())));
        }
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

    public void clickImage(View v) {
        if ((govOfficial.getPhotoURL().isEmpty()) || govOfficial.getPhotoURL() == null) {
            return;
        }
        Intent photoAct = new Intent(this, PhotoActivity.class);
        photoAct.putExtra("OFFICIAL_INFO", govOfficial);
        photoAct.putExtra("LOCATION", location);
        startActivity(photoAct);
    }

    public boolean isPackageInstalled(String packageName) {
        try {
            return getPackageManager().getApplicationInfo(packageName, 0).enabled;
        }
        catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void makeErrorAlert(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(msg);
        builder.setTitle("No App Found");

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}