package com.example.stockapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.text.DecimalFormat;

public class DetailUpdateService extends JobIntentService {
    private static final int UNIQUE_JOB_ID=1337;
    private static final String ACTION_BROADCAST = DetailUpdateService.class.getName() + "DetailUpdate";

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        String quote = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

    }

    static void enqueueWork(Context context) {
        enqueueWork(context, DetailUpdateService.class, UNIQUE_JOB_ID, new Intent(context, DetailUpdateService.class));
    }

    private void getPriceData (String quote) {
        String ticker = quote.split("-")[0];
        String getpriceurl = "http://nodejsapp-env.eba-9tz487ps.us-east-1.elasticbeanstalk.com/price?arg1="+ticker;

        final String[] pricedata = new String[1];
        StringRequest request1 = new StringRequest(Request.Method.GET, getpriceurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                pricedata[0] = response;

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
    }


}
