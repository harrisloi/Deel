package com.example.harrisloi.deel;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.harrisloi.deel.Common.Common;
import com.example.harrisloi.deel.Interface.ItemClickListener;
import com.example.harrisloi.deel.Model.Food;
import com.example.harrisloi.deel.Model.Vendor;
import com.example.harrisloi.deel.Service.ListenOrder;
import com.example.harrisloi.deel.Service.ListenOrderVendor;
import com.example.harrisloi.deel.ViewHolder.FoodViewHolderForVendor;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;

import java.util.UUID;

public class Vendor_Main extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference foodList;
    FirebaseStorage storage;
    StorageReference storageReference;

    EditText edtName, edtDesc, edtPrice;
    Button btnImg, btnCreate;

    Food newFood;
    String username = "";
    String AccessKey = "";


    Uri saveUri;
    private final int PICK_IMAGE_REQUEST = 71;

    FirebaseRecyclerAdapter<Food, FoodViewHolderForVendor> adapter;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Intent home = new Intent(Vendor_Main.this,Vendor_Main.class);
                    startActivity(home);
                    return true;
                case R.id.navigation_order:
                    Intent da = new Intent(Vendor_Main.this,OrderStatusVendor.class);
                    startActivity(da);

                    return true;
                case R.id.navigation_notifications:
                    Intent SignOut = new Intent(Vendor_Main.this, MainActivity.class);
                    SignOut.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    SharedPreferences settings = getSharedPreferences("UserInfo", 0);
                    settings.edit().clear().commit();
                    startActivity(SignOut);
                    Toast.makeText(Vendor_Main.this, "Sign out successfully.", Toast.LENGTH_SHORT).show();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor__main);

        firebaseDatabase = FirebaseDatabase.getInstance();
        foodList = firebaseDatabase.getReference("Foods");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFoodDialog();
            }
        });

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //Load Menu
        recyclerView = (RecyclerView)findViewById(R.id.recycle_vendor);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        SharedPreferences shared = getSharedPreferences("UserInfo", 0);
        username = (shared.getString("Username", ""));

        loadFood(username);

        //Call Service
        Intent service = new Intent(Vendor_Main.this, ListenOrderVendor.class);
        startService(service);
    }

    private void showFoodDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Vendor_Main.this);
        alertDialog.setTitle("Add new Food");
        alertDialog.setMessage("Please fill all information");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_food_layout = inflater.inflate(R.layout.add_new_food, null);

        edtName = add_food_layout.findViewById(R.id.edtName);
        edtDesc = add_food_layout.findViewById(R.id.edtDesc);
        edtPrice = add_food_layout.findViewById(R.id.edtPrice);

        btnImg = add_food_layout.findViewById(R.id.uploadImg);
        btnCreate = add_food_layout.findViewById(R.id.btnCreate);

        alertDialog.setView(add_food_layout);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        btnImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        //Set button
        alertDialog.setPositiveButton("X", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void uploadImage() {
        if(!edtPrice.getText().toString().isEmpty() && edtPrice.getText().toString() != null &&
                !edtDesc.getText().toString().isEmpty() && edtDesc.getText().toString() != null
                && !edtName.getText().toString().isEmpty() && edtName.getText().toString() != null){
            if(saveUri != null){
                final ProgressDialog mDialog = new ProgressDialog(this);
                mDialog.setMessage("Uploading...");
                mDialog.show();

                String imageName = UUID.randomUUID().toString();
                final StorageReference imageFolder = storageReference.child("images/foods/"+imageName);
                imageFolder.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        mDialog.dismiss();
                        Toast.makeText( Vendor_Main.this,"Uploaded", Toast.LENGTH_SHORT).show();
                        imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                newFood = new Food(edtName.getText().toString(),edtDesc.getText().toString(),edtPrice.getText().toString(), uri.toString(), username);

                                foodList.child(String.valueOf(System.currentTimeMillis()))
                                        .setValue(newFood);

                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mDialog.dismiss();
                        Toast.makeText( Vendor_Main.this,""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        mDialog.setMessage("Uploaded "+ progress+"%");
                    }
                });

            }
            else{
                Toast.makeText( Vendor_Main.this,"Please select image.", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText( Vendor_Main.this,"Please fill full information.", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            saveUri = data.getData();
            btnImg.setText("Image Selected");
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void loadFood(String username) {
        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolderForVendor>(Food.class, R.layout.food_item, FoodViewHolderForVendor.class, foodList.orderByChild("owner").equalTo(username)) {
            @Override
            protected void populateViewHolder(FoodViewHolderForVendor viewHolder, Food model, int position) {
                viewHolder.food_name.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.food_image);

                final Food local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                    }
                });
            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    //UPDATE and DELETE
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals(Common.UPDATE)){
            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        }
        else if(item.getTitle().equals(Common.DELETE)){
            DeleteFood(adapter.getRef(item.getOrder()).getKey());
        }

        return super.onContextItemSelected(item);
    }

    private void DeleteFood(final String key) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        foodList.child(key).removeValue();
                        Toast.makeText( Vendor_Main.this,"Food has been deleted.F", Toast.LENGTH_SHORT).show();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(Vendor_Main.this);
        builder.setMessage("Delete food?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private void showUpdateDialog(final String key, final Food item) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Vendor_Main.this);
        alertDialog.setTitle("Update Food");
        alertDialog.setMessage("Please fill all information");

        AccessKey = key;

        LayoutInflater inflater = this.getLayoutInflater();
        View add_food_layout = inflater.inflate(R.layout.add_new_food, null);

        edtName = add_food_layout.findViewById(R.id.edtName);
        edtDesc = add_food_layout.findViewById(R.id.edtDesc);
        edtPrice = add_food_layout.findViewById(R.id.edtPrice);

        btnImg = add_food_layout.findViewById(R.id.uploadImg);
        btnCreate = add_food_layout.findViewById(R.id.btnCreate);

        btnCreate.setText("Update");

        //Set Default Name
        edtName.setText(item.getName());
        edtDesc.setText(item.getDescription());
        edtPrice.setText(item.getPrice());

        alertDialog.setView(add_food_layout);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        btnImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeImage(item);
            }
        });

        //Set button
        alertDialog.setPositiveButton("X", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        alertDialog.show();
    }

    private void changeImage(final Food item) {
        if(!edtPrice.getText().toString().isEmpty() && edtPrice.getText().toString() != null &&
                !edtDesc.getText().toString().isEmpty() && edtDesc.getText().toString() != null
                && !edtName.getText().toString().isEmpty() && edtName.getText().toString() != null){
            if(saveUri != null){
                final ProgressDialog mDialog = new ProgressDialog(this);
                mDialog.setMessage("Uploading...");
                mDialog.show();

                String imageName = UUID.randomUUID().toString();
                final StorageReference imageFolder = storageReference.child("images/foods/"+imageName);
                imageFolder.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        mDialog.dismiss();
                        Toast.makeText( Vendor_Main.this,"Updated "+edtName.getText().toString(), Toast.LENGTH_SHORT).show();
                        imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                item.setImage(uri.toString());
                                item.setName(edtName.getText().toString());
                                item.setDescription(edtDesc.getText().toString());
                                item.setPrice(edtPrice.getText().toString());
                                foodList.child(AccessKey).setValue(item);
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mDialog.dismiss();
                        Toast.makeText( Vendor_Main.this,""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        mDialog.setMessage("Uploaded "+ progress+"%");
                    }
                });

            }
            else{
                Toast.makeText( Vendor_Main.this,"Please select image.", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText( Vendor_Main.this,"Please fill full information.", Toast.LENGTH_SHORT).show();
        }

    }
}
