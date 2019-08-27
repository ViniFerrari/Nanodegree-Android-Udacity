package com.example.android.livraria;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.livraria.data.LivrariaContract.LivrariaEntry;

public class LivroCursorAdapter extends CursorAdapter {

    public LivroCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }


    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView summaryTextView = (TextView) view.findViewById(R.id.summary);
        TextView precoTextView = (TextView) view.findViewById(R.id.preco);
        final TextView quantidadeTextView = (TextView) view.findViewById(R.id.quantidade);
        LinearLayout parentView = (LinearLayout) view.findViewById(R.id.container_layout);

        int idColumnIndex = cursor.getColumnIndex(LivrariaEntry._ID);
        int nomeColumnIndex = cursor.getColumnIndex(LivrariaEntry.COLUMN_LIVRO_NOME);
        int autorColumnIndex = cursor.getColumnIndex(LivrariaEntry.COLUMN_LIVRO_AUTOR);
        int precoColumnIndex = cursor.getColumnIndex(LivrariaEntry.COLUMN_LIVRO_PRECO);
        final int quantidadeColumnIndex = cursor.getColumnIndex(LivrariaEntry.COLUMN_LIVRO_QUANTIDADE);

        final int rowId = cursor.getInt(idColumnIndex);
        String nome = cursor.getString(nomeColumnIndex);
        String autor = cursor.getString(autorColumnIndex);
        int preco = cursor.getInt(precoColumnIndex);
        final int quantidade = cursor.getInt(quantidadeColumnIndex);

        if (quantidade <= 1) {
            quantidadeTextView.setText(quantidade + " " + context.getResources().getString(R.string.livro));
        } else {
            quantidadeTextView.setText(quantidade + " " + context.getResources().getString(R.string.livros));
        }

        nameTextView.setText(nome);
        summaryTextView.setText(autor);
        precoTextView.setText("R$" + String.valueOf(preco) + ",00");

        parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EditorActivity.class);
                Uri currentInventoryUri = ContentUris.withAppendedId(LivrariaEntry.CONTENT_URI, rowId);
                intent.setData(currentInventoryUri);
                context.startActivity(intent);
            }
        });
        Button btnComprar = (Button) view.findViewById(R.id.comprar);
        btnComprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String text = quantidadeTextView.getText().toString();
                String[] splittedText = text.split(" ");
                int quant = Integer.parseInt(splittedText[0]);

                if (quant == 0) {
                    Toast.makeText(context, R.string.quantidade_zerada, Toast.LENGTH_SHORT).show();
                } else if (quant > 0) {
                    quant = quant - 1;

                    String quantidadeString = Integer.toString(quant);

                    ContentValues values = new ContentValues();
                    values.put(LivrariaEntry.COLUMN_LIVRO_QUANTIDADE, quantidadeString);

                    Uri currentInventoryUri = ContentUris.withAppendedId(LivrariaEntry.CONTENT_URI, rowId);

                    int rowsAffected = context.getContentResolver().update(currentInventoryUri, values, null, null);

                    if (rowsAffected != 0) {
                        if (quantidade <= 1) {
                            quantidadeTextView.setText(quant + " " + context.getResources().getString(R.string.livro));
                        } else {
                            quantidadeTextView.setText(quant + " " + context.getResources().getString(R.string.livros));
                        }
                    } else {
                        Toast.makeText(context, R.string.failed_to_update, Toast.LENGTH_SHORT).show();
                    }
                }
            }

        });

    }

}

