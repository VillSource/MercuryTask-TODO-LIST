package com.example.demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    public static FirebaseUser USER;
    private FirebaseAuth mAuth;
    private DocumentReference docRef;

    private EditText name,mail,phone;
    private ImageView profileImg;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        name = findViewById(R.id.tvDisplayName);
        mail = findViewById(R.id.evEmail);
        phone = findViewById(R.id.evPhone);
        profileImg = findViewById(R.id.ivProfile);
        mAuth = FirebaseAuth.getInstance();
        USER = mAuth.getCurrentUser();
//
        db = FirebaseFirestore.getInstance();
        docRef = db
                .collection("UserProfile")
                .document(USER.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    if(doc.exists())
                        updateUI(doc);
                    else
                        Log.d("Anirut","Data null : ");
                }else{
                    Log.d("Anirut","Fail to get Profile data : " + task.getException());
                }
            }
        });
        initAutoUpload();
    }

    public static void setUID(FirebaseUser USER) {
        ProfileActivity.USER = USER;
    }

    private void updateUI(DocumentSnapshot doc){
        Glide.with(getApplicationContext()).load(doc.get("imgProfile")).into(profileImg);
        name.setText(doc.get("displayName").toString());
        mail.setText(doc.get("email").toString());
        try {
            phone.setText(doc.get("phoneNumber").toString());
        }catch (Exception e){
            phone.setText("xxxxxxxxxx");
        }

    }
    private void initAutoUpload(){
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                uploadNewData("displayName",name.getText().toString());
            }
        });
        mail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                uploadNewData("email",mail.getText().toString());
            }
        });
        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                uploadNewData("phoneNumber",phone.getText().toString());
            }
        });

    }
    public void uploadNewData(String tag,String data){
        Map<String,String> map = new HashMap<String,String>();
        map.put(tag,data);
        docRef.set(map, SetOptions.merge());
    }
    public void finishActivity(View v){
        Toast.makeText(getApplicationContext(),"Updated yor Profile",Toast.LENGTH_SHORT).show();
        finish();
    }
}