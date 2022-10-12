package com.example.harrisloi.deel;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.harrisloi.deel.Common.Common;
import com.example.harrisloi.deel.Model.Request;
import com.example.harrisloi.deel.Model.ShippingInformation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.FirebaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TrackingOrderClient extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    FirebaseDatabase database;
    DatabaseReference request, shippingOrder;

    Request currentOrder;

    ShippingInformation shipper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_order_client);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        database = FirebaseDatabase.getInstance();
        request = database.getReference("Requests");

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        if(Common.currentRequest.getLatLang() != null && !Common.currentRequest.getLatLang().isEmpty()) {

            String[] latLng = Common.currentRequest.getLatLang().split(",");
            LatLng orderLocation = new LatLng(Double.parseDouble(latLng[0]), Double.parseDouble(latLng[1]));

            mMap.addMarker(new MarkerOptions().position(orderLocation).title("Your location."));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(orderLocation));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(16.0f));
        }

        shippingMap();
    }

    private void shippingMap() {
        shippingOrder = database.getReference("ShippingOrders");
        shippingOrder.child(Common.currentKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    shipper = dataSnapshot.getValue(ShippingInformation.class);

                    LatLng orderLocation = new LatLng(shipper.getLat(), shipper.getLng());

                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.car);
                    bitmap = Common.scaleBitMap(bitmap, 70, 70);

                    MarkerOptions marker = new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                            .title("Shipper")
                            .position(orderLocation);
                    mMap.addMarker(marker);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
