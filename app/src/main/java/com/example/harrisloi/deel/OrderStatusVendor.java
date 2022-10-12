package com.example.harrisloi.deel;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.harrisloi.deel.Common.Common;
import com.example.harrisloi.deel.Interface.ItemClickListener;
import com.example.harrisloi.deel.Model.Food;
import com.example.harrisloi.deel.Model.Request;
import com.example.harrisloi.deel.ViewHolder.FoodViewHolderForVendor;
import com.example.harrisloi.deel.ViewHolder.OrderViewHolder;
import com.example.harrisloi.deel.ViewHolder.OrderViewHolderVendor;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.jaredrummler.materialspinner.MaterialSpinner;

public class OrderStatusVendor extends AppCompatActivity {

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Request, OrderViewHolderVendor> adapter;

    FirebaseDatabase database;
    DatabaseReference requests;

    MaterialSpinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status_vendor);

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

        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolderVendor>(Request.class, R.layout.order_layout_vendor,
                OrderViewHolderVendor.class,requests.orderByChild("owner").equalTo(username)) {
            @Override
            protected void populateViewHolder(OrderViewHolderVendor viewHolder, final Request model, final int position) {
                viewHolder.txtOrderId.setText(adapter.getRef(position).getKey());
                viewHolder.txtOrderStatus.setText(Common.convertCodeToStatus(model.getStatus()));
                viewHolder.txtOrderAddress.setText(model.getAddress());
                viewHolder.txtOrderPhone.setText(model.getPhone());
                viewHolder.txtOrderPrice.setText(model.getTotal());
                viewHolder.txtOrderCustomerName.setText(model.getFullName());

                viewHolder.btnRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteOrder(adapter.getRef(position).getKey());
                    }
                });

//                viewHolder.btnEdit.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        showUpdateDialog(adapter.getRef(position).getKey(), adapter.getItem(position));
//                    }
//                });

                viewHolder.btnDetail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent orderDetail = new Intent(OrderStatusVendor.this, OrderDetail.class);
                        Common.currentRequest = model;
                        orderDetail.putExtra("OrderId", adapter.getRef(position).getKey());
                        startActivity(orderDetail);
                    }
                });
            }
        };

        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

//    private void showUpdateDialog(String key, final Request item) {
//        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(OrderStatusVendor.this);
//        alertDialog.setTitle("Update Order");
//        alertDialog.setMessage("Please Choose Status");
//
//        LayoutInflater inflater = this.getLayoutInflater();
//        final View view = inflater.inflate(R.layout.update_order_layout, null);
//
//        spinner = (MaterialSpinner)view.findViewById(R.id.statusSpinner);
//        spinner.setItems("Placed", "On the way");
//
//        alertDialog.setView(view);
//
//        final String localkey = key;
//        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//                item.setStatus(String.valueOf(spinner.getSelectedIndex()));
//                adapter.notifyDataSetChanged();
//
//                requests.child(localkey).setValue(item);
//            }
//        });
//        alertDialog.show();
//    }

    private void deleteOrder(final String key) {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        requests.child(key).removeValue();
                        Toast.makeText( OrderStatusVendor.this,"Order has been deleted.F", Toast.LENGTH_SHORT).show();
                        adapter.notifyDataSetChanged();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(OrderStatusVendor.this);
        builder.setMessage("Delete order?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

}
