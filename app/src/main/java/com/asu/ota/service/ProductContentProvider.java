package com.asu.ota.service;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.asu.ota.database.DatabaseHelper;

public class ProductContentProvider extends ContentProvider {

    private final String TAG = ProductContentProvider.class.getSimpleName();

    private DatabaseHelper dataBaseHelper;

    private String   TABLE = "Product";

    @Override
    public boolean onCreate() {
        Log.d(TAG, "onCreate()");
        dataBaseHelper = DatabaseHelper.getInstance(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Log.d(TAG, "查询到了数据....");
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();

        /**
         * 查询全部
         */
        Cursor curosr = db.query(TABLE, // 表名
                projection, // 查询的列
                selection,   // selection 查询的条件 xxx=?
                selectionArgs, // selectionArgs 查询条件的值
                null, // groupBy 分组
                null, // having 分组过滤条件
                sortOrder); // orderBy 排序

        return curosr;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Log.d(TAG, "插入了数据....");
        SQLiteDatabase database = dataBaseHelper.getWritableDatabase();
        // 参数一：表名   参数二:其他应用传递过来的ContentValues
        database.insert(TABLE, null, values);
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.d(TAG, "删除了数据....");
        SQLiteDatabase database = dataBaseHelper.getWritableDatabase();
        // 参数一：表名   参数二:其他应用传递过来的查询条件   参数三:其他应用传递过来的查询条件的值
        database.delete(TABLE, selection, selectionArgs);
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.d(TAG, "修改了数据....");
        SQLiteDatabase database = dataBaseHelper.getWritableDatabase();
        // 参数一：表名   参数二:其他应用传递过来的ContentValues   参数三:其他应用传递过来的查询条件
        database.update(TABLE, values, selection, selectionArgs);
        return 0;
    }

}
