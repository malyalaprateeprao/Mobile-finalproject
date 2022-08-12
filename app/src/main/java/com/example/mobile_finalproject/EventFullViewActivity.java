package com.example.mobile_finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mobile_finalproject.Models.ExampleItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class EventFullViewActivity extends AppCompatActivity {

    String eventId;
    private StorageReference mStorageStickerReference1;
    private TextView editTextEventName, editTextAddress, editTextDes, editTextMax, editTextMin, editTextStart, editTextEnd, editTextCap, editTextCost;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_full_view);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            eventId = extras.getString("eventId");
            System.out.println(eventId + "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
        }

        editTextEventName = findViewById(R.id.event_name1);
        editTextAddress = findViewById(R.id.event_address1);
        editTextDes = findViewById(R.id.event_description1);
        editTextMax = findViewById(R.id.event_max1);
        editTextMin = findViewById(R.id.event_min1);
        editTextStart = findViewById(R.id.event_start1);
        editTextEnd = findViewById(R.id.event_end1);
        editTextCap = findViewById(R.id.event_capacity1);
        editTextCost = findViewById(R.id.event_cost1);
        imageView = findViewById(R.id.imageView_event_poster);


        FirebaseDatabase.getInstance().getReference("Events").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // Iterate over all the users(key) in the child users in the db
                for (DataSnapshot userValue : snapshot.getChildren()) {

                    if(userValue.getValue() != null &&
                            userValue.child("eventId").getValue().toString().equals(eventId)) {
                        System.out.println("rao1" + userValue);
                        String name = userValue.child("eventName").getValue().toString();
                        String description = userValue.child("eventDescription").getValue().toString();
                        String eventId = userValue.child("eventId").getValue().toString();

                        System.out.println(name + " " + description + " " + eventId);

                        editTextEventName.setText(userValue.child("eventName").getValue().toString());
                        editTextAddress.setText(userValue.child("eventAddress").getValue().toString());
                        editTextDes.setText(userValue.child("eventDescription").getValue().toString());
                        editTextMax.setText(userValue.child("maxAgelimit").getValue().toString());
                        editTextMin.setText(userValue.child("minAgelimit").getValue().toString());
                        editTextStart.setText(userValue.child("eventStartDate").getValue().toString());
                        editTextEnd.setText(userValue.child("eventEndDate").getValue().toString());
                        editTextCap.setText(userValue.child("eventUsersMaxCapacity").getValue().toString());
                        editTextCost.setText(userValue.child("eventTicketCost").getValue().toString());

                        mStorageStickerReference1 = FirebaseStorage.getInstance().getReference().child("Images/" + eventId);
                        if(mStorageStickerReference1==null){ continue;}
                        File localFileSticker1 = null;
                        try {
                            localFileSticker1 = File.createTempFile("sticker1", "jpg");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        File finalLocalFileSticker = localFileSticker1;
                        mStorageStickerReference1.getFile(localFileSticker1)
                                .addOnSuccessListener(taskSnapshot -> {
                                    Bitmap bitmap1 = BitmapFactory.decodeFile(finalLocalFileSticker.getAbsolutePath());
                                    imageView.setImageBitmap(bitmap1);
                                });

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        Button button = findViewById(R.id.editbutton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent(EventFullViewActivity.this, EditEventActivity.class);
                intent.putExtra("eventId", eventId);
                startActivity(intent);
            }
        });


        Button button1 = findViewById(R.id.registered_users);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent(EventFullViewActivity.this, RegisteredUserOfEventActivity.class);
                intent.putExtra("eventId", eventId);
                startActivity(intent);
            }
        });

    }
}