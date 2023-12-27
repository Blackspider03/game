package com.example.winmenia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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

import java.util.HashMap;
import java.util.Map;

public class banking extends AppCompatActivity {

    EditText e1,e2,e3,e4,e5,e6,e7;
    Button b1,b2;
    FirebaseAuth auth;
    FirebaseUser user;

    TextView textView;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banking);

        e1=findViewById(R.id.acname);
        e2=findViewById(R.id.ifsccode);
        e3=findViewById(R.id.bname);
        e4=findViewById(R.id.acnum);
        e5=findViewById(R.id.acmbob);
        e6=findViewById(R.id.upiid);
        e7=findViewById(R.id.upiname);
        b1=findViewById(R.id.bankbtn);
        b2=findViewById(R.id.upibtn);
        textView=findViewById(R.id.useriiiid);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        assert user != null;
        textView.setText(user.getPhoneNumber());
        String userd = textView.getText().toString();

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                String ed1 = e1.getText().toString();
                String ed2 = e2.getText().toString();
                String ed3 = e3.getText().toString();
                String ed4 = e4.getText().toString();
                String ed5 = e5.getText().toString();

                if (ed1.isEmpty() || ed2.isEmpty()  || ed3.isEmpty()  || ed4.isEmpty()  || ed5.isEmpty())
                {
                    Toast.makeText(banking.this, "Please Fill All Details", Toast.LENGTH_SHORT).show();
                }
                else {
                    addbankdetails();
                }
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ed4 = e6.getText().toString();
                String ed5 = e7.getText().toString();

                if (ed4 .isEmpty()  || ed5.isEmpty())
                {
                    Toast.makeText(banking.this, "Please Fill All Details", Toast.LENGTH_SHORT).show();
                }
                else {
                    addupidetails();
                }
            }
        });
    }
    private void addupidetails() {
        String userd = textView.getText().toString();
        Map<String, String> v2 = new HashMap<>();
        v2.put("UPIID", e6.getText().toString());
        v2.put("Name", e7.getText().toString());
        v2.put("Userid",textView.getText().toString());

        FirebaseFirestore.getInstance().collection("Banking").document(userd).set(v2)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(banking.this, "Deatils Submited", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void addbankdetails() {
        String userd = textView.getText().toString();

        Map<String, String> v2 = new HashMap<>();
        v2.put("Name", e1.getText().toString());
        v2.put("IFSC", e2.getText().toString());
        v2.put("BankName", e3.getText().toString());
        v2.put("AccountNum", e4.getText().toString());
        v2.put("RegisterMob", e5.getText().toString());
        v2.put("Userid",textView.getText().toString());

        FirebaseFirestore.getInstance().collection("Banking").document(userd).set(v2)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(banking.this, "Detail Submited", Toast.LENGTH_SHORT).show();
                    }
                });


    }
}