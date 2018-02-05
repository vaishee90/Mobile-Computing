package com.example.bhuva.store_buddy_group_31;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.widget.TextView;

public class Navigate extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    String lat="";
    String lng= "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate);
        Bundle bundle = getIntent().getExtras();
        String address = bundle.getString("address");
        lat = bundle.getString("lat");
        lng = bundle.getString("lng");
        TextView txtAddr = (TextView)findViewById(R.id.txtAddress);
        txtAddr.setText("The closest store is: "+address+"\n\n\nUse the map below to navigate to store");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng store = new LatLng(Double.valueOf(lat),Double.valueOf(lng));

        mMap.addMarker(new MarkerOptions().position(store).title("Store"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(store));
    }
}

