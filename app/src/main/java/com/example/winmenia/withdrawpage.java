package com.example.winmenia;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;

public class withdrawpage extends AppCompatActivity {


    Button button1, button2, button3, button4, button5, button6, button7;
    EditText editText;
    TextView textView1, textView2;
    FirebaseAuth auth;
    FirebaseUser user;


    private double currentBalance = 0.0;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference walletRef;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdrawpage);


        button1 = findViewById(R.id.b1);
        button2 = findViewById(R.id.b2);
        button3 = findViewById(R.id.b3);
        button4 = findViewById(R.id.b4);
        button5 = findViewById(R.id.b5);
        button6 = findViewById(R.id.b6);
        button7 = findViewById(R.id.wbtn);
        editText = findViewById(R.id.wamount);
        textView1 = findViewById(R.id.wuserid);
        textView2 = findViewById(R.id.wavailbalance);

        editText.setEnabled(false);

        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        assert user != null;
        String us=user.getPhoneNumber();

        textView1.setText(user.getPhoneNumber());

        walletRef = db.collection("wallet").document(us);


        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("500");
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("1000");
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("2000");
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("5000");
            }
        });
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("10000");
            }
        });
        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("20000");
            }
        });




        walletRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                currentBalance = documentSnapshot.getDouble("RechargeAmount");
                updateBalanceText();
                //minimunwithdraw();
            }
        });

        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amountStr = editText.getText().toString().trim();
                if (!amountStr.isEmpty()) {
                    double amount = Double.parseDouble(amountStr);
                    currentBalance -= amount;
                    updateBalanceInFirestore();
                    amountwithdrawlist();
                }
            }
        });
    }


    private void amountwithdrawlist() {

        String amountStr = editText.getText().toString().trim();

        Map<String, String> v2 = new HashMap<>();
        v2.put("Userid", textView1.getText().toString());
        v2.put("Amount", String.valueOf(Double.valueOf(amountStr)));

        FirebaseFirestore.getInstance().collection("Withdraw").add(v2).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                Toast.makeText(withdrawpage.this, "Request Submited Successfully", Toast.LENGTH_SHORT).show();
            }

        });
    }
    private void updateBalanceText() {
        textView2.setText("Available Balance : ₹" + String.format("%.2f", currentBalance));
        if (currentBalance<500){
            button1.setEnabled(false);
            button2.setEnabled(false);
            button3.setEnabled(false);
            button4.setEnabled(false);
            button5.setEnabled(false);
            button6.setEnabled(false);
            button7.setEnabled(false);

        }else {
            button1.setEnabled(true);
            button2.setEnabled(true);
            button3.setEnabled(true);
            button4.setEnabled(true);
            button5.setEnabled(true);
            button6.setEnabled(true);
            button7.setEnabled(true);
        }

    }
    private void updateBalanceInFirestore() {
        walletRef.update("RechargeAmount", currentBalance)
                .addOnSuccessListener(aVoid -> {
                    updateBalanceText();
                })
                .addOnFailureListener(e -> {
                    // Handle error if updating balance fails
                });
    }
}