package com.example.demo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.demo.adapter.AdapterListFirebase;
import com.example.demo.dataholder.DataHolderTodoItem;
import com.example.demo.flag.SheetFlagSelector;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //      Data for upload
    private  Date deadLine = null;
    private long flag = -1;
    private EditText editNewTaskTitle;


    //      Profile Sheet
    private BottomSheetBehavior bottomSheetBehavior;
    private BottomSheetDialog bottomSheetDialog;
    private View bottomSheetView;
    private NavigationView nav;

    //      Flag Sheet
    private BottomSheetBehavior bottomSheetBehaviorFlag;
    private BottomSheetDialog bottomSheetDialogFlag;
    private View bottomSheetViewFlag;
    private NavigationView navFlag;

    //      Firebase
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    public AdapterListFirebase adapter;
    private FirebaseAuth mAuth;
    public FirebaseUser user;

    //      View
    private View ovVIEW;
    private LinearLayout iconMenu,taskTag,btnMenu,bar;
    private ImageView ivCalenderIcon;
    private LinearLayout submit;
    private ImageView flaglogo;
    private TextView flagtitle ;

    //      Parameters
    private LinearLayout.MarginLayoutParams params;

    //      Check if not log in yet then go to log in first
    @Override
    protected void onRestart() {
        super.onRestart();
        user = mAuth.getCurrentUser();
        if (user==null){
            finish();
        }
        flag = SheetFlagSelector.DEFAULT_COLOR;
        updateFlag();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        //      fire base
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();



//      Profile and FlagSelector Sheet onCreate
        createProfileSheet();
        createFlagSelectorSheet();

        //      View binding
        recyclerView = findViewById(R.id.list_user);
        editNewTaskTitle = findViewById(R.id.editNewTaskTitle);
        ovVIEW = findViewById(R.id.ovVIEW);
        iconMenu=findViewById(R.id.iconMenu);
        taskTag=findViewById(R.id.taskTag);
        btnMenu=findViewById(R.id.btnMenu);
        bar=findViewById(R.id.bar);
        ivCalenderIcon = findViewById(R.id.ivCalenderIcon);
        flaglogo = findViewById(R.id.logoType);
        flagtitle = findViewById(R.id.titleType);




        //      Clear onFocus when click Overlay field
        ovVIEW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFocus(v);
            }
        });

        //      Chang layout when Focus textBox
        editNewTaskTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    ovVIEW.setVisibility(ovVIEW.VISIBLE);
                    focusBox();
                }else {
                    ovVIEW.setVisibility(ovVIEW.INVISIBLE);
                    nonfocusBox();
                }
            }
        });
        editNewTaskTitle.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if(deadLine==null)
                        dateOut(editNewTaskTitle);
                    upload();
                    handled = true;
                }
                return handled;
            }
        });
        editNewTaskTitle.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
//                System.out.println("\t\t\t\t"+(s.length()!=0) +"\t"+(deadLine!=null )+ "\t" + (flag==-1));
                if(s.length()!=0)
                    submit.setVisibility(submit.VISIBLE);
                else submit.setVisibility(submit.INVISIBLE);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });




        //      Query to list of User
        Query query = db
                .collection("userTodos")
                .document(user.getUid())
                .collection("todos")
