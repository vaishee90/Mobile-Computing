package com.example.bhuva.store_buddy_group_31;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.*;
import com.google.android.gms.location.places.*;
import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static com.example.bhuva.store_buddy_group_31.MainActivity.db;

public class HomePage extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    final HomePage getProducts = this;
    public final String TAG = "LoadProducts";
    Button create_list, view_fav, view_recent_list, google_search;
    ListView suggestion_list;
    static String table_name = "product_catalog";
    Cursor cursor_fav, cursor_interval, cursor_date, cursor_check, cursor_recent;
    ArrayList<String> suggestions;
    LocalDate current_date, new_date;
    ArrayAdapter adapter;
    TextView suggestion_text;
    GoogleApiClient mGoogleApiClient;
    GoogleApiClient newGoogleApiClient;
    LocationRequest mLocationRequest;
    static String Lat = "";
    static String Long ="";
    String cSql;
    int days;
    Intent createlist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        suggestion_text = (TextView) findViewById(R.id.suggestion_header);
        suggestion_text.setVisibility(View.INVISIBLE);
        String input = "";
        StringBuilder rs = new StringBuilder();
        APIRequest request = new APIRequest(getProducts, input,rs);
        request.execute();

        cSql = "item_name text, quantity int";
        MainActivity.db.execSQL("create table if not exists fav_table (item_name text);");
        db.execSQL("create table if not exists " + MainActivity.recent_list_table_name + " (" + cSql + ");");
        System.out.println("Creating recent_list table...");
        suggestion_list = (ListView) findViewById(R.id.suggestion_list);
        suggestions = new ArrayList<String>();
        cursor_check = db.rawQuery("select count(*) from " + Favorites.table_name, null);
        cursor_check.moveToFirst();

        // suggestions
        if(cursor_check.getInt(0) > 0)
        {
            cursor_fav = db.rawQuery("select * from " + Favorites.table_name, null);
            if (cursor_fav.getCount() > 0)
            {
                current_date = new LocalDate();
                System.out.println(current_date);
                cursor_fav.moveToFirst();
                do {
                    cursor_interval = db.rawQuery("select interval from " + CreateYourList.history_table_name + " where item_name like '" + cursor_fav.getString(cursor_fav.getColumnIndex("item_name")) + "'", null);
                    cursor_date = db.rawQuery("select date_of_purchase from " + CreateYourList.history_table_name + " where item_name like '" + cursor_fav.getString(cursor_fav.getColumnIndex("item_name")) + "'", null);
                    if(cursor_interval.getCount() > 0 && cursor_date.getCount() > 0)
                    {
                        cursor_interval.moveToFirst();
                        cursor_date.moveToFirst();
                        days = cursor_interval.getInt(cursor_interval.getColumnIndex("interval"));
                        System.out.println(days);
                        new_date = (new LocalDate(cursor_date.getString(cursor_date.getColumnIndex("date_of_purchase")))).plusDays(days);
                        System.out.println(new_date);
                        if(new_date.equals(current_date))
                        {
                            suggestions.add(cursor_fav.getString(cursor_fav.getColumnIndex("item_name")));
                        }
                    }
                } while (cursor_fav.moveToNext());
                cursor_fav.close();
                //suggestions.add("banana");
                //suggestions.add("yogurt");
            }
            if(!suggestions.isEmpty())
            {
                suggestion_text.setVisibility(View.VISIBLE);
                adapter = new ArrayAdapter(this, R.layout.listitem_layout_commodityrecentlist, suggestions);
                suggestion_list.setAdapter(adapter);
            }
        }

        //Location Api
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addApi(Places.PLACE_DETECTION_API)
                    .addOnConnectionFailedListener(this) //Required Interface 'OnConnectionFailedListener' to be implemented
                    .addApi(LocationServices.API)
                    .build();
        }


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {


            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.
                // PERMISSION_REQUEST_ACCESS_FINE_LOCATION can be any unique int
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 3);
            }
        }

        Button btn_create_list = (Button) findViewById(R.id.create_list);

        //create_list = (Button) findViewById(R.id.create_list);
        view_recent_list = (Button) findViewById(R.id.view_recent);
        view_fav = (Button) findViewById(R.id.fav_view);
        google_search = (Button) findViewById(R.id.google_search);

        //suggestion_list = (ListView) findViewById(R.id.suggestion_list);
