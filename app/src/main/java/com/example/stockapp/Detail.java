package com.example.stockapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import org.w3c.dom.Text;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class Detail extends AppCompatActivity {

//    String ticker;
//    String name;
//    String currentprice;
//    String change;
//    String portfolio_message;
//    public int pendingrequests = 4;
//    RequestQueue queue;
    private Menu mOptionsMenu;

    private String TICKER;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String FAVORITE_LIST="favoriteList";
    public static final String PORTFOLIO_LIST = "portfolioList";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.myToolBar);
        myToolbar.setTitleTextAppearance(this, R.style.BoldTextAppearance);
        myToolbar.setTitleTextColor(Color.BLACK);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get search quote
        Intent intent = getIntent();
        String quote = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        TICKER = quote;

        TextView textView = findViewById(R.id.textView);
        textView.setText(quote);

        findViewById(R.id.alldetails).setVisibility(View.GONE);
        findViewById(R.id.progressbar).setVisibility(View.VISIBLE);

        getData(quote);
        // changes for alarm receiver
//        AlarmReceiver.scheduleAlarms(this);

//        LocalBroadcastManager.getInstance(this).registerReceiver(
//                new BroadcastReceiver() {
//                    @Override
//                    public void onReceive(Context context, Intent intent) {
//                        //String quote1 = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
//                        //getPriceData(quote);
//                        System.out.println("MyReceiver: here!");
//                    }
//                }, new IntentFilter(DetailUpdateService.ACTIVITY_SERVICE)
//        );

        final Handler handler = new Handler();
        final int delay = 3600000; // 1000 milliseconds == 1 second

        handler.postDelayed(new Runnable() {
            public void run() {
                //System.out.println("myHandler: here!");
                getPriceData(quote);

                handler.postDelayed(this, delay);
            }
        }, delay);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the search menu action bar
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        mOptionsMenu = menu;

        MenuItem star_border = menu.findItem(R.id.action_favorite);
        star_border.setVisible(false);

        MenuItem star = menu.findItem(R.id.action_unfavorite);
        star.setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_favorite:
                mOptionsMenu.findItem(R.id.action_favorite).setVisible(false);
                mOptionsMenu.findItem(R.id.action_unfavorite).setVisible(true);
                addFavorite(TICKER);

                return true;

            case R.id.action_unfavorite:
                mOptionsMenu.findItem(R.id.action_favorite).setVisible(true);
                mOptionsMenu.findItem(R.id.action_unfavorite).setVisible(false);
                removeFavorite(TICKER);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void getPriceData (String quote) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String ticker = quote.split("-")[0];
        String getpriceurl = "http://nodejsapp-env.eba-9tz487ps.us-east-1.elasticbeanstalk.com/price?arg1="+ticker;

        final String[] pricedata = new String[1];
        StringRequest request1 = new StringRequest(Request.Method.GET, getpriceurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                pricedata[0] = response;
                updatePrices(pricedata);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        queue.add(request1);
    }

    private void getData(String quote){
        RequestQueue queue = Volley.newRequestQueue(this);
        String ticker = quote.split("-")[0];

        String getpriceurl = "http://nodejsapp-env.eba-9tz487ps.us-east-1.elasticbeanstalk.com/price?arg1="+ticker;
        String getcompanyurl = "http://nodejsapp-env.eba-9tz487ps.us-east-1.elasticbeanstalk.com/company?arg1="+ticker;
        String getcharturl = "http://nodejsapp-env.eba-9tz487ps.us-east-1.elasticbeanstalk.com/year?arg1="+ticker;
        String getnewsurl = "http://nodejsapp-env.eba-9tz487ps.us-east-1.elasticbeanstalk.com/news?arg1="+ticker;

        final String[] pricedata = new String[1];
        final String[] companydata = new String[1];
        final String[] chartdata = new String[1];
        final String[] newsdata = new String[1];

        final int[] pendingrequests = {4};

        StringRequest request1 = new StringRequest(Request.Method.GET, getpriceurl, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onResponse(String response) {

                pricedata[0] = response;

                pendingrequests[0]--;
                if (pendingrequests[0] == 0){
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
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onResponse(String response) {

                companydata[0] = response;
                pendingrequests[0]--;
                if (pendingrequests[0] == 0){
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
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onResponse(String response) {

                chartdata[0] = response;
                pendingrequests[0]--;
                if (pendingrequests[0] == 0){
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
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onResponse(String response) {

                newsdata[0] = response;
                pendingrequests[0]--;
                if (pendingrequests[0] == 0){
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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

        }else {

            DecimalFormat df2 = new DecimalFormat("#.##");

            companyjson = JsonParser.parseString(companydata[0]).getAsJsonObject();
            chartjson = JsonParser.parseString(chartdata[0]).getAsJsonArray();
            newsjson = JsonParser.parseString(newsdata[0]).getAsJsonObject();

            TextView ticker = findViewById(R.id.ticker);
            ticker.setText(companyjson.get("ticker").getAsString());

            TextView name = findViewById(R.id.name);
            name.setText(companyjson.get("name").getAsString());


            updatePrices(pricedata);

            // Highcharts section
            WebView webview = (WebView) findViewById(R.id.higchcharts);
            webview.getSettings().setJavaScriptEnabled(true);
            //webview.loadUrl("file:///android_asset/highcharts.html?ticker="+companyjson.get("ticker").getAsString());
            webview.loadUrl("file:///android_asset/highcharts.html?ticker=" + chartdata[0] + "&symbol=" + companyjson.get("ticker").getAsString());

            // About section
            TextView aboutless = findViewById(R.id.aboutless);
            TextView aboutmore = findViewById(R.id.aboutmore);

            aboutless.setText(companyjson.get("description").getAsString());
            aboutmore.setText(companyjson.get("description").getAsString());
            aboutmore.setVisibility(View.GONE);
            findViewById(R.id.showless).setVisibility(View.GONE);

            // News section
            JsonArray articles = newsjson.get("articles").getAsJsonArray();

            if (articles.size() > 0){
                // First news card
                if (!articles.get(0).getAsJsonObject().get("urlToImage").isJsonNull()){
                    ImageView firstImage = findViewById(R.id.firstnewspicture);
                    Glide.with(firstImage).load(articles.get(0).getAsJsonObject().get("urlToImage").getAsString()).into(firstImage);
                    firstImage.setClipToOutline(true);
                }

                TextView firstSource = findViewById(R.id.firstnewssource);
                firstSource.setText(articles.get(0).getAsJsonObject().get("source").getAsJsonObject().get("name").getAsString());
                TextView firstStamp = findViewById(R.id.firstnewstimestamp);
                String newsStamp = articles.get(0).getAsJsonObject().get("publishedAt").getAsString();
                firstStamp.setText(getTimeGap(newsStamp));
                TextView firstHeader = findViewById(R.id.firstnewsheader);
                firstHeader.setText(articles.get(0).getAsJsonObject().get("title").getAsString());

                CardView firstcard = findViewById(R.id.firstcard);
                firstcard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(articles.get(0).getAsJsonObject().get("url").getAsString()));
                        startActivity(browserIntent);
                    }
                });

                final Context context = this;

                firstcard.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        newsDialog(articles.get(0).getAsJsonObject());

                        return true;
                    }
                });
                // Remaining News cards
                if(articles.size() > 1){
                    ArrayList<NewsCardItem> newsCardList = new ArrayList<>();

                    for(int i=1; i < articles.size(); i++){
                        if (!articles.get(i).getAsJsonObject().get("urlToImage").isJsonNull()){
                            newsCardList.add(new NewsCardItem(articles.get(i).getAsJsonObject().get("urlToImage").getAsString(),
                                    articles.get(i).getAsJsonObject().get("source").getAsJsonObject().get("name").getAsString(),
                                    getTimeGap(articles.get(i).getAsJsonObject().get("publishedAt").getAsString()),
                                    articles.get(i).getAsJsonObject().get("title").getAsString()
                            ));
                        }else{
                            newsCardList.add(new NewsCardItem("null",
                                    articles.get(i).getAsJsonObject().get("source").getAsJsonObject().get("name").getAsString(),
                                    getTimeGap(articles.get(i).getAsJsonObject().get("publishedAt").getAsString()),
                                    articles.get(i).getAsJsonObject().get("title").getAsString()
                            ));
                        }

                    }


                    RecyclerView mRecyclerView = findViewById(R.id.newsrecyclerview);
                    mRecyclerView.setHasFixedSize(true);
                    NewsCardAdapter mAdapter = new NewsCardAdapter(newsCardList);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);

                    mRecyclerView.setLayoutManager(mLayoutManager);
                    mRecyclerView.setAdapter(mAdapter);

                    mAdapter.setOnItemClickListener(new NewsCardAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(articles.get(position+1).getAsJsonObject().get("url").getAsString()));
                            startActivity(browserIntent);
                        }
                    });

                    mAdapter.setOnItemLongClickListener(new NewsCardAdapter.OnItemLongClickListener() {
                        @Override
                        public void onItemLongClick(int position) {
                            newsDialog(articles.get(position+1).getAsJsonObject());

                        }
                    });

                }else{
                    findViewById(R.id.newsrecyclerview).setVisibility(View.GONE);
                }
            }
            else{
                findViewById(R.id.firstcard).setVisibility(View.GONE);
                findViewById(R.id.newsrecyclerview).setVisibility(View.GONE);

            }

            //Remove progress bar
            findViewById(R.id.alldetails).setVisibility(View.VISIBLE);
            findViewById(R.id.progressbar).setVisibility(View.GONE);
            findViewById(R.id.errormessage).setVisibility(View.GONE);

            //Display star icon
            displayIcon();


        }


    }

    private void updatePrices (String[] priceData) {
        JsonArray priceJson;

        priceJson = JsonParser.parseString(priceData[0]).getAsJsonArray();

        if (priceJson.size() != 0) {
            DecimalFormat decimalFormat = new DecimalFormat("0.00");

            TextView priceView = findViewById(R.id.price);
            Resources resources = getResources();
            String priceText = String.format(resources.getString(R.string.priceString), priceJson.get(0).getAsJsonObject().get("last").getAsString());
            priceView.setText(priceText);

            TextView changeView = findViewById(R.id.change);
            double changeValueDouble = priceJson.get(0).getAsJsonObject().get("last").getAsDouble() - priceJson.get(0).getAsJsonObject().get("prevClose").getAsDouble();
            String changeText = String.format(resources.getString(R.string.priceString), decimalFormat.format(changeValueDouble));
            changeView.setText(changeText);

            if (changeValueDouble < 0) {
                changeView.setTextColor(resources.getColor(R.color.downTrend));
            } else if (changeValueDouble > 0) {
                changeView.setTextColor(resources.getColor(R.color.upTrend));
            } else {
                changeView.setTextColor(Color.BLACK);
            }
            TextView currentPriceView = findViewById(R.id.currentPrice);
            TextView lowView = findViewById(R.id.low);
            TextView bidPriceView = findViewById(R.id.bidPrice);
            TextView openPriceView = findViewById(R.id.openPrice);
            TextView midView = findViewById(R.id.mid);
            TextView highView = findViewById(R.id.high);
            TextView volumeView = findViewById(R.id.volume);

            String currentPriceText = String.format(resources.getString(R.string.currentPriceString), priceJson.get(0).getAsJsonObject().get("last").getAsString());
            String lowText = String.format(resources.getString(R.string.lowString), priceJson.get(0).getAsJsonObject().get("low").getAsString());
            String bidPriceText;
            if (priceJson.get(0).getAsJsonObject().get("bidPrice").isJsonNull()) {
                bidPriceText = String.format(resources.getString(R.string.bidPriceString), "0.0");
            } else {
                bidPriceText = String.format(resources.getString(R.string.bidPriceString), priceJson.get(0).getAsJsonObject().get("bidPrice").getAsString());
            }

            String openPriceText = String.format(resources.getString(R.string.openPriceString), priceJson.get(0).getAsJsonObject().get("open").getAsString());

            String midText;
            if (priceJson.get(0).getAsJsonObject().get("mid").isJsonNull()) {
                midText = String.format(resources.getString(R.string.midString), "0.0");
            } else {
                midText = String.format(resources.getString(R.string.midString), priceJson.get(0).getAsJsonObject().get("mid").getAsString());
            }
            String highText = String.format(resources.getString(R.string.highString), priceJson.get(0).getAsJsonObject().get("high").getAsString());
            String volumeText = String.format(resources.getString(R.string.volumeString), priceJson.get(0).getAsJsonObject().get("volume").getAsString());

            currentPriceView.setText(currentPriceText);
            lowView.setText(lowText);
            bidPriceView.setText(bidPriceText);
            openPriceView.setText(openPriceText);
            midView.setText(midText);
            highView.setText(highText);
            volumeView.setText(volumeText);



        }
    }

    public void showmore (View v){
        findViewById(R.id.aboutmore).setVisibility(View.VISIBLE);
        findViewById(R.id.showmore).setVisibility(View.GONE);
        findViewById(R.id.aboutless).setVisibility(View.GONE);
        findViewById(R.id.showless).setVisibility(View.VISIBLE);
    }
    public void showless (View v){
        findViewById(R.id.aboutmore).setVisibility(View.GONE);
        findViewById(R.id.showmore).setVisibility(View.VISIBLE);
        findViewById(R.id.aboutless).setVisibility(View.VISIBLE);
        findViewById(R.id.showless).setVisibility(View.GONE);
    }


    public String getTimeGap (String newsStamp){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        try{
            long t1 = sdf.parse(newsStamp).getTime();
            long t2 = timestamp.getTime();
            long diff = t2 - t1;

            long diff_In_minutes = Math.round(diff / (1000*60));
            long diff_In_days = Math.round(diff/(1000*60*60*24));

            if(diff_In_days>0){
                return String.format("%d days ago", diff_In_days);
            }else{
                return String.format("%d minutes ago", diff_In_minutes);
            }

        }
        catch (ParseException e) {
            e.printStackTrace();
            return "Parsing error";
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void newsDialog(JsonObject newsItem){
        //Set dialog contents
        Dialog firstNewsDialog = new Dialog(Detail.this);
        firstNewsDialog.setContentView(R.layout.news_dialog);

        if (!newsItem.get("urlToImage").isJsonNull()){
            ImageView dialog_pic_first = firstNewsDialog.findViewById(R.id.dialog_pic_first);
            Glide.with(dialog_pic_first).load(newsItem.get("urlToImage").getAsString()).into(dialog_pic_first);
            dialog_pic_first.setClipToOutline(true);
        }

        TextView dialog_title = firstNewsDialog.findViewById(R.id.dialog_title);
        dialog_title.setText(newsItem.get("title").getAsString());

        ImageView twitter = firstNewsDialog.findViewById(R.id.twitter);
        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = String.format("https://twitter.com/intent/tweet?url=%s", newsItem.get("url").getAsString());
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        });

        ImageView chrome = firstNewsDialog.findViewById(R.id.chrome);
        chrome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(newsItem.get("url").getAsString()));
                startActivity(browserIntent);
            }
        });
        firstNewsDialog.show();

    }

    public void addFavorite(String ticker){



        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        Set<String> set = sharedPreferences.getStringSet(FAVORITE_LIST, null);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (set == null){
            set = new HashSet<String>();
            set.add(ticker);
        }else{
            set.add(ticker);
        }
        editor.putStringSet(FAVORITE_LIST, set);
        editor.apply();
    }

    public void removeFavorite(String ticker){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> set = sharedPreferences.getStringSet(FAVORITE_LIST, null);
        set.remove(ticker);
        editor.putStringSet(FAVORITE_LIST, set);
        editor.apply();
    }

    public void displayIcon(){
        MenuItem star_border = mOptionsMenu.findItem(R.id.action_favorite);
        MenuItem star = mOptionsMenu.findItem(R.id.action_unfavorite);

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        Set<String> set = sharedPreferences.getStringSet(FAVORITE_LIST, null);
        if(set == null){
            star_border.setVisible(true);
            star.setVisible(false);
        }else if (set.contains(TICKER)){
            star_border.setVisible(false);
            star.setVisible(true);
        }else{
            star_border.setVisible(true);
            star.setVisible(false);
        }

    }


}