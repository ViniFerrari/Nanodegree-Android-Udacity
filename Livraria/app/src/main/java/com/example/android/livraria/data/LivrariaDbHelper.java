package com.example.android.livraria.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.livraria.data.LivrariaContract.LivrariaEntry;

public class LivrariaDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = LivrariaDbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "livraria.bd";

    private static final int DATABASE_VERSION = 1;

    public LivrariaDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_LIVROS_TABLE = "CREATE TABLE " + LivrariaEntry.TABLE_NAME + "("
                + LivrariaEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + LivrariaEntry.COLUMN_LIVRO_NOME + " TEXT NOT NULL, "
                + LivrariaEntry.COLUMN_LIVRO_PRECO + " INTEGER NOT NULL, "
                + LivrariaEntry.COLUMN_LIVRO_QUANTIDADE + " INTEGER, "
                + LivrariaEntry.COLUMN_LIVRO_AUTOR + " TEXT NOT NULL,"
                + LivrariaContract.LivrariaEntry.COLUMN_LIVRO_TEL_AUTOR + " TEXT NOT NULL,"
                + LivrariaEntry.COLUMN_LIVRO_EDITORA + " TEXT NOT NULL);";

        db.execSQL(SQL_CREATE_LIVROS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
