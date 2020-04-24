package com.example.locationml;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity {

    private static Handler handler = new Handler();
    private Runnable likelihoodsRunnable;
    private static final String APP_KEY = "AIzaSyAIv8KvG6Sz5S87c2QTcMc_z-BYL7kX3C8";
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;
    private static final float LIKELIHOOD_LIMIT_PERCENTAGE = 0.05f;
    private static final int REFRESH_DATA_FREQUENCY_TIME = 300000; // 5minutes delay after each data transfer
    private static List<PlaceLikelihood> placeLikelihoods;
    public PlacesClient placesClient;

    public String place;
    public String likelihood;
    private static final String urlAdrress="https://serwer1990534.home.pl/PostLocation.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        placesClient = InitPlaces();
        likelihoodsRunnable = CreateRunnableForLikelihood();
        RunGetPlacesTask(likelihoodsRunnable);
    }

    private Runnable CreateRunnableForLikelihood() {
        return new Runnable() {
            @Override
            public void run() {
                GetNearbyPlaces(LIKELIHOOD_LIMIT_PERCENTAGE);
                handler.postDelayed(this,REFRESH_DATA_FREQUENCY_TIME);
            }
        };
    }

    private void RunGetPlacesTask(Runnable runnable) {
        handler.post(runnable);
    }

    private void StopGetPlaceTask(Runnable runnable) {
        handler.removeCallbacks(runnable);

    }

    PlacesClient InitPlaces() {
        Places.initialize(getApplicationContext(), APP_KEY);
        return Places.createClient(this);
    }

    void GetNearbyPlaces(float likelihoodLimit) {
        // Use fields to define the data types to return.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.TYPES);
        // Use the builder to create a FindCurrentPlaceRequest.
        FindCurrentPlaceRequest request =
        FindCurrentPlaceRequest.newInstance(placeFields);
        // Call findCurrentPlace and handle the response (first check that the user has granted permission).
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);
            placeResponse.addOnCompleteListener(task -> {
                String TAG = "Place";
                if (task.isSuccessful()){
                            FindCurrentPlaceResponse response = task.getResult();
                            for (PlaceLikelihood placeLikelihood : placeLikelihoods = response.getPlaceLikelihoods()) {
                                if(placeLikelihood.getLikelihood() > LIKELIHOOD_LIMIT_PERCENTAGE) {
                                    Log.d(TAG, String.format("Place '%s' has likelihood: %f", placeLikelihood.getPlace().getName(), placeLikelihood.getLikelihood()));
                                    place = placeLikelihood.getPlace().getName();
                                    likelihood = Double.toString(placeLikelihood.getLikelihood());
                                    Sender s =new Sender(MainActivity.this,urlAdrress,place,likelihood);
                                    s.execute();
                                    TextView textView = (TextView)findViewById(R.id.text);
                                    textView.setText(placeLikelihood.getPlace().getName());
                                }
                            }
                            Log.d(TAG, String.format("type '%s'", response.toString()));
                }
                    else {
                    Exception exception = task.getException();
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                    }
                }
            });
        } else {
            Log.d("Place","Ask for permission");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("Place","Permission Granted");
                    GetNearbyPlaces(LIKELIHOOD_LIMIT_PERCENTAGE);
                } else {
                    Log.d("Place","Permission not Granted");
                }
                return;
            }

        }
    }

}
