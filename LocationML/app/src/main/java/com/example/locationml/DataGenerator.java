package com.example.locationml;

import android.content.Context;

import com.google.android.libraries.places.api.model.Place;

import java.util.Date;
import java.util.List;
import java.util.Random;

public class DataGenerator {
    private static double LIKELIHOOD_LIMIT_TO_STAY = 0.8;
    private static double PROBABILITY_TO_STAY_LONGER = 0.5;
    private static double PROBABILITY_TO_STAY_MUCH_LONGER = 0.8;
    private static double startingTimestamp = 1262340000 * 1000; // Equivalent of 2010:01:01 00:00:00
    private static double fakeTimestamp = startingTimestamp;
    private static double probabilityDecreaser = 0.2;
    private static Date fakeTimeOfGeneration;
    private static double MINUTE_IN_MILLISECONDS = 60000;

    public void GenerateData(Context context, String placeName, double likelihood, String types, String urlAddress) {
        if (likelihood > LIKELIHOOD_LIMIT_TO_STAY){
            Random generator = new Random();
            Boolean staying = true;
            String likelihoodString = Double.toString(likelihood);
            double stayingProbability = PROBABILITY_TO_STAY_LONGER;
            double i = 0.0;
            while(staying) {
                SendData(context,placeName,likelihoodString,types,urlAddress);
                double generatedValue = generator.nextDouble() * 1;
                if(generatedValue < stayingProbability) {
                    stayingProbability = PROBABILITY_TO_STAY_MUCH_LONGER - (probabilityDecreaser * i);
                    i += 1;
                } else {
                    staying = false;
                }
            }
        }
    }

    private void IncrementTime() {
        fakeTimestamp += 20 * MINUTE_IN_MILLISECONDS;
    }

    private void CreateNewDate() {
        fakeTimeOfGeneration = new Date((long)fakeTimestamp);
    }

    private void SendData(Context context, String placeName, String likelihoodString, String types, String urlAddress) {
        CreateNewDate();
        Sender s =new Sender(context,urlAddress,placeName,likelihoodString,types,fakeTimeOfGeneration);
        s.execute();
        IncrementTime();
    }
}
