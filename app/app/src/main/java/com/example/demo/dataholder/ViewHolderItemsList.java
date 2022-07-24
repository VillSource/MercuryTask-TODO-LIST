package com.example.demo.dataholder;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demo.R;
import com.example.demo.flag.SheetFlagSelector;

public class ViewHolderItemsList extends RecyclerView.ViewHolder {
    public TextView title;
    public ImageView flag;
    public CheckBox isChecked;
    public View item;

    public ViewHolderItemsList(@NonNull View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.tvTitle);
        flag = itemView.findViewById(R.id.ivFlagType);
        isChecked = itemView.findViewById(R.id.isChecked);
        item = itemView;
    }
}
