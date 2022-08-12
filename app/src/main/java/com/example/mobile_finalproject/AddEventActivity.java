package com.example.mobile_finalproject;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mobile_finalproject.Models.Event;
import com.example.mobile_finalproject.Models.Host;
import com.example.mobile_finalproject.login_registration.RegisterHostActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AddEventActivity extends AppCompatActivity {

    String hostemail;
    Context context;
    ImageView v;
    private FirebaseAuth mAuth;
    private EditText editTextEventName, editTextAddress, editTextDes, editTextMax, editTextMin, editTextStart, editTextEnd, editTextCap, editTextCost;
    ActivityResultLauncher<String> takephoto;
    Intent intent;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;
    private Uri filePath;
    FirebaseStorage storage;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            hostemail = extras.getString("hostemail");
            System.out.println(hostemail + "fffffffffffffffffffffffffffffff");
        }

        //Notification check code
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("My notification", "My notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

        }

        Button b = findViewById(R.id.add_event_button);
        b.setOnClickListener(v -> {
            registerNewEvent(hostemail);
        });


        v = findViewById(R.id.imageView_event_poster);
        Button b1 = findViewById(R.id.textView_upload_image_text);
        b1.setOnClickListener(v -> {
            checkAndRequestPermissions(AddEventActivity.this);
            chooseImage(AddEventActivity.this);
            context = AddEventActivity.this;
            //openSomeActivityForResult();

        });
    }

    // Helper method to register new hosts in database
    private void registerNewEvent(String hostemail) {

        editTextEventName = findViewById(R.id.event_name1);
        editTextAddress = findViewById(R.id.event_address1);
        editTextDes = findViewById(R.id.event_description1);
        editTextMax = findViewById(R.id.event_max1);
        editTextMin = findViewById(R.id.event_min1);
        editTextStart = findViewById(R.id.event_start1);
        editTextEnd = findViewById(R.id.event_end1);
        editTextCap = findViewById(R.id.event_capacity1);
        editTextCost = findViewById(R.id.event_cost1);


        String event_Name = editTextEventName.getText().toString().trim();
        String event_Address = editTextAddress.getText().toString().trim();
        String event_description = editTextDes.getText().toString().trim();
        int event_max = Integer.parseInt(String.valueOf(editTextMax.getText()));
        int event_min = Integer.parseInt(String.valueOf(editTextMin.getText()));
        String event_start = editTextStart.getText().toString().trim();
        String event_end = editTextEnd.getText().toString().trim();
        int event_cap = Integer.parseInt(String.valueOf(editTextCap.getText()));
        int event_cost = Integer.parseInt(String.valueOf(editTextCost.getText()));


        String typeOfUser = "Host User";


        if (event_Name.isEmpty()) {
            editTextEventName.setError("Event Name is Required");
            editTextEventName.requestFocus();
            return;
        }
        if (event_Address.isEmpty()) {
            editTextAddress.setError("Event Address is Required");
            editTextAddress.requestFocus();
            return;
        }
        if (event_description.isEmpty()) {
            editTextDes.setError("Event Description is Required");
            editTextDes.requestFocus();
            return;
        }
        if (Integer.toString(event_max).isEmpty()) {
            editTextMax.setError("Event Max age is Required");
            editTextMax.requestFocus();
            return;
        }
        if (Integer.toString(event_min).isEmpty()) {
            editTextMin.setError("Event Min age is Required");
            editTextMin.requestFocus();
            return;
        }
        if (Integer.toString(event_cap).isEmpty()) {
            editTextCap.setError("Event Capacity is Required");
            editTextCap.requestFocus();
            return;
        }
        if (Integer.toString(event_cost).isEmpty()) {
            editTextCost.setError("Event Cost is Required");
            editTextCost.requestFocus();
            return;
        }
        if (event_start.isEmpty()) {
            editTextStart.setError("Event Start date is Required");
            editTextStart.requestFocus();
            return;
        }
        else {
            if (!event_start.matches("^(1[0-9]|0[1-9]|3[0-1]|2[1-9])/(0[1-9]|1[0-2])/[0-9]{4}$")){
                editTextStart.setError("Event Start date format is wrong");
                editTextStart.requestFocus();
                return;
            }
            SimpleDateFormat format=new SimpleDateFormat("dd/MM/yyyy");
            try {
                format.parse(event_start);
            }catch (ParseException e){
                editTextStart.setError("Event Start date format is wrong");
                editTextStart.requestFocus();
                return;
            }
        }
        if (event_end.isEmpty()) {
            editTextEnd.setError("Event End date is Required");
            editTextEnd.requestFocus();
            return;
        }
        else {
            if (!event_end.matches("^(1[0-9]|0[1-9]|3[0-1]|2[1-9])/(0[1-9]|1[0-2])/[0-9]{4}$")){
                editTextEnd.setError("Event Start date format is wrong");
                editTextEnd.requestFocus();
                return;
            }
            SimpleDateFormat format=new SimpleDateFormat("dd/MM/yyyy");
            try {
                format.parse(event_end);
            }catch (ParseException e){
                editTextEnd.setError("Event Start date format is wrong");
                editTextEnd.requestFocus();
                return;
            }
        }

        FirebaseDatabase fireBasedatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRefFireBase = fireBasedatabase.getReferenceFromUrl("https://mobile-finalproject-17b4f-default-rtdb.firebaseio.com/");

        System.out.println("event_start" + event_start + "event_end" + event_end);
        Event event = new Event(hostemail, event_Name, event_Address, event_description, event_start, event_end, event_cost, event_cap, event_min, event_max);
        myRefFireBase.child("Events").push().setValue(event);

        uploadImage(event.getEventId());

        Toast.makeText(AddEventActivity.this,
                "Event has been Registered Successfully!",
                Toast.LENGTH_LONG).show();

        //Sends notification whenever new event is added
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"My notification");
        builder.setContentTitle("New event added!");
        builder.setContentText("Check out the new event which is added!");
        builder.setSmallIcon(R.drawable.small_logo);
        builder.setAutoCancel(true);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(AddEventActivity.this);
        managerCompat.notify(1, builder.build());


        // Redirects the Host to the main events page for hosts
        Intent intent = new Intent(AddEventActivity.this, HostMainActivity.class);
        intent.putExtra("hostemail", hostemail);
        startActivity(intent);
    }




    // UploadImage method
    private void uploadImage(String eventid)
    {
        if (filePath != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref
                    = storageReference
                    .child(
                            "Images/"
                                    + eventid);

            // adding listeners on upload
            // or failure of image
            ref.putFile(filePath)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {

                                    // Image uploaded successfully
                                    // Dismiss dialog
                                    progressDialog.dismiss();
                                    Toast
                                            .makeText(AddEventActivity.this,
                                                    "Image Uploaded!!",
                                                    Toast.LENGTH_SHORT)
                                            .show();
                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {

                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast
                                    .makeText(AddEventActivity.this,
                                            "Failed " + e.getMessage(),
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage(
                                            "Uploaded "
                                                    + (int)progress + "%");
                                }
                            });
        }
    }



    public void chooseImage(Context context){


        final CharSequence[] optionsMenu = {"Take Photo", "Choose from Gallery", "Exit" }; // create a menuOption Array
        // create a dialog for showing the optionsMenu
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // set the items in builder
        builder.setItems(optionsMenu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(optionsMenu[i].equals("Take Photo")){
                    // Open the camera and get the photo
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    System.out.println("received");
                    startActivityForResult(takePicture, 0);
                    //someActivityResultLauncher.launch(takePicture);
                }
                else if(optionsMenu[i].equals("Choose from Gallery")){
                    // choose from  external storage
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    System.out.println("received");
                    startActivityForResult(pickPhoto , 1);
                    //someActivityResultLauncher.launch(pickPhoto);
                }
                else if (optionsMenu[i].equals("Exit")) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        filePath = data.getData();
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        v.setImageBitmap(selectedImage);
                    }
                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();
                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
                                v.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                                cursor.close();
                            }
                        }
                    }
                    break;
            }
        }
    }


    //check permissions
    public static boolean checkAndRequestPermissions(final Activity context) {
        int WExtstorePermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int cameraPermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (WExtstorePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded
                    .add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(context, listPermissionsNeeded
                            .toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }


    // Handled permission Result
    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS:
                if (ContextCompat.checkSelfPermission(AddEventActivity.this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(),
                                    "FlagUp Requires Access to Camara.", Toast.LENGTH_SHORT)
                            .show();
                } else if (ContextCompat.checkSelfPermission(AddEventActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(),
                            "FlagUp Requires Access to Your Storage.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    chooseImage(AddEventActivity.this);
                }
                break;
        }
    }
}
