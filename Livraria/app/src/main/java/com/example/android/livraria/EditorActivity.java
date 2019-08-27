package com.example.android.livraria;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.livraria.data.LivrariaContract.LivrariaEntry;


public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_LIVRO_LOADER = 0;

    private Uri mCurrentLivroUri;

    private EditText mNomeEditText;
    private EditText mPrecoEditText;
    private EditText mQuantidadeEditText;
    private EditText mAutorEditText;
    private EditText mTelAutorEditText;
    private EditText mEditoraEditText;

    Button contato;
    String telefone;

    private boolean mLivroHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mLivroHasChanged = true;
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentLivroUri = intent.getData();

        contato = (Button) findViewById(R.id.btn_contato);

        if (mCurrentLivroUri == null) {
            setTitle(getString(R.string.editor_activity_title));
            contato.setVisibility(View.INVISIBLE);
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_livro));

            getLoaderManager().initLoader(EXISTING_LIVRO_LOADER, null, this);
        }

        mNomeEditText = (EditText) findViewById(R.id.edit_livro_nome);
        mPrecoEditText = (EditText) findViewById(R.id.edit_livro_preco);
        mQuantidadeEditText = (EditText) findViewById(R.id.edit_livro_quantidade);
        mAutorEditText = (EditText) findViewById(R.id.edit_livro_autor);
        mTelAutorEditText = (EditText) findViewById(R.id.edit_livro_tel_autor);
        mEditoraEditText = (EditText) findViewById(R.id.edit_livro_editora);

        mNomeEditText.setOnTouchListener(mTouchListener);
        mPrecoEditText.setOnTouchListener(mTouchListener);
        mQuantidadeEditText.setOnTouchListener(mTouchListener);
        mAutorEditText.setOnTouchListener(mTouchListener);
        mTelAutorEditText.setOnTouchListener(mTouchListener);
        mEditoraEditText.setOnTouchListener(mTouchListener);

    }

    private void saveLivro() {
        String nomeString = mNomeEditText.getText().toString().trim();
        String precoString = mPrecoEditText.getText().toString().trim();
        String quantidadeString = mQuantidadeEditText.getText().toString().trim();
        String autorString = mAutorEditText.getText().toString().trim();
        String telAutorString = mTelAutorEditText.getText().toString().trim();
        String editoraString = mEditoraEditText.getText().toString().trim();

        if (mCurrentLivroUri == null &&
                TextUtils.isEmpty(nomeString) && TextUtils.isEmpty(precoString) &&
                TextUtils.isEmpty(quantidadeString) && TextUtils.isEmpty(autorString) &&
                TextUtils.isEmpty(telAutorString) && TextUtils.isEmpty(editoraString)) {
            return;
        }

        boolean hasEmptyFields = TextUtils.isEmpty(nomeString) ||
                TextUtils.isEmpty(precoString) ||
                TextUtils.isEmpty(quantidadeString) ||
                TextUtils.isEmpty(autorString) ||
                TextUtils.isEmpty(telAutorString) ||
                TextUtils.isEmpty(editoraString);


        if (hasEmptyFields) {
            Toast.makeText(this, R.string.editor_activity_missing_fields, Toast.LENGTH_SHORT).show();
            return;
        }


        ContentValues values = new ContentValues();
        values.put(LivrariaEntry.COLUMN_LIVRO_NOME, nomeString);
        values.put(LivrariaEntry.COLUMN_LIVRO_PRECO, precoString);
        values.put(LivrariaEntry.COLUMN_LIVRO_QUANTIDADE, quantidadeString);
        values.put(LivrariaEntry.COLUMN_LIVRO_AUTOR, autorString);
        values.put(LivrariaEntry.COLUMN_LIVRO_TEL_AUTOR, telAutorString);
        values.put(LivrariaEntry.COLUMN_LIVRO_EDITORA, editoraString);

        int quantidade = 0;
        if (!TextUtils.isEmpty(quantidadeString)) {

            try {
                quantidade = Integer.parseInt(quantidadeString);
            } catch (NumberFormatException e) {
                Toast.makeText(getApplicationContext(), "Invalid number", Toast.LENGTH_LONG).show();
                return;
            }

            quantidade = Integer.parseInt(quantidadeString);

        }
        values.put(LivrariaEntry.COLUMN_LIVRO_QUANTIDADE, quantidade);

        if (mCurrentLivroUri == null) {
            Uri newUri = getContentResolver().insert(LivrariaEntry.CONTENT_URI, values);

            if (newUri == null) {

                Toast.makeText(this, getString(R.string.editor_insert_livro_failed),
                        Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(this, getString(R.string.editor_insert_livro_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentLivroUri, values, null, null);

            if (rowsAffected == 0) {

                Toast.makeText(this, getString(R.string.editor_update_livro_failed),
                        Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(this, getString(R.string.editor_update_livro_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (mCurrentLivroUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_save:
                saveLivro();
                finish();
                return true;

            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;

            case android.R.id.home:
                if (!mLivroHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!mLivroHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                LivrariaEntry._ID,
                LivrariaEntry.COLUMN_LIVRO_NOME,
                LivrariaEntry.COLUMN_LIVRO_PRECO,
                LivrariaEntry.COLUMN_LIVRO_QUANTIDADE,
                LivrariaEntry.COLUMN_LIVRO_AUTOR,
                LivrariaEntry.COLUMN_LIVRO_TEL_AUTOR,
                LivrariaEntry.COLUMN_LIVRO_EDITORA};

        return new CursorLoader(this,   // Parent activity context
                mCurrentLivroUri,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {

            int nomeColumnIndex = cursor.getColumnIndex(LivrariaEntry.COLUMN_LIVRO_NOME);
            int precoColumnIndex = cursor.getColumnIndex(LivrariaEntry.COLUMN_LIVRO_PRECO);
            int quantidadeColumnIndex = cursor.getColumnIndex(LivrariaEntry.COLUMN_LIVRO_QUANTIDADE);
            int autorColumnIndex = cursor.getColumnIndex(LivrariaEntry.COLUMN_LIVRO_AUTOR);
            int telAutorColumnIndex = cursor.getColumnIndex(LivrariaEntry.COLUMN_LIVRO_TEL_AUTOR);
            int editoraColumnIndex = cursor.getColumnIndex(LivrariaEntry.COLUMN_LIVRO_EDITORA);

            String nome = cursor.getString(nomeColumnIndex);
            int preco = cursor.getInt(precoColumnIndex);
            int quantidade = cursor.getInt(quantidadeColumnIndex);
            String autor = cursor.getString(autorColumnIndex);
            telefone = cursor.getString(telAutorColumnIndex);
            String editora = cursor.getString(editoraColumnIndex);

            mNomeEditText.setText(nome);
            mPrecoEditText.setText(Integer.toString(preco));
            mQuantidadeEditText.setText(Integer.toString(quantidade));
            mAutorEditText.setText(autor);
            mTelAutorEditText.setText(telefone);
            mEditoraEditText.setText(editora);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNomeEditText.setText("");
        mPrecoEditText.setText("");
        mQuantidadeEditText.setText("");
        mAutorEditText.setText("");
        mTelAutorEditText.setText("");
        mEditoraEditText.setText("");
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteLivro();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteLivro() {
        if (mCurrentLivroUri != null) {

            int rowsDeleted = getContentResolver().delete(mCurrentLivroUri, null, null);

            if (rowsDeleted == 0) {

                Toast.makeText(this, getString(R.string.editor_delete_livro_failed),
                        Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(this, getString(R.string.editor_delete_livro_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    public void intentContatoAutor(View view) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + telefone));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    int quantity=0;

    public void decrement(View view) {
        quantity = Integer.valueOf(mQuantidadeEditText.getText().toString());
        if (quantity == 0) {
            Toast.makeText(this,getString(R.string.negative_values_are_not_accepted), Toast.LENGTH_SHORT).show();
            return;
        }
        mQuantidadeEditText.setText(String.valueOf(quantity));
        quantity = quantity - 1;
        mQuantidadeEditText.setText(String.valueOf(quantity));

    }

    public void increment(View view) {
        quantity = Integer.valueOf(mQuantidadeEditText.getText().toString());
        quantity = quantity + 1;
        mQuantidadeEditText.setText(String.valueOf(quantity));
    }

}
