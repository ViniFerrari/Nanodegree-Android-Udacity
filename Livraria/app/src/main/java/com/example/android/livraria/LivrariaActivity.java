package com.example.android.livraria;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.livraria.data.LivrariaContract.LivrariaEntry;

public class LivrariaActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LIVRO_LOADER = 0;

    private LivroCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_livraria);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LivrariaActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        ListView livroListView = (ListView) findViewById(R.id.list);

        View emptyView = findViewById(R.id.empty_view);
        livroListView.setEmptyView(emptyView);

        mCursorAdapter = new LivroCursorAdapter(this, null);
        livroListView.setAdapter(mCursorAdapter);

        livroListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Intent intent = new Intent(LivrariaActivity.this, EditorActivity.class);

                Uri currentLivroUri = ContentUris.withAppendedId(LivrariaEntry.CONTENT_URI, id);

                intent.setData(currentLivroUri);

                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(LIVRO_LOADER, null, this);

    }

    private void insertLivro() {
        ContentValues values = new ContentValues();
        values.put(LivrariaEntry.COLUMN_LIVRO_NOME, "Duna");
        values.put(LivrariaEntry.COLUMN_LIVRO_PRECO, 55);
        values.put(LivrariaEntry.COLUMN_LIVRO_QUANTIDADE, 30);
        values.put(LivrariaEntry.COLUMN_LIVRO_AUTOR, "Frank Herbert");
        values.put(LivrariaEntry.COLUMN_LIVRO_TEL_AUTOR, "999999999");
        values.put(LivrariaEntry.COLUMN_LIVRO_EDITORA, "Aleph");

        Uri newUri = getContentResolver().insert(LivrariaEntry.CONTENT_URI, values);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.inserir_livro_aleatorio:
                insertLivro();
                return true;

            case R.id.deletar_todos_livros:
                deleteAllLivros();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                LivrariaEntry._ID,
                LivrariaEntry.COLUMN_LIVRO_NOME,
                LivrariaEntry.COLUMN_LIVRO_AUTOR,
                LivrariaEntry.COLUMN_LIVRO_PRECO,
                LivrariaEntry.COLUMN_LIVRO_QUANTIDADE};

        return new CursorLoader(this,   // Parent activity context
                LivrariaEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    private void deleteAllLivros() {
        int rowsDeleted = getContentResolver().delete(LivrariaEntry.CONTENT_URI, null, null);
        Log.v("LivrariaActivity", rowsDeleted + " rows deleted from book database");
    }

}
