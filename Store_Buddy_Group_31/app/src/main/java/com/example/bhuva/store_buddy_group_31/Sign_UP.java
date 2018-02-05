package com.example.bhuva.store_buddy_group_31;


import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.os.Bundle;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import android.widget.Toast;


public class Sign_UP extends AppCompatActivity {

    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign__up);



        Button btn_submit = (Button)findViewById(R.id.btn_submit_sign_up);

        btn_submit.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                TextView txtName = (TextView) findViewById(R.id.editTextName);
                TextView txtEmail = (TextView) findViewById(R.id.editTextEmail);
                TextView txtUserId = (TextView) findViewById(R.id.editTextUserId);
                TextView txtPass = (TextView) findViewById(R.id.editTextPass);
                TextView txtConfirmPass = (TextView) findViewById(R.id.editTextConfPass);

                if (txtName.getText() == null || txtEmail.getText() == null || txtUserId.getText() == null || txtPass.getText() == null || txtConfirmPass.getText() == null) {
                    Toast.makeText(getApplicationContext(), "Please enter all the details", Toast.LENGTH_SHORT).show();


                } else if (!txtPass.getText().toString().equals(txtConfirmPass.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Passwords dont match", Toast.LENGTH_SHORT).show();

                } else {



                    String createSql = "id text,name text,email text,password text";

                    MainActivity.db.execSQL("create table if not exists " + MainActivity.table_name + " (" + createSql + ");");
                    System.out.println("Creating table...");
                    MainActivity.db.execSQL("insert into " + MainActivity.table_name  + " values ('" + txtUserId.getText().toString() + "','" + txtName.getText().toString() + "','" + txtEmail.getText().toString() + "','" + txtPass.getText().toString() + "')");
                    System.out.println("User record stored...");
                    Toast.makeText(getApplicationContext(),"Sign Up successful", Toast.LENGTH_SHORT).show();

                    cursor = MainActivity.db.rawQuery("select * from "+ MainActivity.table_name +";", null);
                    cursor.moveToFirst();
                    Toast.makeText(getApplicationContext(),"Welcome "+cursor.getString(0), Toast.LENGTH_SHORT).show();

                    Intent home = new Intent(Sign_UP.this, MainActivity.class);
                    startActivity(home);


                }
            }
        });




    }
    }



