package com.example.bhuva.store_buddy_group_31;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;


public class MainActivity extends AppCompatActivity {
    Intent sign_up;
    Intent get_prod;

    static SQLiteDatabase db;
    static String table_name = "UserDetails";
    static String recent_list_table_name  = "recent_list_table";
    static String fav_table = "favorites_table";
    static String product_catalog_table = "product_catalog";
    private EditText username1, password1;
    private TextView result1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = openOrCreateDatabase("CommoditiesDB.db", MODE_PRIVATE, null);
        System.out.println("DB created");

        String createSql = "id text,name text,email text,password text";

        MainActivity.db.execSQL("create table if not exists " + MainActivity.table_name + " (" + createSql + ");");

        username1 = (EditText) findViewById(R.id.username);
        password1 = (EditText) findViewById(R.id.password);
        result1 = (TextView) findViewById(R.id.result);
        result1.setVisibility(View.INVISIBLE);

        Button btnLogin = (Button) findViewById(R.id.btn_login);
        final Button btnSignup = (Button) findViewById(R.id.btn_signup);


        btnSignup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                sign_up = new Intent(MainActivity.this, Sign_UP.class);
                startActivity(sign_up);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                Cursor c;
                String username = username1.getText().toString();
                String password = password1.getText().toString();

                c = db.rawQuery("select * from UserDetails where id = '"+username+"'",null);
                System.out.println(c.toString());
                c.moveToFirst();
                if(c.getCount() < 1)
                {
                    Toast.makeText(MainActivity.this,"Username not available! Please sign up!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(c.getString(c.getColumnIndex("password")).toString().equals(password.trim())){
                        startActivity(new Intent(MainActivity.this,HomePage.class));
                    }
                    else{
                        Toast.makeText(MainActivity.this,"Username and password does not match", Toast.LENGTH_SHORT).show();
                        //result1.setText("Username and password does not match");
                        //result1.setVisibility(View.VISIBLE);
                    }
                }
            }
        });




    }

    protected Context getContext() {
        return getApplicationContext();
    }



}
