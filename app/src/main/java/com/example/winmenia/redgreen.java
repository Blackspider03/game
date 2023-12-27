package com.example.winmenia;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class redgreen extends AppCompatActivity {
    private EditText amountEditText;
    private Button creditButton;
    private Button debitButton, button1, button2;
    private TextView balanceTextView, textView, textView1, textView2, textView3, textView4;

    FirebaseAuth auth;
    FirebaseUser user;
    private double currentBalance = 0.0;


    FirebaseDatabase firebaseDatabase;

    // creating a variable for our Database
    // Reference for Firebase.
    DatabaseReference databaseReference;

    private FirebaseFirestore firestore;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference walletRef;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redgreen);

        amountEditText = findViewById(R.id.finalamount);
        creditButton = findViewById(R.id.redbtn);
        debitButton = findViewById(R.id.greenbtn);
        button1 = findViewById(R.id.a1);
        button2 = findViewById(R.id.rechargebtn);

        balanceTextView = findViewById(R.id.fetchbalance);
        textView = findViewById(R.id.userid);
        textView1 = findViewById(R.id.timer);
        textView2 = findViewById(R.id.contid);
        textView3 = findViewById(R.id.textView11);
        //textView4 = findViewById(R.id.textView12);


        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        assert user != null;
        textView.setText(user.getPhoneNumber());
        String id = textView.getText().toString();
        walletRef = db.collection("wallet").document(id);

        firestore = FirebaseFirestore.getInstance();
        amountEditText.setEnabled(false);


        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference(id);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String currentDateandTime = sdf.format(new Date());
        textView2.setText(currentDateandTime);

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(redgreen.this, payment.class);
                startActivity(intent);
                finish();
            }
        });
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amountEditText.setText("100");
            }
        });
        walletRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                currentBalance = documentSnapshot.getDouble("RechargeAmount");
                updateBalanceText();
                timer1();
            }
        });
        creditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amountStr = amountEditText.getText().toString().trim();
                if (!amountStr.isEmpty()) {
                    double amount = Double.parseDouble(amountStr);
                    currentBalance -= amount;
                    updateBalanceInFirestore();
                    addred();
                    //toaddred();

                }
            }
        });
        debitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amountStr = amountEditText.getText().toString().trim();
                if (!amountStr.isEmpty()) {
                    double amount = Double.parseDouble(amountStr);
                    currentBalance -= amount;
                    updateBalanceInFirestore();
                    addgreen();
                   // toaddgreen();
                }
            }
        });
    }



    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void updateBalanceText() {
        balanceTextView.setText("Available Balance : ₹" + String.format("%.2f", currentBalance));
        if (currentBalance<100){
            button1.setEnabled(false);
        }else {
            button1.setEnabled(true);
        }
    }

    private void updateBalanceInFirestore() {
        walletRef.update("RechargeAmount", currentBalance)
                .addOnSuccessListener(aVoid -> {
                    updateBalanceText();
                })
                .addOnFailureListener(e -> {
                });
    }


    ////////////////////Timer 1 to run timer 2
    private void timer1() {
       // long duration = TimeUnit.MINUTES.toMillis(1);

        new CountDownTimer(30000, 1000) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long millisUntilFinished) {
                textView1.setText("" + millisUntilFinished / 1000);
                creditButton.setEnabled(true);
                debitButton.setEnabled(true);
            }
            @SuppressLint("SetTextI18n")
            @Override
            public void onFinish() {
                //textView1.setText("");
                creditButton.setEnabled(false);
                debitButton.setEnabled(false);
                timer2();

            }
        }.start();
    }
    //////////////////Timer to show the result
    private void timer2() {
        // long duration = TimeUnit.MINUTES.toMillis(1);

        new CountDownTimer(30000, 1000) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long millisUntilFinished) {
                textView1.setText("" + millisUntilFinished / 1000);
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFinish() {
                fetchAndCompareCounts();
                timer1();
            }
        }.start();
    }

    /////////////////Compare count in red and green
    private void fetchAndCompareCounts() {
        db.collection("red").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            long collection1Count = task.getResult().size();
                            fetchCollection2Count(collection1Count);
                        } else {
                        }
                    }
                });
    }
    private void fetchCollection2Count(long collection1Count) {
        db.collection("green").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            long collection2Count = task.getResult().size();
                            showResult(collection1Count, collection2Count);
                        } else {
                        }
                    }
                });
    }

    /////////////////after compare calculatin result
    @SuppressLint("SetTextI18n")
    private void showResult(long collection1Count, long collection2Count) {
        if (collection1Count < collection2Count) {
            winner1();
            textView3.setText("Red Is Win");
        }
            else {
            textView3.setText("Green is Winner");
            winner2();
        }
    }


    //////////////Adding the count in firestore Red
    private void addred() {
        String u = textView.getText().toString();
        Map<String, String> v1 = new HashMap<>();
        v1.put("userid", textView.getText().toString());
        v1.put("Amount", amountEditText.getText().toString());
        v1.put("contestid", textView2.getText().toString());
        FirebaseFirestore.getInstance().collection("red").add(v1).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                toaddred();
            }
        });
    }


    //////////////Adding the count in firestore Green
    private void addgreen() {
        String u = textView.getText().toString();

        Map<String, String> v1 = new HashMap<>();
        v1.put("userid", textView.getText().toString());
        v1.put("Amount", amountEditText.getText().toString());
        v1.put("contestid", textView2.getText().toString());

        FirebaseFirestore.getInstance().collection("green").add(v1).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                togreen();
            }
        });
    }

