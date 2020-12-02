package com.example.stockapp;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;


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


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;


public class MainActivity extends AppCompatActivity {

    RequestQueue queue;
    private int pendingrequests;

    ArrayList<String> suggestions = new ArrayList<String>();
    private AutoSuggestAdapter autoSuggestAdapter;

    public static final String EXTRA_MESSAGE = "com.example.stockapp.MESSAGE";

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String CASH = "cash";
    public static final String FAVORITE_LIST="favoriteList";
    public static final String PORTFOLIO_LIST = "portfolioList";

    Map<String,?> portfolioList;
    Map<String,?> favoriteList;

    ArrayList<StockItem> portfolioItemList;
    ArrayList<StockItem> favoriteItemList;

    private HashMap<String, Double> lastPrice = new HashMap<String, Double>();
    private HashMap<String, Double> changeInPrice = new HashMap<String, Double>();
    private HashMap<String, String> numShares = new HashMap<String, String>();
    private HashMap<String, String> companyName = new HashMap<String, String>();

    private StockSection portfolioSection;
    private StockSection favoriteSection;
    private SectionedRecyclerViewAdapter sectionedAdapter;
    private RecyclerView recyclerView;

    public static final int DELAY = 3600;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        queue = Volley.newRequestQueue(this);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.myToolBar);
        setSupportActionBar(myToolbar);
        myToolbar.setTitleTextAppearance(this, R.style.BoldTextAppearance);
        resetCash();

        getData();

        final Handler handler = new Handler();
        final int delay = DELAY * 1000; // 1000 milliseconds == 1 second

        handler.postDelayed(new Runnable() {
            public void run() {
                //System.out.println("myHandler: here!");
                getData();

                handler.postDelayed(this, delay);
            }
        }, delay);



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
        autoSuggestAdapter = new AutoSuggestAdapter(this, android.R.layout.simple_dropdown_item_1line);
        final SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete)searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchAutoComplete.setThreshold(3);
        searchAutoComplete.setAdapter(autoSuggestAdapter);

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

    public void getSuggestions(String newText, SearchView.SearchAutoComplete searchAutoComplete) {
        suggestions = new ArrayList<String>();
        String url = "http://nodejsapp-env.eba-9tz487ps.us-east-1.elasticbeanstalk.com/autocomplete?arg1="+newText;

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //System.out.println("get success");
                JsonArray jsonArray = JsonParser.parseString(response).getAsJsonArray();

                for (JsonElement jsonElement : jsonArray) {
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    JsonElement name = jsonObject.get("name");
                    JsonElement ticker = jsonObject.get("ticker");
                    if (!name.isJsonNull() && !ticker.isJsonNull()){
                        String stringName = name.getAsString();
                        String stringTicker = ticker.getAsString();
                        suggestions.add(stringTicker+" - "+stringName);
                    }
                }

                autoSuggestAdapter.setData(suggestions);
                autoSuggestAdapter.notifyDataSetChanged();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });


        queue.add(request);

    }

    public void getData(){
//        Map<String,?> portfolioList = getSharedPreferences(PORTFOLIO_LIST, MODE_PRIVATE).getAll();
//        Map<String,?> favoriteList = getSharedPreferences(FAVORITE_LIST, MODE_PRIVATE).getAll();

        portfolioList = getSharedPreferences(PORTFOLIO_LIST, MODE_PRIVATE).getAll();
        favoriteList = getSharedPreferences(FAVORITE_LIST, MODE_PRIVATE).getAll();

        StringBuilder tickerSet = new StringBuilder();

        for (Map.Entry <String, ?> entry: favoriteList.entrySet()){

            tickerSet.append(entry.getKey()).append(",");
            companyName.put(entry.getKey(), entry.getValue().toString());
        }

        for (Map.Entry<String, ?> entry : portfolioList.entrySet()) {
            tickerSet.append(entry.getKey()).append(",");
            numShares.put(entry.getKey(), entry.getValue().toString());
        }

        String getpriceurl = "http://nodejsapp-env.eba-9tz487ps.us-east-1.elasticbeanstalk.com/price?arg1="+tickerSet;
        StringRequest request = new StringRequest(Request.Method.GET, getpriceurl, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onResponse(String response) {
                JsonArray priceJson = JsonParser.parseString(response).getAsJsonArray();

                for(JsonElement jsonElement: priceJson){
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    double prevClose = jsonObject.get("prevClose").getAsDouble();
                    double last = jsonObject.get("last").getAsDouble();
                    double change = last - prevClose;
                    changeInPrice.put(jsonObject.get("ticker").getAsString(), change);
                    lastPrice.put(jsonObject.get("ticker").getAsString(), last);

                }

                updateViews();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        queue.add(request);
    }

    public void updateViews(){
        // Update date
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM dd, yyyy");
        Calendar calendar = Calendar.getInstance();
        String date = simpleDateFormat.format(calendar.getTime());
        TextView dateView = findViewById(R.id.date);
        dateView.setText(date);

        // Add portfolio items
        portfolioItemList = new ArrayList<>();

        // Calculate current net worth
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String cash = sharedPreferences.getString(CASH, null);
        double dnetWorth = 0;
        try {
            dnetWorth = Double.parseDouble(cash);
        } catch (Exception e){
            dnetWorth = 0;
        }

        for (Map.Entry <String, ?> entry: portfolioList.entrySet()){
            double price = lastPrice.get(entry.getKey());
            double shareAmount = Double.parseDouble(numShares.get(entry.getKey()));
            dnetWorth += price * shareAmount;
        }
        // Add a dummy section for net worth value
        portfolioItemList.add(new StockItem(null, null, null, null, null, String.format("%.2f", dnetWorth)));

        // Add portfolio items
        for (Map.Entry <String, ?> entry: portfolioList.entrySet()){
            String ticker = entry.getKey();
            portfolioItemList.add(new StockItem(ticker,
                    String.format("%.2f",lastPrice.get(ticker)),
                    companyName.get(ticker),
                    changeInPrice.get(ticker),
                    numShares.get(ticker),
                    null));
        }

        // Add favorite items
        favoriteItemList = new ArrayList<>();
        for (Map.Entry <String, ?> entry: favoriteList.entrySet()){
            String ticker = entry.getKey();
            favoriteItemList.add(new StockItem(ticker,
                    String.format("%.2f",lastPrice.get(ticker)),
                    companyName.get(ticker),
                    changeInPrice.get(ticker),
                    numShares.get(ticker),
                    null));
        }

        updateRecyclerView();

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP|ItemTouchHelper.DOWN, ItemTouchHelper.LEFT) {

            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                final int dragFlags = ItemTouchHelper.UP| ItemTouchHelper.DOWN;
                final int swipeFlags = ItemTouchHelper.LEFT;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();

//                viewHolder.itemView.setBackgroundColor(getResources().getColor(R.color.deleteRed));

                if (fromPosition>1 && fromPosition < 1 + portfolioItemList.size() && toPosition>1 && toPosition < 1 + portfolioItemList.size()){
                    // Both start position and end position are in portfolio section
                    Collections.swap(portfolioItemList, fromPosition-1, toPosition-1);
                    //recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
                    //sectionedAdapter.notifyItemMoved(fromPosition, toPosition);
                    //sectionedAdapter.notifyItemMoved(fromPosition, toPosition);
                }else if (fromPosition >= 2 + portfolioItemList.size()&&
                        toPosition >= 2 + portfolioItemList.size()){
                    // Both start position and end position are in favorites section

                    Collections.swap(favoriteItemList, fromPosition - 2 - portfolioItemList.size(), toPosition - 2 - portfolioItemList.size());
                    //sectionedAdapter.notifyItemMoved(fromPosition, toPosition);

                }

                updateRecyclerView();

                return true;
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                int truePosition = position - 2 - portfolioItemList.size();
                if (position >= 2 + portfolioItemList.size()){
                    // If item is within the favorite list

                    // Update sharedPreference
                    SharedPreferences sharedPreferences = getSharedPreferences(FAVORITE_LIST, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove(favoriteItemList.get(truePosition).getTicker());
                    editor.apply();

                    // Update recycler view
                    favoriteItemList.remove(truePosition);

                    updateRecyclerView();

                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.deleteRed))
                        .addActionIcon(R.drawable.ic_baseline_delete_24)
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

            @Override
            public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
                if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                    viewHolder.itemView.setBackgroundColor(Color.parseColor("#777777"));

                }
                super.onSelectedChanged(viewHolder, actionState);
            }
        };


        ItemTouchHelper itemTouchHelper= new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

