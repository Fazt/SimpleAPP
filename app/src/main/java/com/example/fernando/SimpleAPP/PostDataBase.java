package com.example.fernando.SimpleAPP;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class PostDataBase extends SQLiteOpenHelper {

    private final static String DB_NAME = "posts.db";
    private final static String POST_TABLE = "POSTS";
    private final static String COL_ID = "ID";
    private final static String COL_TITLE = "Title";
    private final static String COL_AUTHOR = "Author";
    private final static String COL_DATE = "Date";
    private final static String COL_URL = "Url";
    private final static String COL_FLAG = "Flag"; //0 Activo, 1 Eliminado



    public PostDataBase(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = String.format("create table %s (%s TEXT PRIMARY KEY, %s TEXT, %s TEXT,%s DATE, %s TEXT, %s INTEGER)", POST_TABLE, COL_ID, COL_TITLE, COL_AUTHOR, COL_DATE, COL_URL, COL_FLAG);
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * Metodo encargado de guardar una lista de Posts en la base de datos.
     * @param posts
     */
    public void storePosts(ArrayList<Post> posts) {
        SQLiteDatabase db = getWritableDatabase();

        for (Post post : posts) {
            ContentValues values = new ContentValues();

            values.put(COL_ID, post.getID());
            values.put(COL_TITLE, post.getTitle());
            values.put(COL_AUTHOR, post.getAuthor());
            values.put(COL_DATE, post.getCreated_at());
            values.put(COL_URL, post.getUrl());
            values.put(COL_FLAG, 0);

            db.insert(POST_TABLE, null, values);
        }
        db.close();
    }

    /**
     * Metodo encargado de obtener todos los post almacenados en la base de datos.
     * Si el post tiene el flag de eliminado, este no se carga en la lista.
     * @return ArrayList<Post>
     */
    public ArrayList<Post> getPosts() {
        ArrayList<Post> posts = new ArrayList<Post>();

        SQLiteDatabase db = getReadableDatabase();

        String sql = String.format("SELECT %s,%s,%s,%s,%s,%s FROM %s ORDER BY %s DESC", COL_ID, COL_TITLE, COL_AUTHOR, COL_DATE, COL_URL, COL_FLAG, POST_TABLE, COL_DATE);

        Cursor cursor = db.rawQuery(sql, null);

        while (cursor.moveToNext()) {
            String Id = cursor.getString(0);
            String Title = cursor.getString(1);
            String Author = cursor.getString(2);
            int Date = cursor.getInt(3);
            String Url = cursor.getString(4);
            int flag = cursor.getInt(5);

            if (flag == 0 && !Url.equals("null")) {
                posts.add(new Post(Date, Author, Title, Url, Id, flag));
            }
        }

        db.close();
        return posts;
    }

    /**
     * Metodo encargada de "borrar" un Post, aunque en realidad solo cambia su estado a eliminado
     * @param post
     */
    public void deletePost(Post post) {

        SQLiteDatabase db = getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_FLAG, 1);
        db.update(POST_TABLE, values, String.format("%s=%s", COL_ID, post.getID()), null);
        db.close();

    }

    public void ClearDB() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(POST_TABLE, null, null);
        db.close();
    }
}