//                .orderBy("flag");
                .orderBy("deadLine", Query.Direction.ASCENDING);


        //      RecyclerViewOptions
        FirestoreRecyclerOptions<DataHolderTodoItem> options = new FirestoreRecyclerOptions.Builder<DataHolderTodoItem>()
                .setQuery(query, DataHolderTodoItem.class)
                .build();
        adapter = new AdapterListFirebase(options);



        //      Check empty list
        View empty = (View)findViewById(R.id.isEmptyList);
        RecyclerView list = (RecyclerView)findViewById(R.id.list_user);
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d("Anirut", "Listen failed.", e);
                    return;
                }

                if (snapshot != null && !snapshot.isEmpty()) {
                    empty.setVisibility(empty.INVISIBLE);
                    list.setVisibility(list.VISIBLE);
//                    ((TextView)findViewById(R.id.textView5)).setText("Check your task");
//                    Log.d("Anirut", "onComplete: \t\tInempty");
                } else {
                    empty.setVisibility(empty.VISIBLE);
                    list.setVisibility(list.INVISIBLE);
//                    ((TextView)findViewById(R.id.textView5)).setText("Empty task");
//                    Log.d("Anirut", "onComplete: \t\tEmpty");
                }
            }
        });


        ///         set list todos to show
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


        //          Set on swiped
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                adapter.deleteItem(viewHolder.getAdapterPosition());
                Snackbar.make(recyclerView,"deleted",Snackbar.LENGTH_SHORT)
                        .setAnchorView(R.id.bar).show();
            }

            @Override
            public void onChildDraw(@NonNull Canvas c,
                                    @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(getColor(R.color.red))
                        .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_24)
                        .create()
                        .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(recyclerView);



        ///         Upload List Todos
        submit =  findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Anirut","add");
                upload();
            }
        });

    }


    //          LogOut
    public void logOut(){
        GoogleSignInClient mGoogleSignInClient;
        // Firebase sign out
        mAuth.signOut();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getApplicationContext(), gso);
        mGoogleSignInClient.signOut().addOnCompleteListener(MainActivity.this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(),"Logouted",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }


    ///         upload data
    private void upload(){
        if(
                deadLine == null ||
                editNewTaskTitle.getText().toString().isEmpty()||
                flag<0
        ){
            Toast.makeText(getApplicationContext(),"Please fill in all information.",Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, Object> data = new HashMap<>();
        data.put("title",editNewTaskTitle.getText().toString() );
        data.put("flag", flag);
        data.put("isChecked", false);
        data.put("timeStamp",Timestamp.now());
        data.put("deadLine",new Timestamp(deadLine));

        db.collection("userTodos").document(user.getUid()).collection("todos")
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        deadLine = null;
                        flag = SheetFlagSelector.DEFAULT_COLOR;
                        editNewTaskTitle.setText("");
                        clearFocus(editNewTaskTitle);
                        updateCalenderIcon();
                        Log.d("Anirut", "DocumentSnapshot added with ID: " + documentReference.getId()+data);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Anirut", "Error adding document", e);
                    }
                });
    }


    //      start Update List
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();

        //      set Flag Color
        SheetFlagSelector.setColor(
                new int[]{
                        getColor(R.color.defualtFlag),
                        getColor(R.color.schoolFlag),
                        getColor(R.color.workFlag),
                }
        );
        flag = SheetFlagSelector.DEFAULT_COLOR;
        System.out.println(flag);
        updateFlag();
    }

    //      stop Update List
    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
















    private void createProfileSheet(){

        bottomSheetView =getLayoutInflater().inflate(R.layout.bottom_sheet_profile, null);

        nav = bottomSheetView.findViewById(R.id.navProfile);

        View header = nav.getHeaderView(0);
        Glide.with(getApplicationContext())
                .load(user.getPhotoUrl())
                .into((CircleImageView)header.findViewById(R.id.ivProfileHeader));
        ((TextView)header.findViewById(R.id.tvDisplayNameHeader)).setText(user.getDisplayName());
        ((TextView)header.findViewById(R.id.tvEmailHeader)).setText(user.getEmail());

        nav.setNavigationItemSelectedListener(this);

        bottomSheetDialog = new BottomSheetDialog(MainActivity.this);
        bottomSheetDialog.setContentView(bottomSheetView);

        bottomSheetBehavior = BottomSheetBehavior.from((View) bottomSheetView.getParent());

        bottomSheetBehavior.setPeekHeight(1000);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_DRAGGING);


        bottomSheetDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                // do something
            }
        });

        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                // do something
            }
        });

        BottomSheetBehavior.BottomSheetCallback bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // do something
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // do something
            }
        };

    }
    //      open Profile menu for onClick
    public void openMenu(View view){
        bottomSheetDialog.show();
    }


    public void createFlagSelectorSheet(){

        bottomSheetViewFlag =getLayoutInflater().inflate(R.layout.bottom_sheet_flag_selector, null);

        bottomSheetViewFlag.findViewById(R.id.flagSelectDefault).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SheetFlagSelector.DEFAULT_COLOR = 0;
                updateFlag();
                clsselectFlag(v);
            }
        });
        bottomSheetViewFlag.findViewById(R.id.flagSelectSchool).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SheetFlagSelector.DEFAULT_COLOR = 1;
                updateFlag();
                clsselectFlag(v);
            }
        });
        bottomSheetViewFlag.findViewById(R.id.flagSelectWork).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SheetFlagSelector.DEFAULT_COLOR = 2;
                updateFlag();
                clsselectFlag(v);
            }
        });


        bottomSheetDialogFlag = new BottomSheetDialog(MainActivity.this);
        bottomSheetDialogFlag.setContentView(bottomSheetViewFlag);

        bottomSheetBehaviorFlag = BottomSheetBehavior.from((View) bottomSheetViewFlag.getParent());

        bottomSheetBehaviorFlag.setPeekHeight(1000);
        bottomSheetBehaviorFlag.setState(BottomSheetBehavior.STATE_DRAGGING);


        bottomSheetDialogFlag.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
            }
        });

        bottomSheetDialogFlag.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                // do something
            }
        });

        BottomSheetBehavior.BottomSheetCallback bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // do something
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // do something
            }
        };

    }
    public void selectFlag(View view){
        bottomSheetDialogFlag.show();
    }
    public void clsselectFlag(View view){
        bottomSheetDialogFlag.hide();
    }

    public void updateFlag(){
        if (flag<0)return;
        flag = SheetFlagSelector.DEFAULT_COLOR;
        flaglogo.setColorFilter(SheetFlagSelector.getC(flag));
        flagtitle.setText(SheetFlagSelector.getName(flag));
    }
















    //      chang Layout textBox
    private void focusBox(){
        // ปิด icon menu
        params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT,0);
        iconMenu.setLayoutParams(params);

        // เปิด tag
        params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT,1);
        taskTag.setLayoutParams(params);

        // เปิดขอบขวามน
        btnMenu.setBackground(getDrawable(R.drawable.flag_bg));

        // ลดขนาด
