package com.example.fallanddead;



import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.net.URI;

public class PersonInfo implements Serializable, Parcelable {

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(this.name);
        dest.writeString(this.contents);
        dest.writeString(this.mac);

    }

    String name;
    String contents;
    String mac;
    String url;


    public PersonInfo(String name, String contents, String mac,String url){
        this.name=name;
        this.contents=contents;
        this.mac=mac;
        this.url=url;

    }

    public PersonInfo(Parcel in){
        this.name=in.readString();
        this.contents=in.readString();
        this.mac=in.readString();
    }


    public String getName(){return name;}
    public void setName(String name) { this.name=name;}

    public String getUrl(){return url;}



    public String getContents(){return contents;}
    public void setContents(String contents){this.contents=contents;}

    public String getMac(){return mac;}
    public void setMac(String mac){this.mac=mac;}

    @Override
    public String toString(){
        return "PersonInfo{"+
                "name='" + name + '\'' +
                ", contents='" + contents + '\'' +
                ", mac='" + mac + '\'' +
                '}';
    }


    @SuppressWarnings("rawtypes")
    public static final Creator CREATOR=new Creator(){

        @Override
        public PersonInfo createFromParcel(Parcel in){
            return new PersonInfo(in);
        }

        @Override
        public PersonInfo[] newArray(int size){
            return new PersonInfo[size];
        }

    };
}
