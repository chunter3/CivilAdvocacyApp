package com.example.civiladvocacyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // INTERNET permission in AndroidManifest.xml in manifests folder
    // ACCESS_FINE_LOCATION and ACCESS_COARSE_LOCATION permissions in AndroidManifest.xml in manifests folder

    private FusedLocationProviderClient mFusedLocationClient;
    private static final int LOCATION_REQUEST = 111;

    private TextView noInternetConn;
    private TextView noInternetConnDescription;
    private TextView officialCurrLocation;
    private RecyclerView govOfficialRecyclerView;
    private GovOfficialAdapter govOfficialAdapter;
    private LinearLayoutManager linearLayoutManager;
    private String locationString = "Unspecified Location";
    private final List<GovOfficial> govOfficialList = new ArrayList<>();
    private final String TAG = getClass().getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Successfully created!");
        setContentView(R.layout.activity_main);

        noInternetConn = findViewById(R.id.noInternetConn);
        noInternetConnDescription = findViewById(R.id.noInternetConnDescription);
        officialCurrLocation = findViewById(R.id.officialCurrLocation);
        govOfficialRecyclerView = findViewById(R.id.govOfficialRecyclerView);
        govOfficialAdapter = new GovOfficialAdapter(govOfficialList, this);
        govOfficialRecyclerView.setAdapter(govOfficialAdapter);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        govOfficialRecyclerView.setLayoutManager(linearLayoutManager);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        determineLocation();

        if (!hasNetworkConnection() || locationString.equals("Unspecified Location")) {
            this.setTitle("Know Your Government");
            officialCurrLocation.setText(R.string.no_data_for_location);
            super.onCreate(savedInstanceState);
            return;
        }
        else {
            noInternetConn.setText("");
            noInternetConnDescription.setText("");
        }

        this.setTitle("Civil Advocacy");
        officialCurrLocation.setText(locationString);
        doDownload(locationString);
        super.onCreate(savedInstanceState);
    }

    private boolean hasNetworkConnection() {
        ConnectivityManager connectivityManager = getSystemService(ConnectivityManager.class);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnectedOrConnecting());
    }

    private void doDownload(String location) {
        GovOfficialVolley.downloadCivicInfo(this, location);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.aboutPage) {
            Intent appInfo = new Intent(this, AboutActivity.class);
            startActivity(appInfo);

        } else if (item.getItemId() == R.id.changeLocation) {
            AlertDialog.Builder adBuilder = new AlertDialog.Builder(this);
            adBuilder.setTitle("Enter Address");
            final EditText changeLocation = new EditText(this);
            adBuilder.setView(changeLocation);
            adBuilder.setPositiveButton("OK", (dialogInterface, i) -> {
                locationString = changeLocation.getText().toString();
                officialCurrLocation.setText(locationString);
                govOfficialList.clear();
                doDownload(locationString);
            });
            adBuilder.setNegativeButton("Cancel", (dialogInterface, i) -> {
            });
            AlertDialog dialog = adBuilder.create();
            dialog.show();

        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void updateHomeScreen(ArrayList<GovOfficial> govOffList) {
        if (govOffList == null) {
            Toast.makeText(this, "Invalid location", Toast.LENGTH_SHORT).show();
            return;
        }
        noInternetConn.setText("");
        noInternetConnDescription.setText("");
        govOfficialList.addAll(govOffList);
        govOfficialAdapter.notifyItemRangeChanged(0, govOffList.size());
        this.setTitle("Civil Advocacy");
    }

    public void downloadFailed() {
        govOfficialList.clear();
        govOfficialAdapter.notifyItemRangeChanged(0, govOfficialList.size());
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick: ");

        int position = govOfficialRecyclerView.getChildLayoutPosition(view);
        GovOfficial govOfficial = govOfficialList.get(position);

        Intent officialAct = new Intent(this, OfficialActivity.class);
        officialAct.putExtra("OFFICIAL_INFO", govOfficial);
        officialAct.putExtra("LOCATION", locationString);
        startActivity(officialAct);
    }

    private void determineLocation() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    // Got last known location. In some situations this can be null.
                    if (location != null) {
                        locationString = getPlace(location);
                        officialCurrLocation.setText(locationString);
                    }
                })
                .addOnFailureListener(this, e ->
                        Toast.makeText(MainActivity.this,
                                e.getMessage(), Toast.LENGTH_LONG).show());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST) {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    determineLocation();
                } else {
                    officialCurrLocation.setText(R.string.deniedText);
                }
            }
        }
    }

    private String getPlace(Location loc) {
        StringBuilder sb = new StringBuilder();
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            sb.append(String.format(
                    Locale.getDefault(),
                    "%s, %s%n%nProvider: %s%n%n%.5f, %.5f",
                    city, state, loc.getProvider(), loc.getLatitude(), loc.getLongitude()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: Start application");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Resume application");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: Application paused");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: Application stopped");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Application terminated");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart: Application restarted");
    }
}