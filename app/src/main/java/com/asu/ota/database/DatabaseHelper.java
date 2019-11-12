package com.asu.ota.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;


public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String sDB_NAME = "asu";
    private static final int sDB_VERSION = 2;
    private Context mContext;
    private static DatabaseHelper sInstance;

    private static final String sCREATE_PRODUCT = "create table Product ("
            + "id integer primary key autoincrement, "
            + "name text,"
            + "dbid integer"
            + ")";
    private static final String CREATE_PACKAGE = "create table Package ("
            + "id integer primary key autoincrement, "
            + "version text,"
            + "dbid integer,"
            + "productid integer"
            + ")";


    private static final String CREATE_OTA = "create table Category ("
            + "_id integer primary key autoincrement, "
            + "category_name text, "
            + "category_code integer)";

    /**
     * 得到创建数据库的帮助类
     * @param context
     * @return
     */
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context);
        }
        return sInstance;
    }

    public DatabaseHelper(Context context) {
        super(context,sDB_NAME, null, sDB_VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sCREATE_PRODUCT);
        db.execSQL(CREATE_PACKAGE);
        Toast.makeText(mContext,"create succcess",Toast.LENGTH_SHORT).show();
    }

    /**
     * 前两句的意思是如果发现数据库已经有这两张表就把他删掉，通过onCreate方法重新创建
     * 如果不删掉的话就会直接报错
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //这样在产品上线是不可以的
       /* db.execSQL("drop table if exists Book");
        db.execSQL("drop table if exists Category");
        onCreate(db);*/

        switch (newVersion) {
            case 1:
                db.execSQL(sCREATE_PRODUCT);
                db.execSQL(CREATE_OTA);
                break;

            case 2:
                db.execSQL(CREATE_OTA);

                db.rawQuery("select * from Category",null);
                Toast.makeText(mContext,"create succcess01",Toast.LENGTH_SHORT).show();
            default:
        }
    }
}
