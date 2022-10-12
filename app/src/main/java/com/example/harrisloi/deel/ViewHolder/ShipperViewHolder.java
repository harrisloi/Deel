package com.example.harrisloi.deel.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.harrisloi.deel.Interface.ItemClickListener;
import com.example.harrisloi.deel.R;

public class ShipperViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView shipperName, shipperEmail, shipperPhone;
    public Button btnShipping, btnEdit, btnRemove;

    private ItemClickListener itemClickListener;

    public ShipperViewHolder(View itemView) {
        super(itemView);

        shipperName = (TextView)itemView.findViewById(R.id.shipper_name);
        shipperEmail = (TextView)itemView.findViewById(R.id.shipper_email);
        shipperPhone = (TextView)itemView.findViewById(R.id.shipper_phone);

        btnEdit = (Button)itemView.findViewById(R.id.btnEdit);
        btnRemove = (Button)itemView.findViewById(R.id.btnRemove);
        btnShipping = (Button)itemView.findViewById(R.id.btnShipping);

    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);
    }
}
