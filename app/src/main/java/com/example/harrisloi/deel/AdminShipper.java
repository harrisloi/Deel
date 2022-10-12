package com.example.harrisloi.deel;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.strictmode.CleartextNetworkViolation;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.harrisloi.deel.Model.User;
import com.example.harrisloi.deel.ViewHolder.ClientViewHolder;
import com.example.harrisloi.deel.ViewHolder.ShipperViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class AdminShipper extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    EditText edtUsername, edtFirstName, edtLastName, edtPassword, edtEmail, edtPhoneNumber;
    Button btnUpdate;

    FirebaseDatabase database;
    DatabaseReference user;

    FirebaseRecyclerAdapter<User, ShipperViewHolder> adapter;

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;

    String AccessKey = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_shipper);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Init Firebase
        database = FirebaseDatabase.getInstance();
        user = database.getReference("User");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminShipper.this, AdminHistory.class));
            }
        });

        recyclerView = (RecyclerView)findViewById(R.id.recycle_users);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //load Client profile
        loadClient();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void loadClient() {
        adapter = new FirebaseRecyclerAdapter<User, ShipperViewHolder>(User.class, R.layout.shipper_layout, ShipperViewHolder.class, user.orderByChild("role").equalTo("shipper") ) {
            @Override
            protected void populateViewHolder(ShipperViewHolder viewHolder,final User model,final int position) {
                viewHolder.shipperName.setText(model.getFirstName()+" "+ model.getLastName());
                viewHolder.shipperEmail.setText(model.getEmail());
                viewHolder.shipperPhone.setText(model.getPhoneNumber());

                viewHolder.btnEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showEditDialog(adapter.getRef(position).getKey(), model);
                    }
                });

                viewHolder.btnRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showRemoveDialog(adapter.getRef(position).getKey());
                    }
                });

                viewHolder.btnShipping.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);
    }

    private void showEditDialog(final String key, final User model) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(AdminShipper.this);
        alertDialog.setTitle("Update Shipper");
        alertDialog.setMessage("Please fill all information");

        LayoutInflater inflater = this.getLayoutInflater();
        View updateUser = inflater.inflate(R.layout.create_client_layout, null);

        AccessKey = key;

        edtFirstName = (EditText) updateUser.findViewById(R.id.edtFirstName);
        edtLastName = (EditText) updateUser.findViewById(R.id.edtLastName);
        edtPassword = (EditText) updateUser.findViewById(R.id.edtPassword);
        edtPhoneNumber = (EditText) updateUser.findViewById(R.id.edtPhoneNumber);
        edtEmail = (EditText) updateUser.findViewById(R.id.edtEmail);

        btnUpdate = (Button) updateUser.findViewById(R.id.btnUpdate);
        btnUpdate.setText("Update Shipper");

        //Set Data
        edtFirstName.setText(model.getFirstName());
        edtLastName.setText(model.getLastName());
        edtPassword.setText(model.getPassword());
        edtPhoneNumber.setText(model.getPhoneNumber());
        edtEmail.setText(model.getEmail());

        alertDialog.setView(updateUser);
        alertDialog.setIcon(R.drawable.ic_people_black_24dp);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUser(model);
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

    private void updateUser(final User model) {
        if(!edtEmail.getText().toString().equals(null) && !edtEmail.getText().toString().equals("") && !edtPhoneNumber.getText().toString().equals(null) && !edtPhoneNumber.getText().toString().equals("")
                && !edtFirstName.getText().toString().equals(null) && !edtFirstName.getText().toString().equals("") && !edtLastName.getText().toString().equals(null) && !edtLastName.getText().toString().equals("")
                && !edtPassword.getText().toString().equals(null) && !edtPassword.getText().toString().equals("")){

            Map<String, Object>update = new HashMap<>();
            update.put("firstName", edtFirstName.getText().toString());
            update.put("lastName", edtLastName.getText().toString());
            update.put("phoneNumber", edtPhoneNumber.getText().toString());
            update.put("email", edtEmail.getText().toString());
            update.put("password", edtPassword.getText().toString());

            user.child(AccessKey).updateChildren(update)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText( AdminShipper.this,"Updated User successfully.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(AdminShipper.this, AdminShipper.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText( AdminShipper.this,"Updated User " + e, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AdminShipper.this, AdminHistory.class));
                }
            });

        }
        else{
            Toast.makeText(AdminShipper.this,"Please make sure to fill all the information.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showRemoveDialog(final String key) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        user.child(key).removeValue();
                        Toast.makeText(AdminShipper.this, "Shipper has been deleted.F", Toast.LENGTH_SHORT).show();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(AdminShipper.this);
        builder.setMessage("Delete shipper?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.admin_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_client) {
            startActivity(new Intent(AdminShipper.this, admin_main.class));
        } else if (id == R.id.nav_vendor) {
            startActivity(new Intent(AdminShipper.this, AdminVendor.class));
        } else if (id == R.id.nav_shipper) {
            startActivity(new Intent(AdminShipper.this, AdminShipper.class));
        } else if (id == R.id.nav_history) {
            startActivity(new Intent(AdminShipper.this, AdminHistory.class));
        } else if (id == R.id.nav_Logout) {
            Intent SignOut = new Intent(AdminShipper.this, MainActivity.class);
            SignOut.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            SharedPreferences settings = getSharedPreferences("UserInfo", 0);
            settings.edit().clear().commit();
            startActivity(SignOut);
            Toast.makeText(AdminShipper.this, "Sign out successfully.", Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
