package com.example.harrisloi.deel;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.harrisloi.deel.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminCreateClient extends AppCompatActivity {

    EditText edtUsername, edtFirstName, edtLastName, edtPassword, edtEmail, edtPhoneNumber;
    Button btnCreate;
    boolean checkEmail = false;
    boolean checkPhoneNumber = false;
    String email;
    String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_create_client);

        edtUsername = (EditText)findViewById(R.id.edtUserName);
        edtFirstName = (EditText)findViewById(R.id.edtFirstName);
        edtLastName = (EditText)findViewById(R.id.edtLastName);
        edtPassword = (EditText)findViewById(R.id.edtPassword);
        edtEmail = (EditText)findViewById(R.id.edtEmail);
        edtPhoneNumber = (EditText)findViewById(R.id.edtPhoneNumber);
        btnCreate = (Button)findViewById(R.id.btnCreate);


        String role = "customer";

        //Init Firebase
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog mdial = new ProgressDialog(AdminCreateClient.this);
                mdial.setMessage("Please waiting...");
                mdial.show();

                table_user.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(!edtEmail.getText().toString().equals(null) && !edtEmail.getText().toString().equals("") && !edtPhoneNumber.getText().toString().equals(null) && !edtPhoneNumber.getText().toString().equals("")
                                && !edtFirstName.getText().toString().equals(null) && !edtFirstName.getText().toString().equals("") && !edtLastName.getText().toString().equals(null) && !edtLastName.getText().toString().equals("")
                                && !edtPassword.getText().toString().equals(null) && !edtPassword.getText().toString().equals("") && !edtUsername.getText().toString().equals(null) && !edtUsername.getText().toString().equals("")){
                            //Check if Username exits
                            if(dataSnapshot.child(edtUsername.getText().toString()).exists()){
                                mdial.dismiss();
                                Toast.makeText(AdminCreateClient.this,"Username has already registered.", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(AdminCreateClient.this,"Please make sure to fill all the information.", Toast.LENGTH_SHORT).show();
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

    public void validate(){
        if(checkEmail){
            if(checkPhoneNumber){
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference table_user = database.getReference("User");

                String role = "customer";
                User user = new User(edtFirstName.getText().toString(), edtLastName.getText().toString(), edtEmail.getText().toString(), edtPassword.getText().toString(), role, edtPhoneNumber.getText().toString());
                table_user.child(edtUsername.getText().toString()).setValue(user);
                Toast.makeText(AdminCreateClient.this,"Sign up successfully.", Toast.LENGTH_SHORT).show();
                finish();
            }
            else {
                Toast.makeText(AdminCreateClient.this,"Phone number has already registered.", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(AdminCreateClient.this,"Email has already registered.", Toast.LENGTH_SHORT).show();
        }
    }
}
