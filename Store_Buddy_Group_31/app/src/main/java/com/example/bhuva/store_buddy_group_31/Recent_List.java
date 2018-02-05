package com.example.bhuva.store_buddy_group_31;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class Recent_List extends AppCompatActivity {

    static SQLiteDatabase db;
    static String table_name = "recent_list_table";
    static int item_pos;
    static String item_name;
    Button delete_list;
    Button delete_item;
    Button add_item;
    ListView recent_list_view;
    ArrayList<String> prodList;
    ArrayAdapter adapter;
    Cursor cursor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recent_list);
        db = openOrCreateDatabase("CommoditiesDB.db", MODE_PRIVATE, null);
        add_item = (Button) findViewById(R.id.add_recent);
        delete_item = (Button) findViewById(R.id.delete_recent);
        delete_list = (Button) findViewById(R.id.delete_recent_list);

        recent_list_view = (ListView) findViewById(R.id.recent_listview);

        prodList = new ArrayList<String>();
        cursor = db.rawQuery("select * from "+ table_name +";", null);
        //System.out.println(cursor.toString());

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                prodList.add(cursor.getString(cursor.getColumnIndex("item_name")) + "        " + cursor.getInt(cursor.getColumnIndex("quantity")));

            } while (cursor.moveToNext());
            cursor.close();
        }

        adapter = new ArrayAdapter(Recent_List.this, R.layout.listitem_layout_commodityrecentlist, prodList);
        recent_list_view.setAdapter(adapter);
        add_item.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startActivity(new Intent(Recent_List.this, CreateYourList.class));

                prodList = new ArrayList<String>();
                cursor = db.rawQuery("select * from "+ table_name +";", null);
                //System.out.println(cursor.toString());

                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    do {
                        prodList.add(cursor.getString(cursor.getColumnIndex("item_name")) + "        " + cursor.getInt(cursor.getColumnIndex("quantity")));

                    } while (cursor.moveToNext());
                    cursor.close();
                }

                adapter = new ArrayAdapter(Recent_List.this, R.layout.listitem_layout_commodityrecentlist, prodList);
                recent_list_view.setAdapter(adapter);
            }
        });

        delete_item.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                recent_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parentView, View childView, int position, long id) {
                        item_pos = position;
                    }
                });
                item_name = (recent_list_view.getItemAtPosition(item_pos)).toString();

                db.execSQL("delete from " + table_name + " where item_name = '" + item_name.split("        ")[0].trim() + "'");
                System.out.println("Recent List.......item_name" + item_name.split("        ")[0].trim());
                Toast.makeText(Recent_List.this, "Item: " + item_name + " has been deleted", Toast.LENGTH_SHORT).show();
                prodList = new ArrayList<String>();
                cursor = db.rawQuery("select * from "+ table_name +";", null);
                //System.out.println(cursor.toString());

                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    do {
                        prodList.add(cursor.getString(cursor.getColumnIndex("item_name")) + "        " + cursor.getInt(cursor.getColumnIndex("quantity")));

                    } while (cursor.moveToNext());
                    cursor.close();
                }

                adapter = new ArrayAdapter(Recent_List.this, R.layout.listitem_layout_commodityrecentlist, prodList);
                recent_list_view.setAdapter(adapter);
            }
        });

        delete_list.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
               // db = openOrCreateDatabase("CommoditiesDB.db", MODE_PRIVATE, null);
                db.execSQL("delete from " + table_name);

                prodList = new ArrayList<String>();
                cursor = db.rawQuery("select * from "+ table_name +";", null);
                //System.out.println(cursor.toString());

                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    do {
                        prodList.add(cursor.getString(cursor.getColumnIndex("item_name")) + "        " + cursor.getInt(cursor.getColumnIndex("quantity")));

                    } while (cursor.moveToNext());
                    cursor.close();
                }

                adapter = new ArrayAdapter(Recent_List.this, R.layout.listitem_layout_commodityrecentlist, prodList);
                recent_list_view.setAdapter(adapter);
            }
        });
    }
}
