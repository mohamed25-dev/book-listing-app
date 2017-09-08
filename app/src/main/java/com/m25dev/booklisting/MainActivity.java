package com.m25dev.booklisting;



import android.app.LoaderManager;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {

    private static final String TAG = "MainActivity" ;
    ListView mListView;
    ProgressBar mProgressBar;
    BooksAdapter mBooksAdapter;
    TextView mTextView;
    public String Url;
    LoaderManager loaderManager;

    public static final String BASE_URL = "https://www.googleapis.com/books/v1/volumes?q=";
    ArrayList<Book> mBooks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBooksAdapter = new BooksAdapter(this, mBooks);
        mListView = (ListView) findViewById(R.id.books_list);
        mTextView = (TextView) findViewById(R.id.textView);

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        loaderManager = getLoaderManager();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Log.d(TAG, "onQueryTextSubmit: called");
                if(checkInternetConnection()) {
                    Log.d(TAG, "onQueryTextSubmit: internet is available");
                    mTextView.setVisibility(View.INVISIBLE);
                    mProgressBar.setVisibility(View.VISIBLE);
                    Url = BASE_URL + query;

                    Log.d(TAG, "onQueryTextSubmit: " + Url);

                    mBooksAdapter.clear();
                    mBooks.clear();

                    loaderManager.initLoader(1, null, MainActivity.this);

                    //reset the search view
                    searchView.setQuery("", false);
                    searchView.setIconified(true);
                    searchItem.collapseActionView();

                    Url = "";

                    //set The title of the activity to the search query
                    MainActivity.this.setTitle(query);
                } else {
                    mTextView.setText(R.string.no_internet);
                    mBooksAdapter.clear();
                    mBooks.clear();
                    mTextView.setVisibility(View.VISIBLE);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle bundle) {
        return new BooksLoader(this, Url);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {
        mListView.setAdapter(mBooksAdapter);
        mBooksAdapter.clear();
        mProgressBar.setVisibility(View.GONE);

        //check if there is no results
        if(books.size() < 1) {
            mTextView.setText(R.string.no_results);
            mTextView.setVisibility(View.VISIBLE);
            loaderManager.destroyLoader(1);
            return;
        }
        mBooksAdapter.addAll(books);
        books.clear();
        loaderManager.destroyLoader(1);
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        mBooksAdapter.clear();
    }

    private boolean checkInternetConnection() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }
}
