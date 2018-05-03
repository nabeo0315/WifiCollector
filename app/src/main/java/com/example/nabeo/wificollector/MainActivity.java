package com.example.nabeo.wificollector;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.example.nabeo.wificollector.MySQLiteOpenHelper.CREATE_BSSID_TABLE;
import static com.example.nabeo.wificollector.MySQLiteOpenHelper.CREATE_ROOM_TABLE;
import static com.example.nabeo.wificollector.MySQLiteOpenHelper.CREATE_WIFI_TABLE;

public class MainActivity extends AppCompatActivity {
    private Button startButton;
    private EditText editText;
    private SQLiteDatabase db;
    private static int counter, count;
    private String room;
    private Context context;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        db = new MySQLiteOpenHelper(this).getWritableDatabase();

        startButton = (Button)findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wifiScanData();
            }
        });

        ArrayList<String> room = new ArrayList<String>();
        room.add("-");
        Cursor c = db.rawQuery("select name from room", null);
        while(c.moveToNext()){
            room.add(c.getString(0));
        }

        spinner = (Spinner)findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, room);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 999);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        menu.findItem(R.id.main_menu).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startActivity(new Intent(context, RegisterLocation.class));
                return true;
            }
        });

        menu.findItem(R.id.database).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                startActivity(new Intent(context, WifiDatabase.class));
                return true;
            }
        });
        return true;
    }

    private void wifiScanData(){
        db.execSQL(CREATE_WIFI_TABLE);
        db.execSQL(CREATE_ROOM_TABLE);
        db.execSQL(CREATE_BSSID_TABLE);

        room = spinner.getSelectedItem().toString();
        Cursor c = db.rawQuery("select * from room where name = '" + room + "'", null);
        Log.d("www", String.valueOf(c.getColumnCount()));
        Log.d("www", room);
        if(c.getCount() == 0){
            new Handler().post(new DisplayToast(this, "未登録ロケーション"));
            return;
        }else{
            counter = 0;
            if(db.rawQuery("select * from room where name = \"" + room + "\"", new String[]{}).getCount() != 0){
                Cursor cursor = db.rawQuery("select max(count) as max from wifi where room_id = '" + getRoomId(room) + "'", null);
                cursor.moveToNext();
                count = cursor.getInt(cursor.getColumnIndex("max"));
                cursor.close();
            }

            registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    counter++;
                    if(counter < 11) {
                        Log.v("broadcastReceiver", "receive");
                        count++;
                        scanTrainData(room);
                    }else{
                        unregisterReceiver(this);
                    }
                    ((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).startScan();
                }
            }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            ((WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE)).startScan();
            //editText.setText("");
        }
    }

    public void scanTrainData(String room_name){
        new Handler().post(new DisplayToast(this, "スキャン回数：" + String.valueOf(count)));
        for(ScanResult result: ((WifiManager)this.getApplicationContext().getSystemService(Context.WIFI_SERVICE)).getScanResults()) {
            Log.v("", EscapeSQL(result.SSID));
            String sql = "insert into wifi(timestamp, room_id, bssid_id, count, ssid, rssi) values ('"
                    + new SimpleDateFormat("kk':'mm':'ss").format(new Date()) + "', " + getRoomId(room_name) + ", "  + getBssidId(result.BSSID) + "," + count +
                    ", '" + EscapeSQL(result.SSID) + "', " + result.level + ")";
            db.execSQL(sql);

        }
    }

    private int getRoomId(String room){
        int room_id;
        Cursor c = db.rawQuery("select * from room where name = '" + room + "'", null);
        if(c.getCount() == 0){
            db.execSQL("insert into room(name) values('" + room + "')");
            // Log.v("getRoomID state", "arrive");
        }
        c.close();
        c = null;
        c = db.rawQuery("select * from room where name = '" + room + "'", null);
        c.moveToNext();
        room_id = c.getInt(c.getColumnIndex("id"));
        c.close();
        //Log.v("getRoomId state 2", "close");
        return room_id;
    }

    private int getBssidId(String bssid){
        int bssid_id;
        Cursor c = db.rawQuery("select * from bssid where mac = '" + bssid + "'", null);
        if(c.getCount() == 0){
            db.execSQL("insert into bssid(mac) values('" + bssid + "')");
        }
        c.close();
        c = null;
        c = db.rawQuery("select * from bssid where mac = '" + bssid + "'", null);
        c.moveToNext();
        bssid_id = c.getInt(c.getColumnIndex("id"));
        c.close();
        return bssid_id;
    }

    public static String substitute(String input, String pattern, String replacement){
        int index = input.indexOf(pattern);
        if(index == -1){
            return input;
        }

        StringBuilder buffer = new StringBuilder();
        buffer.append(input.substring(0, index) + replacement);

        if(index + pattern.length() < input.length()){
            Log.v("", pattern);
            String rest = input.substring(index + pattern.length(), input.length());
            buffer.append(substitute(rest, pattern, replacement));
        }
        return buffer.toString();
    }

    public static String EscapeSQL(String input){
        input = substitute(input, "'", "\'");
        return input;
    }
}