//        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,110,1);
//        bar.setLayoutParams(params);

        //ชิดขอบฝั่งขวา
        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.setMarginStart(0);
        params.setMarginEnd(convertDpToPx(getApplicationContext(),16));
        btnMenu.setLayoutParams(params);

        // ชิดขอบขวา
        params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT,1);
        params.setMarginEnd(0);
        params.setMarginStart(convertDpToPx(getApplicationContext(),16));
        editNewTaskTitle.setLayoutParams(params);
    }
    private void nonfocusBox(){
        // เปิด icon menu
        params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT,1);
        iconMenu.setLayoutParams(params);

        // ปิด tag
        params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT,0);
        taskTag.setLayoutParams(params);

        // ปิดขอบขวามน
        btnMenu.setBackground(getDrawable(R.drawable.newtaskbox));

        // ลดขนาด
//        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,110,1);
//        bar.setLayoutParams(params);

        //ไม่ชิดขอบฝั่งขวา
        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.setMarginStart(convertDpToPx(getApplicationContext(),8));
        params.setMarginEnd(convertDpToPx(getApplicationContext(),60));
        btnMenu.setLayoutParams(params);

        // ไม่ชิดขอบขวา
        params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT,1);
        params.setMarginEnd(convertDpToPx(getApplicationContext(),8));
        params.setMarginStart(convertDpToPx(getApplicationContext(),60));
        editNewTaskTitle.setLayoutParams(params);
    }
    public static int convertDpToPx(Context context, int dp) {
        return (int)(dp * context.getResources().getDisplayMetrics().density);
    }

    //      void function
    public void aVoid(View v){}
    public void clearFocus(View v){
        editNewTaskTitle.clearFocus();
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }


    public MaterialDatePicker picDate(){
        MaterialDatePicker.Builder date = MaterialDatePicker.Builder.datePicker();
        date.setTitleText("Due time");
        MaterialDatePicker picker = date.build();
        picker.show(getSupportFragmentManager(),"DATE_PICKER");
        picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                calendar.setTime(new Date((Long) selection));
                deadLine = calendar.getTime();
                updateCalenderIcon();
            }
        });
        return picker;
    }
    public void dateOut(View v){
        System.out.println(picDate().getViewLifecycleOwnerLiveData());
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.btnlogOutMenu:
                logOut();
                break;
            case R.id.btnProfileSetting:
                openProfileSetting();
                break;
            default:
                bottomSheetDialog.hide();
                Toast.makeText(getApplicationContext(),"Default list",Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    public void openProfileSetting(){startActivity(new Intent(this,ProfileActivity.class));}

    private void updateCalenderIcon(){
        if(deadLine!=null) {
            ivCalenderIcon.setColorFilter(getColor(R.color.updateCalenderIconDate));
//            submit.setVisibility(submit.VISIBLE);
        }
        else {
            ivCalenderIcon.setColorFilter(getColor(R.color.updateCalenderIconNull));
//            submit.setVisibility(submit.INVISIBLE);
        }
    }
}