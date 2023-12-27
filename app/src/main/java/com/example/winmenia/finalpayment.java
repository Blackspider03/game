package com.example.winmenia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class finalpayment extends AppCompatActivity {

    TextView textView,textView1,textView2,textView3;
    Button button,btn;
    private ClipboardManager myClipboard;
    private ClipData myClip;
    private double currentBalance = 0.0;
    FirebaseAuth auth;
    FirebaseUser user;

    EditText editText;
    String useriid="";

    private CollectionReference usersCollection;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference walletRef;
    AlertDialog.Builder builder;
    LinearLayout linearLayout;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finalpayment);

        textView = findViewById(R.id.amount);
        textView1 = findViewById(R.id.upitext);
        textView2 = findViewById(R.id.showaccount);
        button = findViewById(R.id.copyupi);
        btn = findViewById(R.id.refbtn);
        editText = findViewById(R.id.reffiled);
        String useriid = textView2.getText().toString();
        textView.setText(payment.getValue());
        textView3=findViewById(R.id.timetext);
        linearLayout=findViewById(R.id.line);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        assert user != null;
        textView2.setText(user.getPhoneNumber());
        String userd = textView2.getText().toString();

        updateupi();

        walletRef = db.collection("wallet").document(userd);

         builder = new AlertDialog.Builder(this);

        myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text;
                text = textView1.getText().toString();
                myClip = ClipData.newPlainText("text", text);
                myClipboard.setPrimaryClip(myClip);
                Toast.makeText(getApplicationContext(), "UPI Copied",
                        Toast.LENGTH_SHORT).show();
            }
        });

        walletRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                currentBalance = documentSnapshot.getDouble("RechargeAmount");
                //updateBalanceText();
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userd = textView2.getText().toString();
                String ee = editText.getText().toString();

                if (ee.isEmpty())
                {
                    Toast.makeText(finalpayment.this, "Please Enter Refrence Number", Toast.LENGTH_SHORT).show();
                }
                else {
                    db.collection("wallet").document(userd).get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(Task<DocumentSnapshot> task) {
                                    if (task.getResult().exists()){
                                        addbalcne();
                                        addrecharge();
                                    }else {
                                        createnewwallet();
                                        addrecharge();
                                    }
                                }
                            });
                    timer();
                }
            }
        });///////////////btn close
    }

    private void updateupi() {
        db.collection("UPI")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots){
                            textView1.append(""+doc.getString("upi"));
                        }
                    }
                });
    }
    private void addamount() {
    }

    private void addbalcne() {
        String amountStr = textView.getText().toString().trim();
        if (!amountStr.isEmpty()) {
            double amount = Double.parseDouble(amountStr);
            currentBalance += amount;
            updateBalanceInFirestore();
        }
    }

    private void updateBalanceInFirestore() {
        walletRef.update("RechargeAmount", currentBalance)
                .addOnSuccessListener(aVoid -> {
                    // updateBalanceText();
                })
                .addOnFailureListener(e -> {
                    // Handle error if updating balance fails
                });
    }
    private void createnewwallet() {
        String u = textView2.getText().toString();
        String money = textView.getText().toString();

        double amo = Double.parseDouble(money);

        Map<String, Double> v1 = new HashMap<>();
        v1.put("userid", Double.valueOf(textView2.getText().toString()));
        v1.put("RechargeAmount",Double.valueOf(amo));

        FirebaseFirestore.getInstance().collection("wallet").document(u).set(v1).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                    Toast.makeText(finalpayment.this, "Recharge Succesfull", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void timer() {
        //long duration = TimeUnit.MINUTES.toMillis(1);

        new CountDownTimer(30000, 1000) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long millisUntilFinished) {
               textView3.setText("" + millisUntilFinished / 1000);
               btn.setEnabled(false);

            }
            @SuppressLint("SetTextI18n")
            @Override
            public void onFinish() {
                Intent intent = new Intent(finalpayment.this,dashboard.class);
                startActivity(intent);
                finish();
            }
        }.start();
    }
    private void addrecharge() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd:=HH:mm:ss");
        String currentDateandTime = sdf.format(new Date());
        Map<String, String> v1 = new HashMap<>();
        v1.put("userid", textView2.getText().toString());
        v1.put("Amount", textView.getText().toString());
        v1.put("RefrenceNumber", editText.getText().toString());
        FirebaseFirestore.getInstance().collection("Recharge").add(v1).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
            }
        });
    }
}