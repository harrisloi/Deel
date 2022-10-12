package com.example.harrisloi.deel.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.harrisloi.deel.R;

public class OrderViewHolderShipper extends RecyclerView.ViewHolder{
    public TextView txtOrderId, txtOrderStatus, txtOrderPhone, txtOrderAddress, txtOrderPrice, txtOrderCustomerName;

    public Button btnShipping;

    public OrderViewHolderShipper(View itemView) {
        super(itemView);

        txtOrderAddress = (TextView)itemView.findViewById(R.id.order_address);
        txtOrderId = (TextView)itemView.findViewById(R.id.order_id);
        txtOrderStatus = (TextView)itemView.findViewById(R.id.order_status);
        txtOrderPhone = (TextView)itemView.findViewById(R.id.order_phone);
        txtOrderPrice = (TextView)itemView.findViewById(R.id.order_price);
        txtOrderCustomerName = (TextView)itemView.findViewById(R.id.order_customer_name);

        btnShipping = (Button)itemView.findViewById(R.id.btnShipping);
    }
}
