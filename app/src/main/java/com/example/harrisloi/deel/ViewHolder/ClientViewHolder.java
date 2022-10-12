package com.example.harrisloi.deel.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.harrisloi.deel.Interface.ItemClickListener;
import com.example.harrisloi.deel.R;

public class ClientViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView clientName, clientEmail, clientPhone;
    public Button btnEdit, btnRemove;

    private ItemClickListener itemClickListener;

    public ClientViewHolder(View itemView) {
        super(itemView);

        clientName = (TextView)itemView.findViewById(R.id.client_name);
        clientEmail = (TextView)itemView.findViewById(R.id.client_email);
        clientPhone = (TextView)itemView.findViewById(R.id.client_phone);

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