//            ItemTouchHelper.Callback callback = new ItemMoveCallback();
//            ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
//            touchHelper.attachToRecyclerView(recyclerView);

        // Remove progress bar
        findViewById(R.id.progressbar).setVisibility(View.GONE);
    }

    public void updateRecyclerView(){
        portfolioSection = new StockSection("PORTFOLIO", portfolioItemList, this);
        portfolioSection.setOnItemClickListener(new StockSection.OnItemClickListener() {
            @Override
            public void onGoTo(int position) {
                String query = portfolioItemList.get(position-1).getTicker();
                Intent intent = new Intent(MainActivity.this, Detail.class);
                String message = query;
                intent.putExtra(EXTRA_MESSAGE, message);
                startActivity(intent);
            }
        });

        favoriteSection = new StockSection("FAVORITES", favoriteItemList, this);
        favoriteSection.setOnItemClickListener(new StockSection.OnItemClickListener() {
            @Override
            public void onGoTo(int position) {
                String query = favoriteItemList.get(position-portfolioItemList.size()-2).getTicker();
                Intent intent = new Intent(MainActivity.this, Detail.class);
                String message = query;
                intent.putExtra(EXTRA_MESSAGE, message);
                startActivity(intent);
            }
        });

        sectionedAdapter = new SectionedRecyclerViewAdapter();
        sectionedAdapter.addSection(portfolioSection);
        sectionedAdapter.addSection(favoriteSection);

        recyclerView = findViewById(R.id.home_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(sectionedAdapter);

    }

    public void toTiingo(View v){
        String url = String.format("https://www.tiingo.com/");
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    public void resetCash(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String cash = sharedPreferences.getString(CASH, null);
        if (cash == null){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(CASH, "20000");
            editor.apply();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        findViewById(R.id.progressbar).setVisibility(View.VISIBLE);
        getData();
    }

}



