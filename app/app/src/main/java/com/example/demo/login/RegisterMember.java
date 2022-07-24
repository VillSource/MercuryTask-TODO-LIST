package com.example.demo.login;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class RegisterMember {
    public static void register(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("UserProfile").document(user.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("Anirut", "Profile: " + document.getData());
                    } else {
                        Map<String, Object> profileMap = new HashMap<String, Object>() {{
                            put("uid", user.getUid());
                            put("email", user.getEmail());
                            put("displayName",user.getDisplayName());
                            put("phoneNumber",user.getPhoneNumber());
                            put("imgProfile",user.getPhotoUrl().toString());
                        }};

                        docRef.collection("flag").document("Default").set(
                                new HashMap<String,Object>(){{
                                    put("number",(int)0);
                                    put("color","#000fff");
                                }}
                        );

                        docRef.set(profileMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("Anirut", "DocumentSnapshot successfully written!");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("Anirut", "Error writing document", e);
                                    }
                                });
                        Log.d("Anirut", "Registered");

                    }
                } else {
                    Log.d("Anirut", "get failed with ", task.getException());
                }
            }
        });
    }
}
