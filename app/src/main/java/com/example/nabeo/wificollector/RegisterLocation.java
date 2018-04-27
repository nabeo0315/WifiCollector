package com.example.nabeo.wificollector;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import static com.example.nabeo.wificollector.MySQLiteOpenHelper.CREATE_ROOM_TABLE;
import static com.example.nabeo.wificollector.MySQLiteOpenHelper.DROP_ROOM_TABLE;

/**
 * Created by nabeo on 2018/04/05.
 */

public class RegisterLocation extends AppCompatActivity {
    private EditText id, place;
    private SQLiteDatabase db;
    private Button registerButton;
    private Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_location);
        context = this;

        db = new MySQLiteOpenHelper(this).getWritableDatabase();
        db.execSQL(CREATE_ROOM_TABLE);

        id = (EditText)findViewById(R.id.locationId);
        place = (EditText)findViewById(R.id.placeName);

        registerButton = (Button)findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(id.getText() != null && place.getText() != null) {
                    db.execSQL("insert into room(id, name) values('" + Integer.parseInt(id.getText().toString()) + "', '" + place.getText().toString() + "')");
                    new Handler().post(new DisplayToast(context, "登録完了"));
                }
            }
        });
    }
}
