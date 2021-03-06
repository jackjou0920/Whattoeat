package com.example.jackjou.whattoeat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    //data array
    private ArrayList<FoodList> list = new ArrayList<>();
    //數據適配器
    private MainAdapter mainAdapter;
    private String TBName;
    private SharedPreferences sp;
    private static final String data = "DATA";

    EditText nameText;
    EditText noteText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        TBName = intent.getStringExtra("TBName");

        //開啟新增資料視窗
        FloatingActionButton add = (FloatingActionButton) findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFood();
            }
        });


        recyclerView = (RecyclerView) findViewById(R.id.rv_main);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mainAdapter = new MainAdapter(this, list, TBName);

        retrieve(TBName);

    }

    //新增資料浮動視窗
    private void addFood(){

        final View item = LayoutInflater.from(SettingsActivity.this).inflate(R.layout.custom_layout, null);
        new AlertDialog.Builder(SettingsActivity.this)
                .setView(item)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //取得店家名稱
                        nameText = (EditText) item.findViewById(R.id.nameEdit);
                        //取得店家註解
                        noteText = (EditText) item.findViewById(R.id.noteEdit);

                        if(nameText.length() <= 0){
                            Toast.makeText(SettingsActivity.this, "店家名稱不能是空滴喔^3^", Toast.LENGTH_SHORT).show();
                        }
                        else if(nameText.getText().toString().trim().isEmpty()){
                            Toast.makeText(SettingsActivity.this, "店家名稱不能是空格喔^3^", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            //把兩個字串傳入save()
                            save(nameText.getText().toString(), noteText.getText().toString());
                        }
                    }
                })
                .show();
    }

    //儲存資料到database
    private void save(String name,String note){
        MyDatabase db = new MyDatabase(this);

        //OPEN DB
        db.openDB();

        //COMMIT
        db.addDB(TBName,name,note);

        db.closeDB();

        //REFRESH
        retrieve(TBName);
    }

    //RETREIEV
    private void retrieve(String TBName){
        list.clear();

        MyDatabase db = new MyDatabase(this);
        db.openDB();

        //RETRIEVE
        Cursor c = db.getAll(TBName);

        //LOOP AND ADD TO ARRAYLIST
        while (c.moveToNext()){
            int id = c.getInt(0);
            String name = c.getString(1);
            String note = c.getString(2);

            FoodList p = new FoodList(id, name, note);

            //ADD TO ARRAYLIS
            list.add(p);
        }

        //CHECK IF ARRAYLIST ISNT EMPTY

        recyclerView.setAdapter(mainAdapter);

        db.closeDB();
    }

    @Override
    protected void onResume() {
        super.onResume();
        retrieve(TBName);
    }
}
