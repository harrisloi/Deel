package com.example.harrisloi.deel;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.harrisloi.deel.Database.Database;
import com.example.harrisloi.deel.Model.Food;
import com.example.harrisloi.deel.Model.Order;
import com.example.harrisloi.deel.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class FoodDetail extends AppCompatActivity {

    TextView food_name, food_price, food_desc;
    ImageView food_img;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnCart;
    ElegantNumberButton numberButton;

    static String foodID = "";

    FirebaseDatabase firebaseDatabase;
    DatabaseReference foods;

    Food currentFood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        //Firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        foods = firebaseDatabase.getReference("Foods");

        //Init view
        numberButton = (ElegantNumberButton)findViewById(R.id.number_button);
        btnCart = (FloatingActionButton)findViewById(R.id.btnCart);

        //Get Food ID from Intent
        if(getIntent() != null)
            foodID = getIntent().getStringExtra("FoodID");
        if(!foodID.isEmpty()){
            getDetailFood(foodID);
        }

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int check = new Database(getBaseContext()).checkCart(foodID);

                if(check <= 0){
                    new Database(getBaseContext()).addToCart(new Order(
                            foodID,
                            currentFood.getName(),
                            numberButton.getNumber(),
                            currentFood.getPrice(),
                            currentFood.getOwner()
                    ));

                    SharedPreferences settings = getSharedPreferences("FoodInfo", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("Owner", currentFood.getOwner());
                    editor.commit();
                    Toast.makeText(FoodDetail.this, "Added to Cart.", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(FoodDetail.this, "Food has been added into cart.", Toast.LENGTH_LONG).show();
                }
            }
        });

        food_desc = (TextView)findViewById(R.id.food_desc);
        food_name = (TextView)findViewById(R.id.food_name);
        food_price = (TextView)findViewById(R.id.food_price);

        food_img = (ImageView)findViewById(R.id.img_food);

        collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collapsing);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);



    }

    private void getDetailFood(String foodID) {
        foods.child(foodID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentFood = dataSnapshot.getValue(Food.class);

                //Set Image
                Picasso.with(getBaseContext()).load(currentFood.getImage())
                        .into(food_img);

                collapsingToolbarLayout.setTitle(currentFood.getName());

                food_price.setText("RM "+currentFood.getPrice());

                food_name.setText(currentFood.getName());

                food_desc.setText(currentFood.getDescription());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
