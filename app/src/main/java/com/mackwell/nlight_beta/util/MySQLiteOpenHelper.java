package com.mackwell.nlight_beta.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by weiyuan zhu on 10/10/14.
 */
public class MySQLiteOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "panelDB.db";
    public static final String TABLE_PANEL = "panels";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PANELIP = "panel_ip";
    public static final String COLUMN_PANELMAC = "panel_mac";
    public static final String COLUMN_PANELLOCATION = "panel_location";
    public static final String COLUMN_CHECK = "panel_check";
    public static final String COLUMN_ENABLE = "panel_enable";

    public static final int DISABLE = 0;




    public MySQLiteOpenHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    public MySQLiteOpenHelper(Context context,SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PRODUCTS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_PANEL
                + " (" + COLUMN_ID + " INTEGER DEFAULT 0, "
                + COLUMN_PANELIP + " TEXT NOT NULL, "
                + COLUMN_PANELMAC + " TEXT PRIMARY KEY NOT NULL, "
                + COLUMN_PANELLOCATION + " TEXT, "
                + COLUMN_CHECK + " INTEGER DEFAULT 0, "
                + COLUMN_ENABLE + " INTEGER DEFAULT 1)";
        db.execSQL(CREATE_PRODUCTS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANEL);
        onCreate(db);

    }


}
