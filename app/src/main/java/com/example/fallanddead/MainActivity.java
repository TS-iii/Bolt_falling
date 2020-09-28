package com.example.fallanddead;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.pedro.library.AutoPermissions;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

//    Button button;

    RecyclerView recyclerView;
    PersonAdapter adapter;
    ArrayList<PersonInfo> result;
    Button addperson;

    PersonDatabase database;

    private static final int REQUEST_CODE_REGISTER =101;
    private static final int REQUEST_CODE_PROFILE=102;
    private static final String TAG = "MainActivity";
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode==REQUEST_CODE_REGISTER){


            if(resultCode==RESULT_OK){

                String name=data.getStringExtra("name");
                String contents=data.getStringExtra("content");
                String mac=data.getStringExtra("mac");

                adapter.addItem(new PersonInfo(name,contents,mac,""));
                adapter.notifyDataSetChanged();
                database.insertRecord(name,contents,mac,"");


            }


        }
        else if(requestCode==REQUEST_CODE_PROFILE){


            // 삭제 요청
            if(resultCode==RESULT_OK){

                String mac=data.getStringExtra("mac");
                // 동기 비동기 주의!!!!!!!!!
                database.deleteRecord(mac);
                result=database.selectAll();
                adapter.setItems(result);
                recyclerView.setAdapter(adapter);


            }
            // 수정 요청
            else if(resultCode==RESULT_CANCELED){

            String mac=data.getStringExtra("mac");
            String content=data.getStringExtra("content");
            String name=data.getStringExtra("name");
            String uri=data.getStringExtra("uri");


            // 동기 비동기 주의!!!!!!
            database.updateRecord(name,content,mac,uri);
            result=database.selectAll();
            adapter.setItems(result);
            recyclerView.setAdapter(adapter);


            }



        }






    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        recyclerView=findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        adapter=new PersonAdapter();

        result=new ArrayList<PersonInfo>();


        if(database!=null){
        database.close();
        database=null;

        }

        database=PersonDatabase.getInstance(this);
        boolean isOpen=database.open();
        if(isOpen){
            Log.d(TAG,"Book database is open");

        } else { Log.d(TAG,"Book db is not open"); }


        AutoPermissions.Companion.loadAllPermissions(this,101);

        result=database.selectAll();
        adapter.setItems(result);
        recyclerView.setAdapter(adapter);


        adapter.setOnItemClickListener(new OnPersonItemClickListener() {
            @Override
            public void onItemClick(PersonAdapter.ViewHolder holder, View view, int position) {
                PersonInfo item=adapter.getItem(position);

                Intent intent=new Intent(getApplicationContext(),ProfileActivity.class);
                intent.putExtra("name",item.getName());
                intent.putExtra("mac",item.getMac());
                intent.putExtra("content",item.getContents());
                intent.putExtra("uri",item.getUrl());

                startActivityForResult(intent,REQUEST_CODE_PROFILE);
            }
        });

        addperson=findViewById(R.id.button5);
        addperson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(getApplicationContext(),RegisterActivity.class);
                startActivityForResult(intent,REQUEST_CODE_REGISTER);

            }
        });


    }




}