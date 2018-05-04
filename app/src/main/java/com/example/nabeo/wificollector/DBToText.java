package com.example.nabeo.wificollector;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by nabeo on 2018/05/04.
 */

public class DBToText {
    private static SQLiteDatabase db;
    private static Context context;

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
        String rootdir = Environment.getExternalStorageDirectory().toString() + "/WifiCollector/";
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
}
