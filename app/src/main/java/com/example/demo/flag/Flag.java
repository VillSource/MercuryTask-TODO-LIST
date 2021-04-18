package com.example.demo.flag;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class Flag {
    public static final String __UNINIT__ = "UNINITED";
    public static final String __INITTED__ = "INITED";

    private static FirebaseFirestore db;

    public static Map<String,String> color = new HashMap<>();

    private static String state = __UNINIT__;

    public static void init(FirebaseUser user){
        if (state==__INITTED__)return;
        state = __INITTED__;
        db = FirebaseFirestore.getInstance();
        Query query = db
                .collection("UserProfile")
                .document(user.getUid())
                .collection("flag")
                .orderBy("number",Query.Direction.ASCENDING);
        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(QueryDocumentSnapshot i : task.getResult()){
                            color.put(i.get("number").toString(),i.get("color").toString());
                            Log.d("Anirut",getColor());
                        }
                    }
                });

    }

    public static String getState() {
        return state;
    }
    public static String getColor(){
        String s ="\n{\n";
        for(String i : color.keySet())s+= "\t" + i + " : " + color.get(i);
        s+="\n}";
        return s;
    }


}
