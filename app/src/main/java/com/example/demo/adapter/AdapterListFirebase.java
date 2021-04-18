package com.example.demo.adapter;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.example.demo.MainActivity;
import com.example.demo.R;
import com.example.demo.dataholder.DataHolderTodoItem;
import com.example.demo.dataholder.ViewHolderItemsList;
import com.example.demo.flag.SheetFlagSelector;
import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class AdapterListFirebase extends FirestoreRecyclerAdapter<DataHolderTodoItem, ViewHolderItemsList> {


    public AdapterListFirebase(@NonNull FirestoreRecyclerOptions<DataHolderTodoItem> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolderItemsList holder, int position, @NonNull DataHolderTodoItem model) {
        Log.d("Anirut","onBindViewHolder : "+model.getTitle()+" "+model.getFlag()+ " "+ model.getIsChecked());
        holder.title.setText(model.getTitle());
        holder.flag.setColorFilter(SheetFlagSelector.getC(model.getFlag()));
        System.out.println(holder.flag);
        holder.isChecked.setChecked(model.getIsChecked());
        DocumentReference positionRef = getSnapshots().getSnapshot(position).getReference();
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCheck(v,positionRef);
            }
        });
    }

    @NonNull
    @Override
    public ViewHolderItemsList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_todos,parent,false);
        System.out.println("creatV");
        return new ViewHolderItemsList(view);
    }

    public void deleteItem(int position){
        getSnapshots().getSnapshot(position).getReference().delete();
    }
    public void updateCheck(View v,DocumentReference position){

        boolean c = ((CheckBox)v.findViewById(R.id.isChecked)).isChecked();
        position.update("isChecked",!c);
    }
}
