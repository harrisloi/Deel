package com.example.harrisloi.deel;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.harrisloi.deel.Common.Common;
import com.example.harrisloi.deel.Model.Request;
import com.example.harrisloi.deel.Service.ListenOrderShipper;
import com.example.harrisloi.deel.Service.ListenOrderVendor;
import com.example.harrisloi.deel.ViewHolder.OrderViewHolder;
import com.example.harrisloi.deel.ViewHolder.OrderViewHolderShipper;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Driver;

public class Driver_Main extends AppCompatActivity {

    FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallback;
    LocationRequest locationRequest;

    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;

    FirebaseDatabase database;
    DatabaseReference shipper;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Request, OrderViewHolderShipper> adapter;

    String username = "";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver__main);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.CALL_PHONE
            }, Common.REQUEST_CODE);
        } else {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.CALL_PHONE
            }, Common.REQUEST_CODE);

            check();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent SignOut = new Intent(Driver_Main.this, MainActivity.class);
                SignOut.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                SharedPreferences settings = getSharedPreferences("UserInfo", 0);
                settings.edit().clear().commit();
                startActivity(SignOut);
                Toast.makeText(Driver_Main.this, "Sign out successfully.", Toast.LENGTH_SHORT).show();
            }
        });

        SharedPreferences shared = getSharedPreferences("UserInfo", 0);
        username = (shared.getString("Username", ""));

        //Init Firebase
        database = FirebaseDatabase.getInstance();
        shipper = database.getReference(Common.ORDER_NEED_SHIP_TABLE).child(username);

        recyclerView = (RecyclerView) findViewById(R.id.recycle_order);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        loadAllOrder(username);


        //Call Service
        Intent service = new Intent(Driver_Main.this, ListenOrderShipper.class);
        startService(service);

    }

    @Override
    protected void onStop() {
        if (fusedLocationProviderClient != null) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
        super.onStop();
    }

    private void loadAllOrder(final String username) {
        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolderShipper>(Request.class, R.layout.order_layout_shipper,
                OrderViewHolderShipper.class, shipper) {
            @Override
            protected void populateViewHolder(OrderViewHolderShipper viewHolder, final Request model, final int position) {
                viewHolder.txtOrderId.setText(adapter.getRef(position).getKey());
                viewHolder.txtOrderStatus.setText(Common.convertCodeToStatus(model.getStatus()));
                viewHolder.txtOrderAddress.setText(model.getAddress());
                viewHolder.txtOrderPhone.setText(model.getPhone());
                viewHolder.txtOrderPrice.setText(model.getTotal());
                viewHolder.txtOrderCustomerName.setText(model.getFullName());

                viewHolder.btnShipping.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (mLastLocation != null && !mLastLocation.equals("")) {
                            Common.currentShippingOrder(adapter.getRef(position).getKey(),
                                    username
                                    , mLastLocation);
                            Common.currentRequest = model;
                            Common.currentKey = adapter.getRef(position).getKey();

                            startActivity(new Intent(Driver_Main.this, TrackingOrderShipper.class));
                        } else {
                            check();
                            //Toast.makeText(Driver_Main.this, "Device is not able to get location, please restart application.", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    public void check() {
        buildLocationRequest();
        buildLocationCallBack();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Common.REQUEST_CODE: {
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        check();
                    }
                    else
                    {
                        Toast.makeText(Driver_Main.this, "You should assign permission.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            default:
                break;
        }
    }

    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(10f);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
    }

    private void buildLocationCallBack() {
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                mLastLocation = locationResult.getLastLocation();

//                Toast.makeText(Driver_Main.this, new StringBuilder("")
//                .append(mLastLocation.getLatitude())
//                .append("/")
//                .append(mLastLocation.getLongitude())
//                .toString(), Toast.LENGTH_SHORT).show();
            }
        };
    }

}
