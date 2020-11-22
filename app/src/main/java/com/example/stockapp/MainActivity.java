package com.example.stockapp;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    RequestQueue queue;
    ArrayList<String> suggestions = new ArrayList<String>();
    public static final String EXTRA_MESSAGE = "com.example.stockapp.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        queue = Volley.newRequestQueue(this);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.myToolBar);
        setSupportActionBar(myToolbar);
        myToolbar.setTitleTextAppearance(this, R.style.BoldTextAppearance);

    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //Inflate the search menu action bar
        getMenuInflater().inflate(R.menu.main_menu, menu);

        // Get search menu
        MenuItem item = menu.findItem(R.id.app_bar_search);

        // Get searchView Object
        SearchView searchView = (SearchView) item.getActionView();

        // Get SearchView autocomplete object
        final SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete)searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchAutoComplete.setThreshold(3);

        //Create ArrayAdapter and add data to search autocomplete object;
//        String dataArr[] =  {"Apple" , "Amazon" , "Amd", "Microsoft", "Microwave", "MicroNews", "Intel", "Intelligence"};
//
//        ArrayAdapter<String> newsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, suggeestions);
//        searchAutoComplete.setAdapter(newsAdapter);

        // Listen to search view item on click event.
        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int itemIndex, long id) {
                String queryString=(String)adapterView.getItemAtPosition(itemIndex);
                searchAutoComplete.setText("" + queryString);
                //Toast.makeText(MainActivity.this, "you clicked " + queryString, Toast.LENGTH_LONG).show();
            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
//                alertDialog.setMessage("Search keyword is " + query);
//                alertDialog.show();
                Intent intent = new Intent(MainActivity.this, Detail.class);
                String message = query;
                intent.putExtra(EXTRA_MESSAGE, message);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //return false;
                getSuggestions(newText, searchAutoComplete);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }




    private void getSuggestions(String newText, SearchView.SearchAutoComplete searchAutoComplete) {
        suggestions = new ArrayList<String>();
        String url = "http://nodejsapp-env.eba-9tz487ps.us-east-1.elasticbeanstalk.com/autocomplete?arg1="+newText;

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //System.out.println("get success");
                JsonArray jsonArray = JsonParser.parseString(response).getAsJsonArray();

                for (int i=0;i<jsonArray.size(); i++) {
                    JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
                    JsonElement name = jsonObject.get("name");
                    JsonElement ticker = jsonObject.get("ticker");
                    if (!name.isJsonNull() && !ticker.isJsonNull()){
                        String stringName = name.getAsString();
                        String stringTicker = ticker.getAsString();
                        suggestions.add(stringTicker+" - "+stringName);
                    }
                }

                ArrayAdapter<String> newsAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_dropdown_item_1line, suggestions);
                searchAutoComplete.setAdapter(newsAdapter);

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });


        queue.add(request);

    }




}



