package com.mackwell.nlight_beta.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mackwell.nlight_beta.models.Panel;

/**
 * Created by weiyuan zhu on 13/10/14.
 */
public class MySQLiteController {

    private SQLiteDatabase database;
    private MySQLiteOpenHelper helper;

    public MySQLiteController(Context context)
    {
        helper = new MySQLiteOpenHelper(context);

    }

    public void open(){
        database = helper.getWritableDatabase();
    }

    public void close(){
        database.close();
    }



    public Cursor readData() {
        String[] allColumns = new String[] {helper.COLUMN_ID, helper.COLUMN_PANELMAC,helper.COLUMN_CHECK,helper.COLUMN_PANELLOCATION,helper.COLUMN_PANELIP};
        Cursor c = database.query(helper.TABLE_PANEL, allColumns, null,null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    public void addProduct(Panel panel){
        ContentValues values = new ContentValues();
        values.put(MySQLiteOpenHelper.COLUMN_PANELLOCATION, panel.getPanelLocation());
        values.put(MySQLiteOpenHelper.COLUMN_PANELIP, panel.getIp());

        database.insert(MySQLiteOpenHelper.TABLE_PANEL,null,values);
    }

    public Panel findPanel(String ip)
    {
        String query  = "SELECT * FROM " + MySQLiteOpenHelper.TABLE_PANEL + " WHERE " + MySQLiteOpenHelper.COLUMN_PANELLOCATION + " = \"" + ip  + "\"";

        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery(query,null);
        Panel panel = new Panel();

        if(cursor.moveToFirst()){
            cursor.moveToFirst();
//            panel.set_id((Integer.parseInt(cursor.getString(0))));
            panel.setIp(cursor.getString(2));
            panel.setPanelLocation(cursor.getString(1));

            cursor.close();

        }else{
            panel = null;
        }

        db.close();
        return panel;
    }

    public boolean deletePanel(String panelIP){

        boolean result = false;

        String query = "SELECT * FROM " + MySQLiteOpenHelper.TABLE_PANEL + " WHERE " + MySQLiteOpenHelper.COLUMN_PANELIP + " = \"" + panelIP + "\"";


        Cursor cursor = database.rawQuery(query,null);

        Panel panel = new Panel();

        if(cursor.moveToFirst())
        {
            panel.setIp(cursor.getString(2));

            database.delete(MySQLiteOpenHelper.TABLE_PANEL, MySQLiteOpenHelper.COLUMN_PANELIP + " = ?", new String[]{panel.getIp()});
            cursor.close();

            result = true;

        }
        return result;

    }
}
