package com.m25dev.booklisting;

import android.content.Context;
import android.preference.TwoStatePreference;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mohamed on 9/6/17.
 */

public class BooksAdapter extends ArrayAdapter<Book> {

    List<Book> mBooks;

    public BooksAdapter(@NonNull Context context, @NonNull List<Book> books) {
        super(context, 0, books);
        mBooks = books;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if(view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        Book currentBook = mBooks.get(position);

        TextView tvTitle = (TextView) view.findViewById(R.id.title_text_view);
        tvTitle.setText(currentBook.getTitle());

        TextView tvAuthors = (TextView) view.findViewById(R.id.authors_text_view);
        tvAuthors.setText(currentBook.getAuthors());

        TextView tvDate = (TextView) view.findViewById(R.id.date_text_view);
        tvDate.setText(currentBook.getPubDate());

        RatingBar ratingBar = (RatingBar) view.findViewById(R.id.rating_bar);
        ratingBar.setRating(currentBook.getRate());

        return view;
    }
}
