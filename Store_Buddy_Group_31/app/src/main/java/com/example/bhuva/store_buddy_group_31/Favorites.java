package com.example.bhuva.store_buddy_group_31;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.example.bhuva.store_buddy_group_31.MainActivity.db;

public class Favorites extends AppCompatActivity {
    static SQLiteDatabase db;
    static String table_name = "fav_table";

    AutoCompleteTextView search_Fav;
    Button add_fav, delete_fav;
    ListView fav_list;
    int item_pos;
    String item_name;
    ArrayList<String> prodList, prodNameList;
    ArrayAdapter adapter, adapter_prodcat;
    Cursor cursor, cursorProdName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorites);
        db = openOrCreateDatabase("CommoditiesDB.db", MODE_PRIVATE, null);
        add_fav = (Button) findViewById(R.id.add_fav);
        delete_fav = (Button) findViewById(R.id.del_fav);
        fav_list = (ListView) findViewById(R.id.fav_list);
        search_Fav = (AutoCompleteTextView) findViewById(R.id.autocomplete_favs);

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
        prodList = new ArrayList<String>();
        cursor = db.rawQuery("select * from "+ table_name +";", null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                prodList.add(cursor.getString(cursor.getColumnIndex("item_name")));

            } while (cursor.moveToNext());
            cursor.close();
        }

        adapter_prodcat = new ArrayAdapter(Favorites.this, R.layout.listitem_layout_commodityrecentlist, prodNameList);
        adapter = new ArrayAdapter(Favorites.this, R.layout.listitem_layout_commodityrecentlist, prodList);
        search_Fav.setAdapter(adapter_prodcat);
        search_Fav.setThreshold(1);

        fav_list.setAdapter(adapter);

        add_fav.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                // add item to table
                if(!search_Fav.getText().toString().equals(""))
                    db.execSQL("insert into " + table_name  + " values ('" + search_Fav.getText().toString() +"');");
                else
                    Toast.makeText(Favorites.this, "Please enter an item", Toast.LENGTH_SHORT).show();

                prodList = new ArrayList<String>();
                cursor = db.rawQuery("select * from "+ table_name +";", null);

                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    do {
                        prodList.add(cursor.getString(cursor.getColumnIndex("item_name")));

                    } while (cursor.moveToNext());
                    cursor.close();
                }

                adapter = new ArrayAdapter(Favorites.this, R.layout.listitem_layout_commodityrecentlist, prodList);
                fav_list.setAdapter(adapter);
                //System.out.println("Added...");
            }
        });

        delete_fav.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //db = openOrCreateDatabase("CommoditiesDB.db", MODE_PRIVATE, null);
                fav_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parentView, View childView, int position, long id) {
                        item_pos = position;
                    }
                });
                item_name = (fav_list.getItemAtPosition(item_pos)).toString();
                db.execSQL("delete from " + table_name + " where item_name = '" + item_name + "'");
                Toast.makeText(Favorites.this, "Item: " + item_name + " has been deleted", Toast.LENGTH_SHORT).show();

                prodList = new ArrayList<String>();
                cursor = db.rawQuery("select * from "+ table_name +";", null);

                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    do {
                        prodList.add(cursor.getString(cursor.getColumnIndex("item_name")));

                    } while (cursor.moveToNext());
                    cursor.close();
                }

                adapter = new ArrayAdapter(Favorites.this, R.layout.listitem_layout_commodityrecentlist, prodList);
                fav_list.setAdapter(adapter);
            }
        });
    }
}
