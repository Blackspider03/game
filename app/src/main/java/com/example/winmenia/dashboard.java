package com.example.winmenia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class dashboard extends AppCompatActivity {
    CardView c1,c2;
    TextView t1,t2;
    Button b1,b2;
    private ClipboardManager myClipboard;
    private ClipData myClip;
    private FirebaseFirestore db;
    private FirebaseDatabase database;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        c1=findViewById(R.id.card2);
        c2=findViewById(R.id.card3);
        t1=findViewById(R.id.c1text);
        t2=findViewById(R.id.c2text);
        b1=findViewById(R.id.c1btn);
        b2=findViewById(R.id.c2btn);

        myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);



        db=FirebaseFirestore.getInstance();
        updateinstagram();
        updatetelegram();


        c1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(dashboard.this,redgreen.class);
                startActivity(intent);
            }
        });
        c2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(dashboard.this,profile.class);
                startActivity(intent);
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text;
                text = t1.getText().toString();
                myClip = ClipData.newPlainText("text", text);
                myClipboard.setPrimaryClip(myClip);
                Toast.makeText(getApplicationContext(), "Instagram Link Copied",
                        Toast.LENGTH_SHORT).show();


            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text;
                text = t2.getText().toString();
                myClip = ClipData.newPlainText("text", text);
                myClipboard.setPrimaryClip(myClip);
                Toast.makeText(getApplicationContext(), "Telegram Link Copied",
                        Toast.LENGTH_SHORT).show();

            }
        });


    }




    private void updateinstagram() {
        db.collection("INSTAGRAM")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots){
                            t1.append(""+doc.getString("id"));
                        }
                    }
                });
    }
    private void updatetelegram() {
        db.collection("TELEGRAM")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots){
                            t2.append(""+doc.getString("id"));
                        }
                    }
                });
    }
}