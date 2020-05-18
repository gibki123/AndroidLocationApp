package com.example.locationml;

import android.content.Context;
import android.util.Log;

import com.google.android.libraries.places.api.model.Place;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class DataGenerator {
    // Set 20m per second on gps provider
    private static double LIKELIHOOD_LIMIT_TO_STAY = 0.2;
    private static double PROBABILITY_TO_STAY_LONGER = 0.5;
    private static double PROBABILITY_TO_STAY_MUCH_LONGER = 0.8;
    private static int timerCounter = 0;
    private static double startingTimestamp = 1276664400000.0; // Equivalent of 2010:01:01 00:00:00
    private static double fakeTimestamp = startingTimestamp;
    private static double probabilityDecreaser = 0.2;
    private static Date fakeTimeOfGeneration;
    private static double MINUTE_IN_MILLISECONDS = 60000;

    public void GenerateData(Context context, String placeName, double likelihood, String types, String urlAddress) {
        String likelihoodString = Double.toString(likelihood);
        if (likelihood > LIKELIHOOD_LIMIT_TO_STAY){
//            Log.d("Place", "Likelihood enough");
            Random generator = new Random();
            Boolean staying = true;
            double stayingProbability = PROBABILITY_TO_STAY_LONGER;
            double i = 0.0;
            while(staying) {
                SendData(context,placeName,likelihoodString,types,urlAddress);
                double generatedValue = generator.nextDouble() * 1;
                if(generatedValue < stayingProbability) {
                    IncrementTime();
                    stayingProbability = PROBABILITY_TO_STAY_MUCH_LONGER - (probabilityDecreaser * i);
                    i += 1;
                } else {
                    staying = false;
                }
            }
        } else {
//            Log.d("Place", "Likelihood not enough");
            SendData(context,placeName,likelihoodString,types,urlAddress);
        }
    }

    public static void IncrementTime() {
        timerCounter++;
        fakeTimestamp += 20.0 * MINUTE_IN_MILLISECONDS;
        if(timerCounter == 55) { // Time equals 00:00;
            timerCounter=0;
            fakeTimestamp += 340.0 * MINUTE_IN_MILLISECONDS;
        }
    }

    private void CreateNewDate() {
        fakeTimeOfGeneration = new Date((long)fakeTimestamp);
    }

    private void SendData(Context context, String placeName, String likelihoodString, String types, String urlAddress) {
        CreateNewDate();
        DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        Sender s =new Sender(context,urlAddress,placeName,likelihoodString,types,dateFormat.format(fakeTimeOfGeneration));
        s.execute();
    }
}
