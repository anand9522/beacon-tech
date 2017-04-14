package com.think.mozzo_test_java;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by anand on 19/12/16.
 */

public class LoaderActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{


    ListView ls;
    ArrayAdapter<Object> aa;
    CursorAdapter cursorAdapter;
     public LoaderActivity() {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loader_activity);
         ls=(ListView) findViewById(R.id.list_view);
        ArrayList<Object> arrayList=new ArrayList<>();
        aa=new ArrayAdapter<Object>(this,R.layout.my_text_view,arrayList);

        cursorAdapter=new SimpleCursorAdapter(this,R.layout.loader_layout,null,new String[]{"_id","TIME"},new int[]{R.id._id,R.id.TIME});
        ls.setAdapter(cursorAdapter);

        getSupportLoaderManager().initLoader(0,null,this);

    }




    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader= new CursorLoader(this,UrlHistoryProvider.CONTENT_URI,null,null,null,null);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        cursorAdapter.swapCursor(data);
//        System.out.println(data.getCount());
        cursorAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
