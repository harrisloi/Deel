package com.example.harrisloi.deel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.harrisloi.deel.Common.Common;
import com.example.harrisloi.deel.Database.Database;
import com.example.harrisloi.deel.Interface.ItemClickListener;
import com.example.harrisloi.deel.Model.Order;
import com.example.harrisloi.deel.Model.Request;
import com.example.harrisloi.deel.ViewHolder.OrderViewHolder;
import com.example.harrisloi.deel.ViewHolder.OrderViewHolderVendor;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class OrderStatus extends AppCompatActivity {

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;

    FirebaseDatabase database;
    DatabaseReference requests;
    DatabaseReference check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        //Firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        recyclerView = (RecyclerView)findViewById(R.id.list_order);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //Get user username
        SharedPreferences shared = getSharedPreferences("UserInfo", 0);
        String username = (shared.getString("Username", ""));

        loadOrders(username);
    }

    private void loadOrders(String username) {

        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(Request.class, R.layout.order_layout,
                OrderViewHolder.class,requests.orderByChild("username").equalTo(username)) {
            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, final Request model,final int position) {
                viewHolder.txtOrderId.setText(adapter.getRef(position).getKey());
                viewHolder.txtOrderStatus.setText(Common.convertCodeToStatus(model.getStatus()));
                viewHolder.txtOrderAddress.setText(model.getAddress());
                viewHolder.txtOrderPhone.setText(model.getPhone());
                viewHolder.txtOrderPrice.setText(model.getTotal());

                viewHolder.btnDetail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent orderDetail = new Intent(OrderStatus.this, OrderDetail.class);
                        Common.currentRequest = model;
                        orderDetail.putExtra("OrderId", adapter.getRef(position).getKey());
                        startActivity(orderDetail);
                    }
                });

                viewHolder.btnLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String status = model.getStatus();
                        if(!status.equals(2)){
                            Common.currentRequest = model;
                            Common.currentKey = adapter.getRef(position).getKey();
                            startActivity(new Intent(OrderStatus.this, TrackingOrderClient.class));
                        }
                        else {
                            Toast.makeText(OrderStatus.this, "Order #"
                                    +adapter.getRef(position).getKey()+" has been shipped.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);
    }

}
