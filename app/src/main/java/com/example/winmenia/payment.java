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

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class payment extends AppCompatActivity {
    Button b1,b2,b3,b4,b5,b6,rebtn;
    EditText editText;
    private double currentBalance = 0.0;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference walletRef = db.collection("wallet").document("24JRzD2gGU57btPzw0Yt");

        TextView textView;
    private static String value;

    public static String getValue() {
        return value;
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        b1=findViewById(R.id.btn1);
        b2=findViewById(R.id.btn2);
        b3=findViewById(R.id.btn3);
        b4=findViewById(R.id.btn4);
        b5=findViewById(R.id.btn5);
        b6=findViewById(R.id.btn6);
        rebtn=findViewById(R.id.rechargebbtn);
        textView = findViewById(R.id.reamingbalance);

        editText=findViewById(R.id.manupayment);

        editText.setEnabled(false);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("500.00");
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("1000.00");
            }
        });
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("3000.00");
            }
        });
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("5000.00");
            }
        });
        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("10000.00");
            }
        });
        b6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("20000.00");
            }
        });



        rebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String txt = editText.getText().toString();

                if (txt.isEmpty()){
                    Toast.makeText(payment.this, "Please Fill the Amount", Toast.LENGTH_SHORT).show();
                }
                else {
                    value = editText.getText().toString().trim();
                    Intent ie = new Intent(payment.this,finalpayment.class);
                    startActivity(ie);
                    finish();


                }
            }
        });

        walletRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                currentBalance = documentSnapshot.getDouble("RechargeAmount");
                updateBalanceText();
            }
        });

    }

    private void updateBalanceText() {
        textView.setText("Available Balance : ₹" + String.format("%.2f", currentBalance));

    }
}