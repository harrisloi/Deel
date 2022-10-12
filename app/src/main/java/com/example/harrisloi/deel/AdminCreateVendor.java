package com.example.harrisloi.deel;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.harrisloi.deel.Model.Food;
import com.example.harrisloi.deel.Model.User;
import com.example.harrisloi.deel.Model.Vendor;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class AdminCreateVendor extends AppCompatActivity {

    EditText edtUsername, edtFirstName, edtLastName, edtPassword, edtEmail, edtPhoneNumber, edtShopName;
    Spinner dropDownVendor;
    Button btnCreate, uploadImg;
    boolean checkEmail = false;
    boolean checkPhoneNumber = false;
    String email;
    String phone;

    FirebaseDatabase database;
    DatabaseReference user;
    DatabaseReference vendor;
    FirebaseStorage storage;
    StorageReference storageReference;

    Uri saveUri;
    private final int PICK_IMAGE_REQUEST = 71;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_create_vendor);

        edtUsername = (EditText)findViewById(R.id.edtUserName);
        edtFirstName = (EditText)findViewById(R.id.edtFirstName);
        edtLastName = (EditText)findViewById(R.id.edtLastName);
        edtPassword = (EditText)findViewById(R.id.edtPassword);
        edtEmail = (EditText)findViewById(R.id.edtEmail);
        edtPhoneNumber = (EditText)findViewById(R.id.edtPhoneNumber);
        edtShopName = (EditText)findViewById(R.id.edtShopName);
        btnCreate = (Button)findViewById(R.id.btnCreate);
        uploadImg = (Button)findViewById(R.id.uploadImg);
        dropDownVendor = (Spinner)findViewById(R.id.dropDownVendor);

        //Set DropDown items
        String[] items = new String[]{"Restaurant", "Mini Market", "Hotel"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropDownVendor.setAdapter(adapter);

        database = FirebaseDatabase.getInstance();
        user = database.getReference("User");
        vendor = database.getReference("Vendor");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        uploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog mdial = new ProgressDialog(AdminCreateVendor.this);
                mdial.setMessage("Please waiting...");
                mdial.show();

                user.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(!edtEmail.getText().toString().equals(null) && !edtEmail.getText().toString().equals("") && !edtPhoneNumber.getText().toString().equals(null) && !edtPhoneNumber.getText().toString().equals("")
                                && !edtFirstName.getText().toString().equals(null) && !edtFirstName.getText().toString().equals("") && !edtLastName.getText().toString().equals(null) && !edtLastName.getText().toString().equals("")
                                && !edtPassword.getText().toString().equals(null) && !edtPassword.getText().toString().equals("") && !edtUsername.getText().toString().equals(null) && !edtUsername.getText().toString().equals("")
                                && !edtShopName.getText().toString().equals(null) && !edtShopName.getText().toString().equals("")){
                            //Check if Username exits
                            if(dataSnapshot.child(edtUsername.getText().toString()).exists()){
                                mdial.dismiss();
                                Toast.makeText(AdminCreateVendor.this,"Username has already registered.", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                for(DataSnapshot data: dataSnapshot.getChildren()){
                                    User user = data.getValue(User.class);
                                    email = user.getEmail();

                                    //Validate Email address
                                    if(email.equals(edtEmail.getText().toString())){
                                        checkEmail = false;
                                        break;
                                    }
                                    else{
                                        checkEmail = true;
                                        phone = user.getPhoneNumber();
                                        //Validate Phone number
                                        if(phone.equals(edtPhoneNumber.getText().toString())){
                                            checkPhoneNumber = false;
                                            break;
                                        }
                                        else{
                                            checkPhoneNumber = true;
                                        }
                                    }
                                }
                                validate();
                                mdial.dismiss();
                            }
                        }
                        else{
                            Toast.makeText(AdminCreateVendor.this,"Please make sure to fill all the information.", Toast.LENGTH_SHORT).show();
                            mdial.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            saveUri = data.getData();
            uploadImg.setText("Image Selected");
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    public void validate(){
        if(checkEmail){
            if(checkPhoneNumber){
                if(saveUri != null) {
                    final FirebaseDatabase database = FirebaseDatabase.getInstance();
                    final DatabaseReference table_user = database.getReference("User");

                    String role = "vendor";
                    User user = new User(edtFirstName.getText().toString(), edtLastName.getText().toString(), edtEmail.getText().toString(), edtPassword.getText().toString(), role, edtPhoneNumber.getText().toString());
                    table_user.child(edtUsername.getText().toString()).setValue(user);

                    //Save Image
                    final String userName = edtUsername.getText().toString();
                    final StorageReference imageFolder = storageReference.child("images/vendors/"+userName);
                    imageFolder.putFile(saveUri);
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            DatabaseReference vendor = database.getReference("Vendor");

                            String type = dropDownVendor.getSelectedItem().toString();
                            Vendor u = new Vendor(edtShopName.getText().toString(), uri.toString(), type);
                            vendor.child(userName).setValue(u);
                            Toast.makeText(AdminCreateVendor.this,"Vendor has been created.", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });

                }
            }
            else {
                Toast.makeText(AdminCreateVendor.this,"Phone number has already registered.", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(AdminCreateVendor.this,"Email has already registered.", Toast.LENGTH_SHORT).show();
        }
    }
}
