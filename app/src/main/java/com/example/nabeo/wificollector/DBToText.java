package com.example.nabeo.wificollector;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by nabeo on 2018/05/04.
 */

public class DBToText {
    private static SQLiteDatabase db;
    private static Context context;
    private static String rootdir = Environment.getExternalStorageDirectory().toString() + "/WifiCollector/";

    DBToText(Context context){
        this.context = context;
        db = new MySQLiteOpenHelper(context).getReadableDatabase();
    }

    public static void writeDBToTextFile(){
        StringBuilder sb = new StringBuilder();
        Cursor c = db.rawQuery("select * from room", null);
        while(c.moveToNext()){
            sb.append(c.getInt(0) + "," + c.getString(1) + "\n");
        }
        writeFile(sb.toString(), "room_db.txt");

        Cursor c2 = db.rawQuery("select * from bssid", null);
        sb = new StringBuilder();
        while(c2.moveToNext()){
            sb.append(c2.getInt(0) + "," + c2.getString(1) + "\n");
        }
        writeFile(sb.toString(), "bssid_db.txt");

        Cursor c3 = db.rawQuery("select * from wifi", null);
        sb = new StringBuilder();
        while(c3.moveToNext()){
            sb.append(c3.getInt(0) + "," + c3.getString(1) + "," + c3.getInt(2) +
                    "," + c3.getInt(3) + "," + c3.getInt(4) + "," + c3.getString(5) + "," + c3.getInt(6) + "\n");
        }
        writeFile(sb.toString(), "wifi_db.txt");
    }

    private static void writeFile(String str, String filePath){
        try{
            File file = new  File(rootdir + filePath);
            if(file.exists()) file.delete();
            FileWriter fileWriter = new FileWriter(file, true);
            fileWriter.write(str);
            fileWriter.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void writeTextToDB(){
        try {
            BufferedReader br_room = new BufferedReader(new FileReader(rootdir + "room_db.txt"));
            BufferedReader br_bssid = new BufferedReader(new FileReader(rootdir + "bssid_db.txt"));
            BufferedReader br_wifi = new BufferedReader(new FileReader(rootdir + "wifi_db.txt"));
            String line;
            String[] str;

            while((line = br_room.readLine()) != null){
                str  = line.split(",");
                db.execSQL("insert into room(id, name) values(" + str[0] + ", " + str[1] + ")");
            }
            while((line = br_bssid.readLine()) != null){
                str = line.split(",");
                db.execSQL("insert into bssid(id, mac) values(" + str[0] + ", " + str[1] + ")");
            }
            while((line = br_wifi.readLine()) != null){
                str = line.split(",");
                db.execSQL("insert into wifi(id, timestamp, room_id, bssid_id, count, ssid, rssi) values("
                        + str[0] + ", " + str[1] + ", " + str[2] + ", " + str[3] + ", " + str[4] + ", " + str[5] + ", " + str[6] + ")");
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
