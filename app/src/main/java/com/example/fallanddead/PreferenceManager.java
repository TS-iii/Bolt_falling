package com.example.fallanddead;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {


    private static final String NAME="fallanddead";

    private  static final String DEFAULT_VALUE_STRING="";

    private static final int DEFAULT_VALUE_INT=0;

    private static SharedPreferences getPreferences(Context context){

        return context.getSharedPreferences(NAME,Context.MODE_PRIVATE);

    }

    public static void setString(Context context, String key, String value){

        SharedPreferences prefs=getPreferences(context);
        SharedPreferences.Editor editor=prefs.edit();
        editor.putString(key,value);
        editor.commit();


    }

    public static String getString(Context context,String key){

        SharedPreferences prefs=getPreferences(context);
        String value=prefs.getString(key,DEFAULT_VALUE_STRING);
        return value;

    }

    public static void setInt(Context context, String key, int value){
        SharedPreferences prefs=getPreferences(context);
        SharedPreferences.Editor editor=prefs.edit();
        editor.putInt(key,value);
        editor.commit();

    }

    public static int getInt(Context context,String key){

        SharedPreferences prefs=getPreferences(context);
        int value=prefs.getInt(key,DEFAULT_VALUE_INT);
        return value;

    }

    public static void removeKey(Context context,String key){
        SharedPreferences prefs=getPreferences(context);
        SharedPreferences.Editor edit=prefs.edit();
        edit.remove(key);
        edit.commit();
    }


    public static void clear(Context context){

        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor edit = prefs.edit();
        edit.clear();
        edit.commit();

    }

}
