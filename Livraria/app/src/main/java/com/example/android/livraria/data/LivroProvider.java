package com.example.android.livraria.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.livraria.data.LivrariaContract.LivrariaEntry;

public class LivroProvider extends ContentProvider {

    public static final String LOG_TAG = LivroProvider.class.getSimpleName();

    private static final int LIVROS = 100;

    private static final int LIVRO_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(LivrariaContract.CONTENT_AUTHORITY, LivrariaContract.PATH_LIVROS, LIVROS);

        sUriMatcher.addURI(LivrariaContract.CONTENT_AUTHORITY, LivrariaContract.PATH_LIVROS + "/#", LIVRO_ID);
    }

    private LivrariaDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new LivrariaDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case LIVROS:

                cursor = database.query(LivrariaEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case LIVRO_ID:

                selection = LivrariaEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(LivrariaEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case LIVROS:
                return insertLivro(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertLivro(Uri uri, ContentValues values) {

        String nome = values.getAsString(LivrariaEntry.COLUMN_LIVRO_NOME);
        if (nome == null) {
            throw new IllegalArgumentException("Livro precisa de um Nome");
        }

        String preco = values.getAsString(LivrariaEntry.COLUMN_LIVRO_PRECO);
        if (preco == null) {
            throw new IllegalArgumentException("Livro precisa de um Preco");
        }

        Integer quantidade = values.getAsInteger(LivrariaEntry.COLUMN_LIVRO_QUANTIDADE);
        if (quantidade != null && quantidade < 0) {
            throw new IllegalArgumentException("Livro precisa de uma Quantidade valida");
        }

        String autor = values.getAsString(LivrariaEntry.COLUMN_LIVRO_AUTOR);
        if (autor == null) {
            throw new IllegalArgumentException("Livro precisa de um Autor");
        }

        String tel = values.getAsString(LivrariaEntry.COLUMN_LIVRO_TEL_AUTOR);
        if (tel == null) {
            throw new IllegalArgumentException("Livro precisa de um Telefone do Autor");
        }

        String editora = values.getAsString(LivrariaEntry.COLUMN_LIVRO_EDITORA);
        if (editora == null) {
            throw new IllegalArgumentException("Livro precisa de uma Editora");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(LivrariaEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case LIVROS:
                return updateLivro(uri, contentValues, selection, selectionArgs);
            case LIVRO_ID:
                selection = LivrariaEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateLivro(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateLivro(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(LivrariaEntry.COLUMN_LIVRO_NOME)) {
            String nome = values.getAsString(LivrariaEntry.COLUMN_LIVRO_NOME);
            if (nome == null) {
                throw new IllegalArgumentException("Livro precisa de um Nome");
            }
        }

        if (values.containsKey(LivrariaEntry.COLUMN_LIVRO_PRECO)) {
            Integer preco = values.getAsInteger(LivrariaEntry.COLUMN_LIVRO_PRECO);
            if (preco == null) {
                throw new IllegalArgumentException("Livro precisa de um Preco");
            }
        }

        if (values.containsKey(LivrariaEntry.COLUMN_LIVRO_QUANTIDADE)) {

            Integer quantidade = values.getAsInteger(LivrariaEntry.COLUMN_LIVRO_QUANTIDADE);
            if (quantidade != null && quantidade < 0) {
                throw new IllegalArgumentException("Livro precisa de uma Quantidade valida");
            }
        }

        if (values.containsKey(LivrariaEntry.COLUMN_LIVRO_AUTOR)) {
            String autor = values.getAsString(LivrariaEntry.COLUMN_LIVRO_AUTOR);
            if (autor == null) {
                throw new IllegalArgumentException("Livro precisa de um Autor");
            }
        }

        if (values.containsKey(LivrariaEntry.COLUMN_LIVRO_TEL_AUTOR)) {
            String tel = values.getAsString(LivrariaEntry.COLUMN_LIVRO_TEL_AUTOR);
            if (tel == null) {
                throw new IllegalArgumentException("Livro precisa de um Telefone do Autor");
            }
        }

        if (values.containsKey(LivrariaEntry.COLUMN_LIVRO_EDITORA)) {
            String editora = values.getAsString(LivrariaEntry.COLUMN_LIVRO_EDITORA);
            if (editora == null) {
                throw new IllegalArgumentException("Livro precisa de uma Editora");
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(LivrariaEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case LIVROS:

                rowsDeleted = database.delete(LivrariaEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case LIVRO_ID:

                selection = LivrariaEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(LivrariaEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case LIVROS:
                return LivrariaEntry.CONTENT_LIST_TYPE;
            case LIVRO_ID:
                return LivrariaEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}

