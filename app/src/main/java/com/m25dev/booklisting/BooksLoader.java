package com.m25dev.booklisting;

import android.content.Context;
import android.content.AsyncTaskLoader;
import android.util.Log;

import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by mohamed on 9/7/17.
 */

public class BooksLoader extends AsyncTaskLoader<List<Book>>{

    String mUrl;
    public BooksLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
        Log.d(TAG, "onStartLoading: started");
    }

    @Override
    public List<Book> loadInBackground() {
        Log.d(TAG, "loadInBackground: started");
        return QueryUtils.fetchData(mUrl);
    }
}
