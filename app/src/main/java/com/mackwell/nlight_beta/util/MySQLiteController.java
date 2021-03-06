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
        String orderBy = helper.COLUMN_PANELIP + " ASC";
        Cursor c = database.query(helper.TABLE_PANEL, allColumns, null,null, null, null, orderBy);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    public Cursor selectIp(){
        String[] columns = new String[] {MySQLiteOpenHelper.COLUMN_PANELIP,MySQLiteOpenHelper.COLUMN_PANELMAC,MySQLiteOpenHelper.COLUMN_PANELLOCATION,MySQLiteOpenHelper.COLUMN_CHECK,MySQLiteOpenHelper.COLUMN_ENABLE};
        String orderBy = MySQLiteOpenHelper.COLUMN_PANELIP + " ASC";
        Cursor c = database.query(MySQLiteOpenHelper.TABLE_PANEL, columns, null,null, null, null, orderBy);
        if (c != null) {
            c.moveToFirst();
        }
        return c;

    }

    public void insertPanel(Panel panel){
        ContentValues values = new ContentValues();
        values.put(MySQLiteOpenHelper.COLUMN_ID,0);
        values.put(MySQLiteOpenHelper.COLUMN_PANELIP, panel.getIp());
        values.put(MySQLiteOpenHelper.COLUMN_PANELMAC, panel.getMacString());
        values.put(MySQLiteOpenHelper.COLUMN_PANELLOCATION, panel.getPanelLocation());
        values.put(MySQLiteOpenHelper.COLUMN_CHECK,0);

//        database.insert(MySQLiteOpenHelper.TABLE_PANEL,null,values);
        database.insertWithOnConflict(MySQLiteOpenHelper.TABLE_PANEL,null,values,SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void insertPanel_Ignore(Panel panel){
        ContentValues values = new ContentValues();
        values.put(MySQLiteOpenHelper.COLUMN_ID,0);
        values.put(MySQLiteOpenHelper.COLUMN_PANELIP, panel.getIp());
        values.put(MySQLiteOpenHelper.COLUMN_PANELMAC, panel.getMacString());
        values.put(MySQLiteOpenHelper.COLUMN_PANELLOCATION, panel.getPanelLocation());
        values.put(MySQLiteOpenHelper.COLUMN_CHECK,0);

//        database.insert(MySQLiteOpenHelper.TABLE_PANEL,null,values);
        database.insertWithOnConflict(MySQLiteOpenHelper.TABLE_PANEL,null,values,SQLiteDatabase.CONFLICT_IGNORE);
    }

    public Panel findPanelByIp(String ip)
    {
        String query  = "SELECT * FROM " + MySQLiteOpenHelper.TABLE_PANEL + " WHERE " + MySQLiteOpenHelper.COLUMN_PANELIP + " = \"" + ip  + "\"";

        SQLiteDatabase db = helper.getReadableDatabase();
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

        //db.close();
        return panel;
    }

    public Panel findPanelByMac(String macAddress)
    {

        String query  = "SELECT * FROM " + MySQLiteOpenHelper.TABLE_PANEL + " WHERE " + MySQLiteOpenHelper.COLUMN_PANELMAC + " = \"" + macAddress  + "\"";

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(query,null);
        Panel panel = new Panel();

        if(cursor.moveToFirst()){
            cursor.moveToFirst();
//            panel.set_id((Integer.parseInt(cursor.getString(0))));
            panel.setIp(cursor.getString(1));
            panel.setPanelLocation(cursor.getString(3));

            cursor.close();

        }else{
            panel = null;
        }

        return panel;
    }

    public boolean panelExist(String macAddress)
    {

        String query  = "SELECT * FROM " + MySQLiteOpenHelper.TABLE_PANEL + " WHERE " + MySQLiteOpenHelper.COLUMN_PANELMAC + " = \"" + macAddress  + "\"";

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(query,null);

        if(cursor.moveToFirst()){
            cursor.moveToFirst();
//            panel.set_id((Integer.parseInt(cursor.getString(0))));

            cursor.close();
            return true;

        }else{
            return false;
        }


    }


    public boolean deletePanel(String macAddress){

        boolean result = false;

        String query = "Select * FROM " + MySQLiteOpenHelper.TABLE_PANEL + " WHERE " + MySQLiteOpenHelper.COLUMN_PANELMAC + " = \"" + macAddress + "\"";


       Cursor cursor = database.rawQuery(query,null);

        //Panel panel = new Panel();

        if(cursor.moveToFirst())
        {
            database.delete(MySQLiteOpenHelper.TABLE_PANEL, MySQLiteOpenHelper.COLUMN_PANELMAC + " = ?", new String[]{macAddress});
            cursor.close();

            result = true;

        }
        return result;

    }

    public void updatePanelIpAddress(String macAddress, String ip){
        ContentValues values = new ContentValues();
        values.put(MySQLiteOpenHelper.COLUMN_PANELIP,ip);
        String whereClause = MySQLiteOpenHelper.COLUMN_PANELMAC + "=" + "\"" + macAddress + "\"";

        database.update(MySQLiteOpenHelper.TABLE_PANEL,values,whereClause,null);
    }



    public void updatePanelLocation(String macAddress, String location){
        ContentValues values = new ContentValues();
        values.put(MySQLiteOpenHelper.COLUMN_PANELLOCATION,location);
        String whereClause = MySQLiteOpenHelper.COLUMN_PANELMAC + "=" + "\"" + macAddress + "\"";

        database.update(MySQLiteOpenHelper.TABLE_PANEL,values,whereClause,null);
    }

    public boolean isEnable(String ip)
    {
        String[] columns = new String[] {MySQLiteOpenHelper.COLUMN_ENABLE};

        Cursor c = database.query(MySQLiteOpenHelper.TABLE_PANEL, columns, MySQLiteOpenHelper.COLUMN_PANELIP + " = ?",new String[]{ip}, null, null, null);
        if (c != null) {
            c.moveToFirst();

        }

        return (c.getInt(0) != 0);

    }

    public void updateEnable(String ip,int enable){

        ContentValues values = new ContentValues();
        values.put(MySQLiteOpenHelper.COLUMN_ENABLE,enable);
        String whereClause = MySQLiteOpenHelper.COLUMN_PANELIP + "=" + "\"" + ip + "\"";

        database.update(MySQLiteOpenHelper.TABLE_PANEL,values,whereClause,null);

    }

    public void resetEnable(){
        ContentValues values = new ContentValues();
        values.put(MySQLiteOpenHelper.COLUMN_ENABLE,1);
        database.update(MySQLiteOpenHelper.TABLE_PANEL,values,null,null);

    }

    public boolean isChedked(String ip)
    {
        String[] columns = new String[] {MySQLiteOpenHelper.COLUMN_CHECK};

        Cursor c = database.query(MySQLiteOpenHelper.TABLE_PANEL, columns, MySQLiteOpenHelper.COLUMN_PANELIP + " = ?",new String[]{ip}, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }

        return (c.getInt(0) != 0);

    }

    public void updateChecked(String macAddress,int check){

        ContentValues values = new ContentValues();
        values.put(MySQLiteOpenHelper.COLUMN_CHECK,check);
        String whereClause = MySQLiteOpenHelper.COLUMN_PANELMAC + "=" + "\"" + macAddress + "\"";

        database.update(MySQLiteOpenHelper.TABLE_PANEL,values,whereClause,null);

    }

    public void resetCheck(){
        ContentValues values = new ContentValues();
        values.put(MySQLiteOpenHelper.COLUMN_CHECK,0);
        database.update(MySQLiteOpenHelper.TABLE_PANEL,values,null,null);
    }

}
