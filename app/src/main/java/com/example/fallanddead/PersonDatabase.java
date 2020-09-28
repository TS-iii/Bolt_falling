package com.example.fallanddead;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;

public class PersonDatabase {

    public static final String TAG="PersonDatabase";


    // 싱글턴 변수
    private static PersonDatabase database;

    // db 이름
    public static String DATABASE_NAME="fallperson1.db";

    // table 이름
    public static String TABLE_PERSON_INFO="PERSON_INFOO";

    // 버전
    public static int DATABASE_VERSION=1;

    // 헬퍼 클래스
    private DatabaseHelper dbHelper;

    private SQLiteDatabase db;
    private Context context;


    private PersonDatabase(Context context){ this.context=context;}

    // 데이터베이스 오픈
    public boolean open(){
        dbHelper=new DatabaseHelper(context);
        db=dbHelper.getWritableDatabase();

        return true;

    }

    //데이터 베이스 닫기
    public void close(){
        db.close();
        database=null;
    }

    // 싱글턴 변수 생성
    public static PersonDatabase getInstance(Context context) {
        if (database == null) {
            database = new PersonDatabase(context);
        }
        return database;
    }


    public Cursor rawQuery(String SQL){
        println("\nexecuteQuery called.\n");


        Cursor c1 = null;
        try {
            c1 = db.rawQuery(SQL, null);
            println("cursor count : " + c1.getCount());
        } catch(Exception ex) {
            Log.e(TAG, "Exception in executeQuery", ex);
        }

        return c1;



    }


    public boolean execSQL(String SQL){
        println("\nexecute called.\n");

        try{
            Log.d(TAG,"SQL:"+SQL);
            db.execSQL(SQL);
        } catch(Exception ex){
            Log.e(TAG,"Exception in executeQuery",ex);
            return false;
        }

        return true;
    }

    // 데이터 추가
    public void insertRecord(String name, String contents,String mac, String url){
        try{
            db.execSQL("insert into "+ TABLE_PERSON_INFO + "(NAME, CONTENTS , MAC , URL) values ('" + name + "',  '" + contents + "' , '"+ mac + "' , '"+url+"');" );
        } catch(Exception ex){
            Log.e(TAG, "Exception in executing insert SQL.", ex);

        }
    }

    // 삭제
    public void deleteRecord(String mac){

        //  int temp=Integer.parseInt(device);

        try{
            db.execSQL("delete from " + TABLE_PERSON_INFO + " WHERE MAC = '" + mac + "' ;");
        } catch(Exception ex){
            Log.e(TAG,"Exception in executing delete SQL.",ex);
        }

    }

    // 업데이트
    public void updateRecord(String name, String contents , String mac, String url){


        try{
            db.execSQL("update "+ TABLE_PERSON_INFO + " SET CONTENTS= '"+contents+ "', NAME='"+name+ "' , URL='"+url+"' WHERE MAC='"+mac +"' ;");
        } catch(Exception ex){
            Log.e(TAG,"Exception in executing updat SQL",ex);
        }


    }

    // 전부 반환
    public ArrayList<PersonInfo> selectAll(){
        ArrayList<PersonInfo> result=new ArrayList<PersonInfo>();
        try {
            Cursor cursor = db.rawQuery("select NAME, CONTENTS, MAC , URL from " + TABLE_PERSON_INFO, null);
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                String name = cursor.getString(0);
                String contents = cursor.getString(1);
                String mac=cursor.getString(2);
                String url=cursor.getString(3);
                PersonInfo info = new PersonInfo(name, contents,mac ,url);
                result.add(info);
            }

        } catch(Exception ex) {
            Log.e(TAG, "Exception in executing insert SQL.", ex);
        }
        return result;
    }

    // 헬퍼 클래스
    private class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context){
            super(context,DATABASE_NAME,null,DATABASE_VERSION);

        }

        public void onCreate(SQLiteDatabase _db){

            println("creating table [" + TABLE_PERSON_INFO);

            //테이블 존재하면 버림
            String DROP_SQL = "drop table if exists " + TABLE_PERSON_INFO;
            try{ _db.execSQL(DROP_SQL); }
            catch(Exception e){    Log.d(TAG, "Exception in DROP_SQL", e); }

            // 테이블 만듬
            String CREATE_SQL ="create table " + TABLE_PERSON_INFO + "("
                    + " _id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
                    + " NAME TEXT,"
                    + " CONTENTS TEXT,"
                    + " MAC TEXT,"
                    + " URL TEXT,"
                    + " CREATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                    + ")";
            try{ _db.execSQL(CREATE_SQL); Log.e(TAG,"CREATE SQL!!!!!!!!!!"); }
            catch(Exception e){  Log.e(TAG, "Exception in CREATE_SQL", e);}


        } // onCreate 끝

        public void onOpen(SQLiteDatabase db){
            println("opened database [" + DATABASE_NAME + "].");
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            println("Upgrading database from version " + oldVersion + " to " + newVersion + ".");

            if (oldVersion < 2) {   // version 1

            }
        }

        private void insertRecord(SQLiteDatabase _db, String name, String contents, String mac, String url) {
            try {
                _db.execSQL( "insert into " + TABLE_PERSON_INFO + "(NAME, CONTENTS, MAC , URL ) values ('" + name + "',  '" + contents + "' , '" + mac +"','"+url+"');" );
            } catch(Exception ex) {
                Log.e(TAG, "Exception in executing insert SQL.", ex);
            }
        }
    } // 헬퍼 클래스 끝


    private void println(String msg) {
        Log.d(TAG, msg);
    }
}
