package com.example.allthingsgaucho;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference noteRef = db.document("Riders/My First Note");

    private static final String DROP_KEY = "Dropoff Location";
    private static final String PICK_KEY ="Pickup Location" ;
    private static final String TAG = "Rider";

    Spinner pickSpinner;
    Spinner dropSpinner;
    String pickText;
    String dropText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pickSpinner = (Spinner) findViewById(R.id.spinnerPick);

        ArrayAdapter<String> pickAdapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.pickname));
        pickAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pickSpinner.setAdapter(pickAdapter);
        pickSpinner.setOnItemSelectedListener(this);

        dropSpinner = (Spinner) findViewById(R.id.spinnerDrop);

        ArrayAdapter<String> dropAdapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.dropname));
        dropAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropSpinner.setAdapter(dropAdapter);
        dropSpinner.setOnItemSelectedListener(this);
    }

    public void saveRider(View view) {

        pickText =  pickSpinner.getSelectedItem().toString();
        dropText = dropSpinner.getSelectedItem().toString();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            if (dropText.isEmpty() || pickText.isEmpty() ) {
                return;
            }
        }
        Map<String, Object> dataToSave = new HashMap<String, Object>();
        dataToSave.put(DROP_KEY, dropText);
        dataToSave.put(PICK_KEY, pickText);


        db.collection("Riders").add(dataToSave).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(MainActivity.this, "Details added to Cloud", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String error = e.getMessage();
                Toast.makeText(MainActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();

            }
        });

    }

    public void showRider(View view)
    {
        db.collection("Riders")
                .whereEqualTo(PICK_KEY,pickText)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String drop = document.getString(DROP_KEY);
                                String pick = document.getString(PICK_KEY);

                                Log.d(TAG, "Pick: " + pick + "\n" + "Drop: " + drop);

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(parent.getContext(),

                "OnItemSelectedListener : " + parent.getItemAtPosition(position).toString(),

                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}