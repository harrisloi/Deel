package com.example.harrisloi.deel.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.harrisloi.deel.Model.Order;
import com.example.harrisloi.deel.R;

import java.util.List;

class MyViewHolder extends RecyclerView.ViewHolder{

    public TextView name, quantity, price;

    public MyViewHolder(View itemView){
        super(itemView);

        name = (TextView)itemView.findViewById(R.id.product_name);
        quantity = (TextView)itemView.findViewById(R.id.product_quantity);
        price = (TextView)itemView.findViewById(R.id.product_price);
    }

}

public class OrderDetailAdapter extends RecyclerView.Adapter<MyViewHolder>{

    List<Order> myOrder;

    public OrderDetailAdapter(List<Order> myOrder) {
        this.myOrder = myOrder;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.order_detail_layout, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        Order order = myOrder.get(i);
        myViewHolder.name.setText(String.format("Name : %s", order.getProductName()));
        myViewHolder.quantity.setText(String.format("Quantity : %s", order.getQuantity()));
        myViewHolder.price.setText(String.format("Price : RM %s", order.getPrice()));
    }

    @Override
    public int getItemCount() {
        return myOrder.size();
    }
}