//        suggestion_list.setVisibility(View.INVISIBLE);

        btn_create_list.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                db.execSQL("drop table if exists "+MainActivity.recent_list_table_name+";");
                createlist = new Intent(HomePage.this, CreateYourList.class);
                startActivity(createlist);
            }
        });

        view_recent_list.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                cursor_recent = db.rawQuery("select * from " + MainActivity.recent_list_table_name, null);
                cursor_recent.moveToFirst();
                if(cursor_check.getCount() < 1)
                {
                    Toast.makeText(getApplicationContext(),"Please create a list first!",Toast.LENGTH_SHORT).show();
                }
                else
                    startActivity(new Intent(HomePage.this, Recent_List.class));
            }
        });

        view_fav.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startActivity(new Intent(HomePage.this, Favorites.class));
            }
        });

        google_search.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startActivity(new Intent(HomePage.this, GoogleSearchIntentActivity.class));
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 3: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    try {
                        PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi.getCurrentPlace(mGoogleApiClient, null);
                        result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
                            @Override
                            public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
                                for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                                    Log.i(TAG, String.format("Place '%s' has likelihood: %g",
                                            placeLikelihood.getPlace().getName(),
                                            placeLikelihood.getLikelihood()));
                                    System.out.println(placeLikelihood.getPlace().getName());
                                    Toast.makeText(getApplicationContext(),placeLikelihood.getPlace().getName(),Toast.LENGTH_SHORT).show();
                                }
                                likelyPlaces.release();
                            }
                        });
                    } catch (SecurityException e) {
                        Toast.makeText(getApplicationContext(),"error getting places",Toast.LENGTH_SHORT).show();
                    }
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    protected Context getContext() {
        return getApplicationContext();
    }

    protected void loadcatalog(String searchQuery, String json) {
        String createSql = "id text,item_name text,price text,store text";

        MainActivity.db.execSQL("create table if not exists " + MainActivity.product_catalog_table + " (" + createSql + ");");

        System.out.println("Creating table...");



        if (json != null) {
           // Intent intent = new Intent(HomePage.this, LoadProducts.class);

            try {
                JSONObject jsonObject = new JSONObject(json);



                JSONArray jsonArray = jsonObject.getJSONArray("items");

                // parse jsonArray to get the events, then put them in a object of their own
                for (int obj = 0; obj < jsonArray.length(); obj++) {

                    JSONObject eventObject = jsonArray.getJSONObject(obj);
                    String prodName = "";
                    String id = "";
                    String price = "";

                    try {
                        prodName = eventObject.getString("name");

                        id = eventObject.getString("itemId");
                        price = eventObject.getString("salePrice");


                    } catch (JSONException e) {
                        Log.i(TAG, "Issue getting data from Store api");
                    }

                    MainActivity.db.execSQL("insert into " + MainActivity.product_catalog_table  + " values ('" + id + "','" + prodName + "','" +price + "','walmart')");
                    MainActivity.db.execSQL("insert into " + MainActivity.product_catalog_table  + " values ('" + id + "','" + prodName + "','" +(Double.parseDouble(price)*0.5) + "','target')");


                }

                MainActivity.db.execSQL("insert into " + MainActivity.product_catalog_table  + " values ('1000','plain yogurt','0.5','walmart')");
                MainActivity.db.execSQL("insert into " + MainActivity.product_catalog_table  + " values ('1000','plain yogurt','1.0','target')");
                MainActivity.db.execSQL("insert into " + MainActivity.product_catalog_table  + " values ('1000','banana','0.4','walmart')");
                MainActivity.db.execSQL("insert into " + MainActivity.product_catalog_table  + " values ('1000','banana','1.4','target')");

            } catch (JSONException e) {
                e.printStackTrace();
            }




            //LoadProducts.json = json;

           // startActivity(intent);
        }




    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    @Override
    public void onConnected(Bundle connectionHint) { // requires Interface 'OnConnectionCallbacks'
        try {
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                Lat = String.valueOf(mLastLocation.getLatitude());
                Long = String.valueOf(mLastLocation.getLongitude());
            }
        }
        catch (SecurityException e)
        {
            Toast.makeText(this,"Cannot retrive location", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onConnectionSuspended(int x)
    {

    }

    @Override
    public void onConnectionFailed(ConnectionResult x)
    {

    }

}
