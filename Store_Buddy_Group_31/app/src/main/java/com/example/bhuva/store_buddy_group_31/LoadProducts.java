package com.example.bhuva.store_buddy_group_31;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LoadProducts extends AppCompatActivity {

    public final String TAG = "LoadProducts";
    public static String json;

    TextView textView;
    ListView listView;

    ArrayAdapter arrayAdapter;
    ArrayList<String> eventList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_products);

        Bundle bundle = getIntent().getExtras();
        String query = bundle.getString("query");
        // TODO: get json String from bundle here

        textView = (TextView) findViewById(R.id.textView2);
        textView.setText(query);

        listView = (ListView) findViewById(R.id.mylistview);

        createEventList(json);
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.listitem_layout, eventList);
        listView.setAdapter(adapter);


    }

    private void createEventList(String jsonString) {
        eventList = new ArrayList<String>();

        try {
            JSONObject jsonObject = new JSONObject(jsonString);



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
                        Log.i(TAG, "Issue getting data from eventObject");
                    }

                    eventList.add(prodName +"\n" + id +"\n" + price);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
