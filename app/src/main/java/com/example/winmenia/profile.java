package com.example.winmenia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class profile extends AppCompatActivity {
    CardView c1,c2,c3,c4,c5,c6,c7,c8;
    FirebaseAuth auth;
    FirebaseUser user;
    Button button;
    private double currentBalance = 0.0;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference walletRef;
TextView textView,textView1;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        c1=findViewById(R.id.card1);////////sigin
        c2=findViewById(R.id.card2);//////////order
        c3=findViewById(R.id.card3);/////////Refrel Code
        c4=findViewById(R.id.card4);///////Wallet
        c5=findViewById(R.id.card5);/////////Banking
        c6=findViewById(R.id.card6);/////////Withdraw
        c7=findViewById(R.id.card7);///////Recharge
        c8=findViewById(R.id.card8);//////////Ask ?
        textView=findViewById(R.id.userid1);
        textView1=findViewById(R.id.avb);
        button=findViewById(R.id.logoutbtn);

        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        String  user1 ="";

      //  user.getPhoneNumber();
        textView.setText(user.getPhoneNumber());
        String id = textView.getText().toString();

        walletRef = db.collection("wallet").document(id);

        walletRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                currentBalance = documentSnapshot.getDouble("RechargeAmount");
                updateBalanceText();

            }
        });

        c1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        c2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        c3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        c4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(profile.this,wallet.class);
                startActivity(intent);


            }
        });
        c5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(profile.this,banking.class);
                startActivity(intent);


            }
        });
        c6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(profile.this,withdrawpage.class);
                startActivity(intent);


            }
        });
        c7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(profile.this,payment.class);
                startActivity(intent);

            }
        });
        c8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(profile.this,complentpage.class);
                startActivity(intent);


            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                Intent intent = new Intent(profile.this,register.class);
                startActivity(intent);
                finish();
                Toast.makeText(profile.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updateBalanceText() {
            textView1.setText("Available Balance : ₹" + String.format("%.2f", currentBalance));
        }
}