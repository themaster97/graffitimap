package com.zybooks.graffiti;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;


public class SqlDb extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "image.db";
    private static final int VERSION = 7;
    private static SqlDb db;

    public SqlDb(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }


    private static final class ImageTable {
        private static final String TABLE = "imgTable";
        private static final String COL_ID = "_id";
        private static final String COL_LAT = "latitude";
        private static final String COL_LON= "longitude";
        private static final String COL_IMG = "img";
    }

    public Image returnImage(String id){
        String query = "SELECT * FROM " + ImageTable.TABLE + " WHERE " + ImageTable.COL_ID + " = " + id;
        Cursor cursor = getItem(query);


        Image queryImage = new Image(id, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                queryImage = new Image(
                        ( cursor.getString(0) ),
                        ( cursor.getString(1) ),
                        ( cursor.getString(2) ),
                        ( BitmapFactory.decodeByteArray(cursor.getBlob(2), 0, cursor.getBlob(2).length) )
                );
            } while (cursor.moveToNext());
        }

        cursor.close();
        return queryImage;
    }

    public static SqlDb getInstance(Context context) {
        if (db == null) {
            db = new SqlDb(context);
        }
        return db;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + ImageTable.TABLE + " (" +
                ImageTable.COL_ID + " integer primary key autoincrement, " +
                ImageTable.COL_LON + " text, " +
                ImageTable.COL_LAT + " text, " +
                ImageTable.COL_IMG + " text, " +
                " float)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + ImageTable.TABLE);
        onCreate(db);
    }

    public long addImage(String lat, String lon, Bitmap bit) {
        SQLiteDatabase db = getWritableDatabase();

        //START Got these three lines for converting a Bitmap to a byte array from here: https://stackoverflow.com/questions/28183558/how-to-save-bitmap-from-imageview-to-database-sqlite
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bit.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] img = bos.toByteArray();
        //END

        ContentValues values = new ContentValues();
        values.put(ImageTable.COL_LON, lon);
        values.put(ImageTable.COL_LAT, lat);
        values.put(ImageTable.COL_IMG, img);

        return db.insert(ImageTable.TABLE, null, values);
    }


    public ArrayList<Image> getImages() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Image> Images = new ArrayList<Image>();

        String sql = "select * from " + ImageTable.TABLE;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                Image i = new Image(
                        ( cursor.getString(0) ),
                        ( cursor.getString(1) ),
                        ( cursor.getString(2) ),
                        ( BitmapFactory.decodeByteArray(cursor.getBlob(3), 0, cursor.getBlob(3).length) )
                );

                //Add the currently created image to the list of Images
                Images.add(i);

            } while (cursor.moveToNext());
        }
        cursor.close();
        //return the list of Images from the database
        return Images;
    }

    public Cursor getItem(String query){
        return this.getReadableDatabase().rawQuery(query, null);
    }

    public boolean deleteImage(String id) {
        SQLiteDatabase db = getWritableDatabase();
        int rowsDeleted = db.delete(ImageTable.TABLE, ImageTable.COL_ID + " = ?", new String[] { id });
        return rowsDeleted > 0;
    }

    public void clearDatabase(String TABLE_NAME) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE "+ ImageTable.TABLE);
    }


}
