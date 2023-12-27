package com.example.winmenia;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class wallet extends AppCompatActivity {
    TextView t1, t2, t3, t4;
    FirebaseAuth auth;
    FirebaseUser user;

    FirebaseFirestore db;

    private double currentBalance = 0.0;
    private DocumentReference walletRef;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        t1 = findViewById(R.id.userid123454);
        t2 = findViewById(R.id.bal);
        t3 = findViewById(R.id.rechargeamount);
        t4 = findViewById(R.id.withdrawamount);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        assert user != null;
        t1.setText(user.getPhoneNumber());

        db = FirebaseFirestore.getInstance();
        String id = t1.getText().toString();

        walletRef = db.collection("wallet").document(id);

        walletRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                currentBalance = documentSnapshot.getDouble("RechargeAmount");
                updateBalanceText();
                showrecharge();
                showrecharge1();            }
        });

    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void updateBalanceText() {
        t2.setText("Available Balance : ₹" + String.format("%.2f", currentBalance));
    }

    private void showrecharge() {
        String id = t1.getText().toString();
        db.collection("Withdraw")
                .whereEqualTo("Userid", id)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            String data = doc.getString("Userid");
                            if (data.contains(id)) {
                                t4.append("" + doc.getString("Userid") + "\n" + doc.getString("Amount") +"\n");
                            } else {
                                t4.setText("No Data Found");
                            }
                        }

                    }
                });


    }

    private void showrecharge1() {
        String id = t1.getText().toString();
        db.collection("Recharge")
                .whereEqualTo("userid", id)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            String data = doc.getString("userid");
                            if (data.contains(id)) {
                                t3.append("" + doc.getString("userid") + "\n" + doc.getString("Amount") + "\n" + doc.getString("RefrenceNumber") + "\n");
                            } else {
                                t3.setText("No Data Found");
                            }
                        }

                    }
                });

    }

}