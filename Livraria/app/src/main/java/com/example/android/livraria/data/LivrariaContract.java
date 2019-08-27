package com.example.android.livraria.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class LivrariaContract {

    private LivrariaContract() {}

    public static final String CONTENT_AUTHORITY = "com.example.android.livraria";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_LIVROS = "livros";

    public static final class LivrariaEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_LIVROS);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LIVROS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LIVROS;

        public static final String TABLE_NAME = "livros";

        public static final String _ID = BaseColumns._ID;

        public static final String COLUMN_LIVRO_NOME = "nome";
        public static final String COLUMN_LIVRO_PRECO = "preco";
        public static final String COLUMN_LIVRO_QUANTIDADE = "quantidade";
        public static final String COLUMN_LIVRO_AUTOR = "autor";
        public static final String COLUMN_LIVRO_TEL_AUTOR = "telefone";
        public static final String COLUMN_LIVRO_EDITORA = "editora";

    }
}
