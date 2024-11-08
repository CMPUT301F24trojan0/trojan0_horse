package com.example.trojan0project;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;


import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class EventActivity extends AppCompatActivity implements DeleteEventFragment.DeleteEventDialogListener {

    private ListView eventAdminList; //create reference to the Listview
    private ArrayAdapter<Event> eventAdminAdapter;
    public ArrayList<Event> dataList;
    private Event selectedEvent = null;
    private FirebaseFirestore db;
    private ImageView qrCodeImageView;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.events_main);
        db = FirebaseFirestore.getInstance();

        final CollectionReference collectionReference = db.collection("events");


        String []events = {"Beginners Swimming","Golfing", "Baking classes", "Picnic" }; //string array consisting of events which can be fed into ListView
        //int[] qrImages = {R.drawable.qr_code, R.drawable.qr_code,
                //R.drawable.qr_code, R.drawable.qr_code}; // array conssiting of all the diff qr codes
        dataList = new ArrayList<Event>(); // ArrayList which will contain the data (string array of events)
        //for (int i = 0; i < events.length; i++) {
            //dataList.add(new Event(events[i],qrImages[i]));

        //}

        //dataList.addAll(Arrays.asList(events)); // add the data in string array to dataList
        eventAdminList = findViewById(R.id.admin_events_list); //find reference to to the ListView and assign it to eventAdminList
        eventAdminAdapter = new EventArrayAdapter(this, dataList); // link content file and  and datalist as well as pass id of textview in content.xml
        eventAdminList.setAdapter(eventAdminAdapter); // show each TextView in scrolling list form

        // Listener for Firestore data
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots,@Nullable  FirebaseFirestoreException error) {


                dataList.clear(); // Clear the existing data

                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    String eventName = (String) doc.getData().get("name");
                    String qrContent = (String) doc.getData().get("qrContent"); // assuming we are using a url for the qrcode


                    if (qrContent != null) {
                        Bitmap qrCodeBitmap = generateQRCode(qrContent);
                        dataList.add(new Event(eventName, qrCodeBitmap));
                    } else {
                        dataList.add(new Event(eventName, null));
                    }

                }

                eventAdminAdapter.notifyDataSetChanged();


            }
        });

        eventAdminList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedEvent = dataList.get(i);

                //OpenAI, (2024, October 26), "How do I create a dialog where i can delete the selected event?", ChatGPT
                DeleteEventFragment fragment = DeleteEventFragment.newInstance(selectedEvent); //creates a new instance of DeleteEventragment which is selectedEvent(this pops up the screen for udeleting the evnts)
                fragment.show(getSupportFragmentManager(), "Delete Event");

            }
        });

    }

    private Bitmap generateQRCode(String content) {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 500, 500);
            Bitmap bmp = Bitmap.createBitmap(500, 500, Bitmap.Config.RGB_565);
            for (int x = 0; x < 500; x++) {
                for (int y = 0; y < 500; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? android.graphics.Color.BLACK : android.graphics.Color.WHITE);
                }
            }
            return bmp;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    private byte[] getQRCodeImageData(Bitmap qrCodeBitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        qrCodeBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    //NEW

    @Override
    public void deleteQRCode(Event event) {
        //selectedEvent.removeQRCode();
        if (selectedEvent != null) { //city is not null so that means the user clicked on an existing city
            //facilityAdminAdapter.remove(selectedFacility);
            //facilityAdminAdapter.notifyDataSetChanged();
            db.collection("events")
                    .whereEqualTo("name", selectedEvent.getEventName())
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {

                            db.collection("events").document(document.getId())
                                    .update("qrContent", null)  // Set qrContent to null (or delete)
                                    .addOnSuccessListener(aVoid -> {
                                        // Notify the adapter that the data has been updated
                                        eventAdminAdapter.notifyDataSetChanged();
                                        Toast.makeText(this, "QR code deleted", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "QR code not deleted", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    });


        }


    }

    @Override
    public void deleteEvent(Event event) {
        if (selectedEvent != null) { //city is not null so that means the user clicked on an existing city
            //eventAdminAdapter.remove(selectedEvent);
            //eventAdminAdapter.notifyDataSetChanged();
            //delete from firestore db
            db.collection("events")
                    .whereEqualTo("name", selectedEvent.getEventName())
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots){
                            db.collection("events").document(document.getId()).delete()
                                    .addOnSuccessListener(Void ->{
                                        dataList.remove(selectedEvent);
                                        eventAdminAdapter.notifyDataSetChanged();
                                        Toast.makeText(this, "Event is deleted", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e ->
                                            Toast.makeText(this, "Event not deleted", Toast.LENGTH_SHORT).show());

                        }
                    })
                    .addOnFailureListener(e ->{
                        Toast.makeText(this, "Event not deleted", Toast.LENGTH_SHORT).show();
                    });


        }


    }



}
