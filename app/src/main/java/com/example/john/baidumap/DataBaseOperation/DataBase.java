package com.example.john.baidumap.DataBaseOperation;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by android学习 on 2017/12/12.
 */

public class DataBase extends SQLiteOpenHelper {
    public static final String CREATE_USERINFO = "create table userinfo ("
            + "id integer primary key autoincrement,"
            + "account text, "
            + "password text,"
            + "record blob)";
    private Context mContext;

    public DataBase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_USERINFO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists userinfo");
        onCreate(sqLiteDatabase);
    }
}
