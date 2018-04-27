package com.example.nabeo.wificollector;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by nabeo on 2018/04/07.
 */

public class WifiDatabase extends AppCompatActivity {
    private Context context;
    private SQLiteDatabase db;
    private TextView db_tv;
    private Button searchButton;
    private EditText wifiRoom;
    private StringBuilder sb;
    private int id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_database);
        context = this;
        db = new MySQLiteOpenHelper(context).getWritableDatabase();
        sb = new StringBuilder();

        id = -1;
        db_tv = (TextView)findViewById(R.id.database_tv);
        wifiRoom = (EditText)findViewById(R.id.wifi_room);
        searchButton = (Button)findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor c = db.rawQuery("select id from room where name='" + wifiRoom.getText().toString() + "'", null);
                c.moveToNext();
                id = c.getInt(c.getColumnIndex("id"));

                Cursor c2 = db.rawQuery("select * from wifi where room_id=" + id, null);
                while(c2.moveToNext()){
                    sb.append(c2.getInt(0)  + ", " + c2.getString(1) + ", " + c2.getInt(2) + ", " + c2.getInt(3) + ", " +
                            c2.getString(4) + ", " + c2.getString(5) + ", " + c2.getString(6) +  "\n");
                }
                db_tv.setText(sb.toString());

                c.close();
                c2.close();





























                db.close();
            }
        });
    }
}
