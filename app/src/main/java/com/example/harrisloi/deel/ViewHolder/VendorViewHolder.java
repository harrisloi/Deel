package com.example.harrisloi.deel.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.harrisloi.deel.Interface.ItemClickListener;
import com.example.harrisloi.deel.R;

public class VendorViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView vendorName, vendorEmail, vendorPhone;
    public Button btnEdit, btnRemove;
    private ItemClickListener itemClickListener;


    public VendorViewHolder(View itemView) {
        super(itemView);

        vendorName = (TextView)itemView.findViewById(R.id.vendor_name);
        vendorEmail = (TextView)itemView.findViewById(R.id.vendor_email);
        vendorPhone = (TextView)itemView.findViewById(R.id.vendor_phone);

        btnEdit = (Button)itemView.findViewById(R.id.btnEdit);
        btnRemove = (Button)itemView.findViewById(R.id.btnRemove);

    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);
    }
}
