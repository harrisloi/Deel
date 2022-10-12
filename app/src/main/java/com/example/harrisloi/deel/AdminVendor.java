package com.example.harrisloi.deel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.harrisloi.deel.Model.User;
import com.example.harrisloi.deel.ViewHolder.ShipperViewHolder;
import com.example.harrisloi.deel.ViewHolder.VendorViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminVendor extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    Uri saveUri;
    private final int PICK_IMAGE_REQUEST = 71;

    FirebaseDatabase database;
    DatabaseReference user;

    FirebaseRecyclerAdapter<User, VendorViewHolder> adapter;

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_vendor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Init Firebase
        database = FirebaseDatabase.getInstance();
        user = database.getReference("User");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminVendor.this, AdminCreateVendor.class));
            }
        });

        recyclerView = (RecyclerView)findViewById(R.id.recycle_users);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //load Client profile
        loadVendor();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void loadVendor() {
        adapter = new FirebaseRecyclerAdapter<User, VendorViewHolder>(User.class, R.layout.vendor_layout, VendorViewHolder.class, user.orderByChild("role").equalTo("vendor") ) {
            @Override
            protected void populateViewHolder(VendorViewHolder viewHolder,final User model,final int position) {
                viewHolder.vendorName.setText(model.getFirstName()+" "+ model.getLastName());
                viewHolder.vendorEmail.setText(model.getEmail());
                viewHolder.vendorPhone.setText(model.getPhoneNumber());

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
            }
        };
        recyclerView.setAdapter(adapter);
    }

    private void showRemoveDialog(String key) {
    }

    private void showEditDialog(String key, User model) {
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
            startActivity(new Intent(AdminVendor.this, admin_main.class));
        } else if (id == R.id.nav_vendor) {
            startActivity(new Intent(AdminVendor.this, AdminVendor.class));
        } else if (id == R.id.nav_shipper) {
            startActivity(new Intent(AdminVendor.this, AdminShipper.class));
        } else if (id == R.id.nav_history) {
            startActivity(new Intent(AdminVendor.this, AdminHistory.class));
        } else if (id == R.id.nav_Logout) {
            Intent SignOut = new Intent(AdminVendor.this, MainActivity.class);
            SignOut.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            SharedPreferences settings = getSharedPreferences("UserInfo", 0);
            settings.edit().clear().commit();
            startActivity(SignOut);
            Toast.makeText(AdminVendor.this, "Sign out successfully.", Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
