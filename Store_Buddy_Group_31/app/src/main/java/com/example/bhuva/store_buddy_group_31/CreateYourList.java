package com.example.bhuva.store_buddy_group_31;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.IntegerRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.example.bhuva.store_buddy_group_31.MainActivity.db;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class CreateYourList extends AppCompatActivity {

    final CreateYourList createlist=this;
    AutoCompleteTextView editTextProduct;
    static String history_table_name = "history_table";
    EditText editTextQty;
    Button btn_add, home;
    ListView listView;
    String cSql;
    ArrayList<String> prodList;
   Cursor cursor, cursorProdName, cursor_history, cursor_price, cursor_store, cursor_date, cursor_order, cursor_fav;
    ArrayList<String> prodNameList, places;
    //DateTimeFormatter dateFormat;
    LocalDate old_date;
    int order_count, days;
    Intent navigate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_your_list);


        editTextProduct = (AutoCompleteTextView) findViewById(R.id.editTextProduct);
        editTextQty = (EditText) findViewById(R.id.editTextQty);
        btn_add = (Button) findViewById(R.id.btn_add);
        home = (Button) findViewById(R.id.home_button);
        listView = (ListView) findViewById(R.id.commoditylistview);
        Button btn_find_store = (Button) findViewById(R.id.btn_findstore);

        prodNameList = new ArrayList<String>();
        cursorProdName = db.rawQuery("select * from "+ MainActivity.product_catalog_table +";", null);
        System.out.println(cursorProdName.toString());

        if (cursorProdName.getCount() > 0)
        {
            cursorProdName.moveToFirst();
            do {
                prodNameList.add(cursorProdName.getString(cursorProdName.getColumnIndex("item_name")));

            } while (cursorProdName.moveToNext());
            cursorProdName.close();
        }

        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,prodNameList);

        editTextProduct.setAdapter(adapter);
        editTextProduct.setThreshold(1);
        Button compare = (Button) findViewById(R.id.compare);


        String input = "";
        StringBuilder rs = new StringBuilder();
        PlacesRequest request1 = new PlacesRequest(createlist, input, rs);
        request1.execute();


        btn_find_store.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {


                navigate = new Intent(CreateYourList.this, Navigate.class);
                String output = places.get(0);
                navigate.putExtra("address",output.split("---")[0]);
                navigate.putExtra("lat",output.split("---")[1]);
                navigate.putExtra("lng",output.split("---")[2]);
                startActivity(navigate);


            }  });

        btn_add.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                EditText txtProduct = (EditText) findViewById(R.id.editTextProduct);
                EditText txtQty = (EditText) findViewById(R.id.editTextQty);
                //dateFormat = new DateTimeFormat();



                if (txtProduct.getText() == null || txtQty.getText() == null) {
                    Toast.makeText(getApplicationContext(), "Please enter all the details", Toast.LENGTH_SHORT).show();


                }

                else {

                    boolean Available = FALSE;

                    for(String prod: prodNameList)
                    {
                        if(txtProduct.getText().toString().trim().equals(prod))
                        {
                            Available = TRUE;
                        }
                    }

                    if(Available == TRUE) {

                        cSql = "item_name text, quantity int";
                        //create complete history table
                        db.execSQL("create table if not exists " + history_table_name + " (item_name text, quantity int, price text, date_of_purchase text, store text, interval int, order_count int)");
                        System.out.println("Creating history table...");
                        db.execSQL("create table if not exists " + MainActivity.recent_list_table_name + " (" + cSql + ");");
                        System.out.println("Creating recent_list table...");
                        db.execSQL("insert into " + MainActivity.recent_list_table_name + " values ('" + txtProduct.getText().toString() + "'," + Integer.parseInt(txtQty.getText().toString()) + ");");
                        System.out.println("Added to recent_list table...");
                        Toast.makeText(getApplicationContext(), "Product Added to Recent List", Toast.LENGTH_SHORT).show();

                        cursor_history = db.rawQuery("select * from " + history_table_name + ";", null);
                        cursor_price = db.rawQuery("select price from " + MainActivity.product_catalog_table + " where item_name like '" + txtProduct.getText().toString() + "'", null);
                        cursor_store = db.rawQuery("select store from " + MainActivity.product_catalog_table + " where item_name like '" + txtProduct.getText().toString() + "'", null);

                        if (cursor_history.getCount() < 1) {
                            if (cursor_price.getCount() > 0 && cursor_store.getCount() > 0) {
                                cursor_price.moveToFirst();
                                cursor_store.moveToFirst();
                                db.execSQL("insert into " + history_table_name + " values ('" + txtProduct.getText().toString() + "'," + Integer.parseInt(txtQty.getText().toString()) + ",'" + cursor_price.getString(cursor_price.getColumnIndex("price")) + "','" + new LocalDate().toString() + "','" + cursor_store.getString(cursor_store.getColumnIndex("store")) + "'," + "0," + "1)");
                            }
                        }
                        else
                        {
                            cursor_date = db.rawQuery("select date_of_purchase from " + history_table_name + " where item_name like '" + txtProduct.getText().toString() + "'", null);
                            cursor_order = db.rawQuery("select order_count from " + history_table_name + " where item_name like '" + txtProduct.getText().toString() + "'", null);
                            if (cursor_date.getCount() > 0)
                            {
                                cursor_date.moveToFirst();
                                try
                                {
                                    old_date = new LocalDate(cursor_date.getString(cursor_date.getColumnIndex("date_of_purchase")));
                                    //LocalDate new_date = new LocalDate("2017-05-3");
                                    days = Days.daysBetween(old_date, new LocalDate()).getDays();
                                    //System.out.println("new date = " + new_date);
                                    //System.out.println("days = " + days);
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                }

                                if(cursor_order.getCount() > 0)
                                {
                                    cursor_order.moveToFirst();
                                    order_count = Integer.parseInt(cursor_order.getString(cursor_order.getColumnIndex("order_count"))) + 1;
                                    System.out.println("order count = " + order_count);

                                    db.execSQL("update " + history_table_name + " set order_count = " + order_count +  ", interval = " + days + ", date_of_purchase = '" + new LocalDate().toString() + "' where item_name = '" + txtProduct.getText().toString() + "'");

                                    if(order_count > 5)
                                    {
                                        cursor_fav = db.rawQuery("select * from " + Favorites.table_name + " where item_name like '" + txtProduct.getText().toString() + "'", null);

                                        if(cursor_fav.getCount() < 1)
                                        {
                                            db.execSQL("insert into " + Favorites.table_name + " values('" + txtProduct.getText().toString() + "')");
                                        }
                                    }
                                    //Toast.makeText(getApplicationContext(), "order count = " + order_count, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Product Not Available In Store", Toast.LENGTH_SHORT).show();

                    }
                    prodList = new ArrayList<String>();
                    cursor = db.rawQuery("select * from "+ MainActivity.recent_list_table_name +";", null);
                    System.out.println(cursor.toString());

                    if (cursor.getCount() > 0)
                    {
                        cursor.moveToFirst();
                        do {
                            prodList.add(cursor.getString(cursor.getColumnIndex("item_name")) +"        " + cursor.getInt(cursor.getColumnIndex("quantity")));

                        } while (cursor.moveToNext());
                        cursor.close();
                    }




                    ArrayAdapter adapter = new ArrayAdapter(CreateYourList.this, R.layout.listitem_layout_commodityrecentlist, prodList);
                    listView.setAdapter(adapter);

                }
            }
        });




        compare.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                int total_items = 0;
                Cursor c1,c2;


                c1 = db.rawQuery("select count(*) from " + MainActivity.recent_list_table_name,null);
                c1.moveToFirst();
                total_items = Integer.parseInt(c1.getString(0));
                System.out.println(total_items);

                String[] items = new String[total_items];
                int[] item_quantity = new int[total_items];
                int i = 0;
                c2 = db.rawQuery("select * from " + MainActivity.recent_list_table_name,null);
                c2.moveToFirst();
                do{
                    items[i] = c2.getString(c2.getColumnIndex("item_name"));
                    item_quantity[i] = c2.getInt(c2.getColumnIndex("quantity"));
                    i++;
                }while(c2.moveToNext());
                float price, total_price_walmart, total_price_target;
                total_price_walmart= Integer.MAX_VALUE;
                total_price_target=Integer.MAX_VALUE;
                Cursor c3,c4;
                for(int j=0;j<total_items;j++){
                    c3 = db.rawQuery("select price from product_catalog where item_name = '"+items[j]+"' and store = 'walmart'",null);
                   if(c3.getCount()>0) {
                       c3.moveToFirst();
                       price = c3.getFloat(0);
                       total_price_walmart += price;
                   }
                    c4 = db.rawQuery("select price from product_catalog where item_name = '"+items[j]+"' and store = 'target'",null);
                    if(c4.getCount()> 0) {
                        c4.moveToFirst();
                        price = c4.getFloat(0);
                        total_price_target += price;
                    }


                }

                if(total_price_walmart<total_price_target){

                    Toast.makeText(CreateYourList.this, "Walmart has better price - "+Float.toString(total_price_walmart), Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(CreateYourList.this, "Target has better price - "+Float.toString(total_price_target), Toast.LENGTH_SHORT).show();

                }




            }
        });


        home.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startActivity(new Intent(CreateYourList.this, HomePage.class));
            }
        });

    }

    protected void loadplaces(String searchQuery, String json) {


        if (json != null) {
            // Intent intent = new Intent(HomePage.this, LoadProducts.class);

            try {
                JSONObject jsonObject = new JSONObject(json);

                System.out.println(jsonObject);

                JSONArray jsonArray = jsonObject.getJSONArray("results");
                places = new ArrayList<String>();
                // parse jsonArray to get the events, then put them in a object of their own
                for (int obj = 0; obj < jsonArray.length(); obj++) {

                    JSONObject eventObject = jsonArray.getJSONObject(obj);
                    String addr ="";
                    String latitude="";
                    String longitude="";

                    try {
                        addr = eventObject.getString("formatted_address");
                        JSONObject geo = eventObject.getJSONObject("geometry");
                        JSONObject loc = geo.getJSONObject("location");
                        latitude = String.valueOf(loc.getDouble("lat"));
                        longitude = String.valueOf(loc.getDouble("lng"));

                        places.add(addr+"---"+latitude+"---"+longitude);

                    } catch (JSONException e) {
                        //  Log.i(TAG, "Issue getting data from Store api");
                    }


                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    protected Context getContext() {
        return getApplicationContext();
    }
}
