package com.example.bhuva.store_buddy_group_31;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class GetProducts extends AppCompatActivity {

    TextView textView;
    EditText editText;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_products);
        final GetProducts getProducts = this;

        textView = (TextView) findViewById(R.id.textView);
        editText = (EditText) findViewById(R.id.editText);
        button = (Button) findViewById(R.id.button);


        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String input = editText.getText().toString();
                Toast.makeText(getApplicationContext(), "api", Toast.LENGTH_SHORT).show();
                // TODO: create APIRequest object, then execute it
                //APIRequest request = new APIRequest(getProducts, input);
                //request.execute();
            }
        });



    }

    protected Context getContext() {
        return getApplicationContext();
    }


    protected void onFinish(String searchQuery, String json) {


        if (json != null) {
            Intent intent = new Intent(GetProducts.this, LoadProducts.class);
            intent.putExtra("query", searchQuery);

            LoadProducts.json = json;

            startActivity(intent);
        }
    }
}
