package com.example.harrisloi.deel;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.harrisloi.deel.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private EditText loginUsername;
    private EditText loginPassword;
    Button btnSignIn, btnSignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginUsername = (EditText) findViewById(R.id.edtUserName);
        loginPassword = (EditText) findViewById(R.id.edtPassword);
        btnSignIn = (Button)findViewById(R.id.btnSignIn);
        btnSignUp = (Button)findViewById(R.id.btnSignUp);

        SharedPreferences shared = getSharedPreferences("UserInfo", 0);
        String username = (shared.getString("Username", ""));

        if(username != null && !username.isEmpty())
        {
            String role = (shared.getString("Role", ""));

            if(role.equals("customer")){
                Intent signin = new Intent(MainActivity.this,Customer_Main.class);
                startActivity(signin);
                finish();
            }
            else if(role.equals("vendor")){
                Intent signin = new Intent(MainActivity.this,Vendor_Main.class);
                startActivity(signin);
                finish();
            }
            else if(role.equals("admin")){
                Intent signin = new Intent(MainActivity.this, admin_main.class);
                startActivity(signin);
                finish();
            }
            else if(role.equals("shipper")){
                Intent signin = new Intent(MainActivity.this,Driver_Main.class);
                startActivity(signin);
                finish();
            }
        }


        //Init Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(loginPassword != null && !loginPassword.equals("")
                        && loginUsername != null && !loginUsername.equals("")){
                    final ProgressDialog mdial = new ProgressDialog(MainActivity.this);
                    mdial.setMessage("Please waiting...");
                    mdial.show();

                    table_user.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if(dataSnapshot.child(loginUsername.getText().toString()).exists()){
                                //Get User Information
                                User user = dataSnapshot.child(loginUsername.getText().toString()).getValue(User.class);
                                if(user.getPassword().equals(loginPassword.getText().toString())){
                                    Toast.makeText(MainActivity.this, "Sign in successfully!", Toast.LENGTH_SHORT).show();
                                    SharedPreferences settings = getSharedPreferences("UserInfo", 0);
                                    SharedPreferences.Editor editor = settings.edit();
                                    editor.putString("Username", loginUsername.getText().toString());
                                    editor.putString("Password", user.getPassword());
                                    editor.putString("FirstName", user.getFirstName());
                                    editor.putString("LastName", user.getLastName());
                                    editor.putString("Email", user.getEmail());
                                    editor.putString("PhoneNumber", user.getPhoneNumber());
                                    editor.putString("Role", user.getRole());
                                    editor.commit();

                                    String role = user.getRole();

                                    if(role.equals("customer")){
                                        Intent signin = new Intent(MainActivity.this,Customer_Main.class);
                                        startActivity(signin);
                                        finish();
                                    }
                                    else if(role.equals("vendor")){
                                        Intent signin = new Intent(MainActivity.this,Vendor_Main.class);
                                        startActivity(signin);
                                        finish();
                                    }
                                    else if(role.equals("admin")){
                                        Intent signin = new Intent(MainActivity.this, admin_main.class);
                                        startActivity(signin);
                                        finish();
                                    }
                                    else if(role.equals("shipper")){
                                        Intent signin = new Intent(MainActivity.this,Driver_Main.class);
                                        startActivity(signin);
                                        finish();
                                    }
                                }
                                else {
                                    Toast.makeText(MainActivity.this, "Incorrect Username of Password.", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else{
                                Toast.makeText(MainActivity.this, "Incorrect Username of Password.", Toast.LENGTH_SHORT).show();
                            }
                            mdial.dismiss();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else {
                    Toast.makeText(MainActivity.this, "Please fill your Username and Password.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signin = new Intent(MainActivity.this,Sign_Up_Activity.class);
                startActivity(signin);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
