package com.example.harrisloi.deel;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.harrisloi.deel.Common.Common;
import com.example.harrisloi.deel.Database.Database;


import com.example.harrisloi.deel.Model.Order;
import com.example.harrisloi.deel.Model.Request;
import com.example.harrisloi.deel.ViewHolder.CartAdapter;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Cart extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;

    public TextView txtTotalPrice;
    Button btnPlace;

    String username = "";
    String phoneNumber = "";
    String email = "";
    String firstName = "";
    String lastName = "";

    List<Order> carts = new ArrayList<>();

    CartAdapter adapter;

    Place shippingAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        //Firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        //Init
        recyclerView = (RecyclerView)findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        txtTotalPrice = (TextView)findViewById(R.id.Total);
        btnPlace = (Button)findViewById(R.id.btnPlaceOrder);

        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(carts.size() > 0)
                    showAlertDialog();
                else
                    Toast.makeText(Cart.this, "Your cart is empty.", Toast.LENGTH_SHORT).show();

            }
        });

        localListFood();

    }

    private void showAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Cart.this);
        alertDialog.setTitle("One more step");
        alertDialog.setMessage("Enter your address: ");

        LayoutInflater inflater = this.getLayoutInflater();
        View order_address = inflater.inflate(R.layout.order_address, null);

        PlaceAutocompleteFragment placeAutocompleteFragment = (PlaceAutocompleteFragment)getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        //Hide search icon before fragment
        placeAutocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_button).setVisibility(View.GONE);
        //Set Hint for Autocomplete Edit Text
        ((EditText)placeAutocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input))
                .setHint("Enter your address.");

        //Set Text Size
        ((EditText)placeAutocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input))
                .setTextSize(14);

        //Get Address from Place Autocomplete
        placeAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                shippingAddress = place;
            }

            @Override
            public void onError(Status status) {
                Log.e("ERROR", status.getStatusMessage());
            }
        });

        alertDialog.setView(order_address); // add edit text to alert dialog
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alertDialog.setPositiveButton("Process", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences shared = getSharedPreferences("UserInfo", 0);
                username = (shared.getString("Username", ""));
                phoneNumber = (shared.getString("PhoneNumber", ""));
                firstName = (shared.getString("FirstName", ""));
                lastName = (shared.getString("LastName", ""));
                email = (shared.getString("Email", ""));

                SharedPreferences shared2 = getSharedPreferences("FoodInfo", 0);
                String ownername = (shared2.getString("Owner", ""));

                String fullName = firstName + " " + lastName;

                Request request = new Request(phoneNumber, shippingAddress.getAddress().toString(), txtTotalPrice.getText().toString(),
                        username, fullName, "0" ,String.format("%s,%s", shippingAddress.getLatLng().latitude, shippingAddress.getLatLng().longitude)
                        ,carts, ownername);

                //Submit to firebase
                requests.child(String.valueOf(System.currentTimeMillis()))
                        .setValue(request);

                //Remove Fragment
                getFragmentManager().beginTransaction()
                        .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment))
                        .commit();

                //Delete Cart
                new Database(getBaseContext()).cleanCart();
                Toast.makeText(Cart.this, "Thank you, order has been placing.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                //Remove Fragment
                getFragmentManager().beginTransaction()
                        .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment))
                        .commit();
            }
        });

        alertDialog.show();

    }

    private void localListFood() {
        carts = new Database(this).getCarts();
        adapter = new CartAdapter(carts,this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        //Calculate total price
        int total = 0;
        for(Order order:carts)
            total+=(Integer.parseInt(order.getPrice()))*(Integer.parseInt(order.getQuantity()));

        Locale locale = new Locale("en", "MY");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

        txtTotalPrice.setText(fmt.format(total));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals(Common.DELETE))
            deleteCartItem(item.getOrder());
        return true;
    }

    private void deleteCartItem(int position) {
        carts.remove(position);

        new Database(this).cleanCart();

        for(Order item:carts)
            new Database(this).addToCart(item);

        //Refresh
        localListFood();
    }
}
