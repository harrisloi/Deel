package com.example.harrisloi.deel.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.RelativeLayout;

import com.example.harrisloi.deel.Common.Common;
import com.example.harrisloi.deel.Model.Request;
import com.example.harrisloi.deel.OrderStatus;
import com.example.harrisloi.deel.OrderStatusVendor;
import com.example.harrisloi.deel.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class ListenOrderVendor extends Service implements ChildEventListener {

    FirebaseDatabase database;
    DatabaseReference orders;

    public ListenOrderVendor() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        database = FirebaseDatabase.getInstance();
        orders = database.getReference("Requests");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        orders.addChildEventListener(this);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        //Trigger here
        Request request = dataSnapshot.getValue(Request.class);
        //Get user username
        SharedPreferences shared = getSharedPreferences("UserInfo", 0);
        String username = (shared.getString("Username", ""));
        if(request.getOwner().equals(username)){
            if(request.getStatus().equals("0"))
                showNotification(dataSnapshot.getKey(), request);
        }
    }

    private void showNotification(String key, Request request) {
        Intent intent = new Intent(getBaseContext(), OrderStatusVendor.class);

        PendingIntent contentIntent = PendingIntent.getActivity(getBaseContext(), 0 , intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext());

        builder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setTicker("Harris")
                .setContentInfo("New Order")
                .setContentText("Your Have New Order #"+key)
                .setContentIntent(contentIntent)
                .setSmallIcon(R.mipmap.ic_launcher);

        NotificationManager notificationManager = (NotificationManager)getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);

        int randomInt = new Random().nextInt(9999-1)+1;
        notificationManager.notify(randomInt, builder.build());
    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}
