package com.example.winmenia;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;

public class complentpage extends AppCompatActivity {

    EditText ed1,ed2,ed3;

    FirebaseFirestore  db;
    Button btn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complentpage);


        ed1=findViewById(R.id.c1);
        ed2=findViewById(R.id.c2);
        ed3=findViewById(R.id.c3);
        btn=findViewById(R.id.button);
         db=FirebaseFirestore.getInstance();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String t1 = ed1.getText().toString();
                String t2 = ed2.getText().toString();
                String t3 = ed3.getText().toString();

                if (t1.isEmpty() || t2.isEmpty()||t3.isEmpty()){
                    Toast.makeText(complentpage.this, "Please Fill all Fileds", Toast.LENGTH_SHORT).show();
                }
                else {
                    Map<String, String> v2 = new HashMap<>();
                    v2.put("Subject", ed1.getText().toString());
                    v2.put("Discription", ed2.getText().toString());
                    v2.put("mobile", ed3.getText().toString());

                    db.collection("Complents").add(v2)
                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(Task<DocumentReference> task) {
                                    Toast.makeText(complentpage.this, "Request Submited", Toast.LENGTH_SHORT).show();

                                    ed1.setText("");
                                    ed2.setText("");
                                    ed3.setText("");
                                }
                            });
                }
            }
        });
    }
}