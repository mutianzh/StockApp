package com.example.stockapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DecimalFormat;

public class Detail extends AppCompatActivity {

//    String ticker;
//    String name;
//    String currentprice;
//    String change;
//    String portfolio_message;
    public int pendingrequests = 4;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        queue = Volley.newRequestQueue(this);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.myToolBar);
        myToolbar.setTitleTextAppearance(this, R.style.BoldTextAppearance);
        myToolbar.setTitleTextColor(Color.BLACK);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get search quote
        Intent intent = getIntent();
        String quote = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        TextView textView = findViewById(R.id.textView);
        textView.setText(quote);

        findViewById(R.id.alldetails).setVisibility(View.GONE);
        findViewById(R.id.progressbar).setVisibility(View.VISIBLE);

        getData(quote);
    }

    private void getData(String quote){
        String ticker = quote.split("-")[0];

        String getpriceurl = "http://nodejsapp-env.eba-9tz487ps.us-east-1.elasticbeanstalk.com/price?arg1="+ticker;
        String getcompanyurl = "http://nodejsapp-env.eba-9tz487ps.us-east-1.elasticbeanstalk.com/company?arg1="+ticker;
        String getcharturl = "http://nodejsapp-env.eba-9tz487ps.us-east-1.elasticbeanstalk.com/year?arg1="+ticker;
        String getnewsurl = "http://nodejsapp-env.eba-9tz487ps.us-east-1.elasticbeanstalk.com/news?arg1="+ticker;

        final String[] pricedata = new String[1];
        final String[] companydata = new String[1];
        final String[] chartdata = new String[1];
        final String[] newsdata = new String[1];




        pendingrequests = 4;

        StringRequest request1 = new StringRequest(Request.Method.GET, getpriceurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //System.out.println("get success");

                pricedata[0] = response;

                pendingrequests--;
                if (pendingrequests == 0){
                    updateviews(pricedata, companydata, chartdata, newsdata);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        StringRequest request2 = new StringRequest(Request.Method.GET, getcompanyurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //System.out.println("get success");
                companydata[0] = response;
                pendingrequests--;
                if (pendingrequests == 0){
                    updateviews(pricedata, companydata, chartdata, newsdata);
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        StringRequest request3 = new StringRequest(Request.Method.GET, getcharturl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //System.out.println("get success");
                chartdata[0] = response;
                pendingrequests--;
                if (pendingrequests == 0){
                    updateviews(pricedata, companydata, chartdata, newsdata);
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        StringRequest request4 = new StringRequest(Request.Method.GET, getnewsurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //System.out.println("get success");
                newsdata[0] = response;
                pendingrequests--;
                if (pendingrequests == 0){
                    updateviews(pricedata, companydata, chartdata, newsdata);
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        queue.add(request1);
        queue.add(request2);
        queue.add(request3);
        queue.add(request4);


    }

    private  void updateviews(String[] pricedata, String[] companydata, String[] chartdata, String[] newsdata){

        JsonArray pricejson;
        JsonObject companyjson;
        JsonArray chartjson;
        JsonObject newsjson;

        // Check error cases
        // Wrong ticker error
        pricejson = JsonParser.parseString(pricedata[0]).getAsJsonArray();

        if (pricejson.size() == 0){
            findViewById(R.id.alldetails).setVisibility(View.GONE);
            findViewById(R.id.progressbar).setVisibility(View.GONE);
            TextView errormessage= findViewById(R.id.errormessage);
            errormessage.setText("Wrong ticker");

        }else{

            DecimalFormat df2 = new DecimalFormat("#.##");

            companyjson = JsonParser.parseString(companydata[0]).getAsJsonObject();
            chartjson = JsonParser.parseString(chartdata[0]).getAsJsonArray();
            newsjson = JsonParser.parseString(newsdata[0]).getAsJsonObject();

            TextView ticker = findViewById(R.id.ticker);
            ticker.setText(companyjson.get("ticker").getAsString());

            TextView price = findViewById(R.id.price);
            price.setText("$"+pricejson.get(0).getAsJsonObject().get("last").getAsString());

            TextView name = findViewById(R.id.name);
            name.setText(companyjson.get("name").getAsString());

            TextView change = findViewById(R.id.change);
            double changeindouble = pricejson.get(0).getAsJsonObject().get("last").getAsDouble() -
                    pricejson.get(0).getAsJsonObject().get("prevClose").getAsDouble();
            change.setText("$"+df2.format(changeindouble));
            if (changeindouble < 0){
                change.setTextColor(Color.RED);
            }else if(changeindouble > 0){
                change.setTextColor(Color.GREEN);
            }else{
                change.setTextColor(Color.BLACK);
            }






            findViewById(R.id.alldetails).setVisibility(View.VISIBLE);
            findViewById(R.id.progressbar).setVisibility(View.GONE);
            findViewById(R.id.errormessage).setVisibility(View.GONE);
        }


    }
}