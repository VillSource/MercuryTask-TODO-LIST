package com.example.demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class ProfileActivity extends AppCompatActivity {

    public static FirebaseUser USER;
    private FirebaseAuth mAuth;

    private TextView name;
    private ImageView profileImg;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        name = findViewById(R.id.tvDisplayName);
        profileImg = findViewById(R.id.ivProfile);
        mAuth = FirebaseAuth.getInstance();
//
        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db
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
    }

    public static void setUID(FirebaseUser USER) {
        ProfileActivity.USER = USER;
    }

    private void updateUI(DocumentSnapshot doc){
        Glide.with(getApplicationContext()).load(doc.get("imgProfile")).into(profileImg);
        name.setText(doc.get("displayName").toString());
    }

    public void logOut(View v){
        GoogleSignInClient mGoogleSignInClient;
        // Firebase sign out
        mAuth.signOut();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getApplicationContext(), gso);
        mGoogleSignInClient.signOut().addOnCompleteListener(ProfileActivity.this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(),"Logouted",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }
}