///////////////////adding red entry in realtimedatabase
    private void toaddred() {
        String red = "red";
        String uid = textView2.getText().toString();
        Map<String, String> v1 = new HashMap<>();

        v1.put("userid", textView.getText().toString());
        v1.put("Amount", amountEditText.getText().toString());
        v1.put("contestid", textView2.getText().toString());
        v1.put("color", red.toString());

        databaseReference.child(uid).setValue(v1).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
            }
        });
    }

///////////////////adding green entry in realtimedatabase
    private void togreen() {
        String red = "green";
        Map<String, String> v1 = new HashMap<>();
        String uid = textView2.getText().toString();

        v1.put("userid", textView.getText().toString());
        v1.put("Amount", amountEditText.getText().toString());
        v1.put("contestid", textView2.getText().toString());
        v1.put("color", red.toString());

        databaseReference.child(uid).setValue(v1).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
            }
        });
    }


    /////////////////After Calculation we fetch userid and adding wining amount thir wallet
    private void winner1() {
        String uerdi = textView.getText().toString();
        db.collection("red")
                .whereEqualTo("userid", uerdi)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int count = task.getResult().size();
                            int beat = 180;
                            int total;
                            total = beat * count;
                            String input = new String(String.valueOf(total));
                            if (!input.isEmpty()) {
                                double amount = Double.parseDouble(input);
                                currentBalance += amount;
                                updateBalanceInFirestore1();
                                Deletedcontest1();
                                Deletedcontest();
                                updatebalance1();
                            }

                        }
                        else {
                            //textView3.setText("You Are loss");
                        }
                    }
                });
    }


    private void winner2() {
        String uerdi = textView.getText().toString();
        db.collection("green")
                .whereEqualTo("userid", uerdi)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int count = task.getResult().size();
                            int beat = 180;
                            int total;
                            total = beat * count;
                            String input = new String(String.valueOf(total));
                           // textView3.setText("You Are Win");
                            if (!input.isEmpty()) {
                                double amount = Double.parseDouble(input);
                                currentBalance += amount;
                                updateBalanceInFirestore2();
                                Deletedcontest1();
                                Deletedcontest();

                            }

                        }
                        else {
                           // textView3.setText("You Are loss");
                        }
                    }
                });
    }



    ////////////////////////////updating balance in wallet
    private void updateBalanceInFirestore1() {
        walletRef.update("RechargeAmount", currentBalance)
                .addOnSuccessListener(aVoid -> {
                    Deletedcontest();
                                    })
                .addOnFailureListener(e -> {
                    // Handle error if updating balance fails
                });
    }
    private void updateBalanceInFirestore2() {
        walletRef.update("RechargeAmount", currentBalance)
                .addOnSuccessListener(aVoid -> {
                    Deletedcontest();
                          })
                .addOnFailureListener(e -> {
                    // Handle error if updating balance fails
                });
    }
    /////////////////after over the game delete old entry's
    private void Deletedcontest1() {
        String ndb =textView.getText().toString();
        CollectionReference collectionRef = db.collection("green"); // Replace with your actual collection name
        collectionRef.whereEqualTo("userid", ndb)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> documentIdsToDelete = new ArrayList<>();
                            for (DocumentSnapshot document : task.getResult()) {
                                documentIdsToDelete.add(document.getId());
                            }
                            for (String docId : documentIdsToDelete) {
                                collectionRef.document(docId).delete();
                            }
                        } else {
                            // Handle errors
                        }
                    }
                });
        }

    private void Deletedcontest() {
        String ndb =textView.getText().toString();
        CollectionReference collectionRef = db.collection("red"); // Replace with your actual collection name
        collectionRef.whereEqualTo("userid", ndb)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> documentIdsToDelete = new ArrayList<>();
                            for (DocumentSnapshot document : task.getResult()) {
                                documentIdsToDelete.add(document.getId());
                            }
                            for (String docId : documentIdsToDelete) {
                                collectionRef.document(docId).delete();
                            }
                        } else {
                            // Handle errors
                        }
                    }
                });

        }
    private void updatebalance1() {
        balanceTextView.setText("Available Balance : ₹" + String.format("%.2f", currentBalance));

    }

}




/////////////////////////This code is developed by  ROHAN BHAGWAT(BLACKSPIDER)///////////////////////










