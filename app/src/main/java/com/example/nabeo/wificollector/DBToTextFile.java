package com.example.nabeo.wificollector;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBToTextFile {
    private static SQLiteDatabase db;
    private static Context context;

    DBToTextFile(Context context){
        this.context = context;
        db = new MySQLiteOpenHelper(context).getReadableDatabase();
    }

    public static void toTextFile(){
        Cursor c = db.rawQuery("select * from room", null);
        while(c.moveToNext()){

        }
    }
}
