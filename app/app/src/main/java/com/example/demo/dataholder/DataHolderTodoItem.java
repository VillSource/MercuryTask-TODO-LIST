package com.example.demo.dataholder;

import com.google.firebase.Timestamp;

import java.util.Date;

public class DataHolderTodoItem {

    private String title;
    private long flag;
    private boolean isChecked;

    DataHolderTodoItem(){} // need for firestore

    public DataHolderTodoItem(String title, int flag, boolean isChecked) {
        this.title = title;
        this.flag = flag;
        this.isChecked = isChecked;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getFlag() {
        return flag;
    }

    public void setFlag(long flag) {
        this.flag = flag;
    }

    public boolean getIsChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
