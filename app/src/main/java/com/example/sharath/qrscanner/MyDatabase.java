package com.example.sharath.qrscanner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

/**
 * Created by Sharath on 09-08-2016.
 */
public class MyDatabase extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME ="qrtables.db";
    private static final String TABLE_NAME = "qrtable";
    private static final String COLUMN_0="TAG";
    private static final String COLUMN_1="POINTS";
    private static final String COLUMN_2="MARKED";
    private static final String COLUMN_3="CLUE";
    private static final String ROW_NUMBER="NUMBER";
    MyDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db){
        String query = "CREATE TABLE "+TABLE_NAME+" ("+COLUMN_0+" TEXT,"+COLUMN_1+" INTEGER,"+COLUMN_2+" INTEGER,"+
                COLUMN_3+" TEXT,"+ROW_NUMBER+" TEXT"+");";
        db.execSQL(query);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }
    public void dropTable()
    {
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }
    public void emptyTable()
    {
        SQLiteDatabase db =this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
    }
    public void addRow(String tag,int points,int number,String clue)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues cv= new ContentValues();
        cv.put(COLUMN_0,tag);
        cv.put(COLUMN_1,points);
        cv.put(ROW_NUMBER,String.valueOf(number));
        cv.put(COLUMN_2,0);
        cv.put(COLUMN_3,clue);
        db.insert(TABLE_NAME, null, cv);
    }
    public Bundle getRow(int number)
    {
        String query= "SELECT * FROM "+TABLE_NAME+" WHERE "+ROW_NUMBER+"="+String.valueOf(number);
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor c=db.rawQuery(query, null);
        Bundle bundle=new Bundle();
        c.moveToFirst();
        if(c.getCount()>0)
            do {
                bundle.putString(COLUMN_0, c.getString(c.getColumnIndex(COLUMN_0)));
                bundle.putInt(COLUMN_1, c.getInt(c.getColumnIndex(COLUMN_1)));
                bundle.putInt(COLUMN_2,c.getInt(c.getColumnIndex(COLUMN_2)));
                bundle.putString(COLUMN_3, c.getString(c.getColumnIndex(COLUMN_3)));
            }while(c.moveToNext());
        c.close();
        return bundle;
    }
    public void updateRow(int number,int ismarked)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues cv= new ContentValues();
        cv.put(COLUMN_2,ismarked);
        db.update(TABLE_NAME, cv, ROW_NUMBER + "= ?", new String[]{String.valueOf(number)});
    }
    public String getCell(String col,int id){
        String query= "SELECT * FROM "+TABLE_NAME+" WHERE "+ROW_NUMBER+"="+String.valueOf(id);
        SQLiteDatabase db=this.getWritableDatabase();
        String result=new String();
        Cursor c=db.rawQuery(query, null);
        c.moveToFirst();
        if(c.getCount()>0)
            do {
                if(col.equals("TAG")||col.equals("NUMBER")||col.equals("CLUE"))
                result= c.getString(c.getColumnIndex(col));
                else
                    result=String.valueOf(c.getInt(c.getColumnIndex(col)));
            }while(c.moveToNext());
        c.close();
        return result;
    }
    public void setPiece(String col,int id,String name){
        ContentValues cv = new ContentValues();
        SQLiteDatabase db=this.getWritableDatabase();
        cv.put(col, name);
        db.update(TABLE_NAME, cv, ROW_NUMBER + "= ?", new String[]{String.valueOf(id)});
        //String query="UPDATE "+TABLE_NAME+" SET "+col+" ="+name+" WHERE "+ROW_NUMBER+"="+ String.valueOf(id);
        //db.execSQL(query);
    }
    public int getCount(){
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor c=db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        int k=c.getCount();
        c.close();
        return k;
    }

}
