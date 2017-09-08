package com.m25dev.booklisting;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by mohamed on 9/6/17.
 */

public class QueryUtils {

    static StringBuilder authorsString = new StringBuilder();
    static List<Book> mBooks = new ArrayList<>();

    public static List<Book> fetchData(String stringUrl) {
       String jsonResponse = "";

        Log.d(TAG, "fetchData: started");
        try {
           URL url = createUrl(stringUrl);
           jsonResponse = makHttpRequest(url);
       } catch (IOException e) {
           e.printStackTrace();
       }
        List<Book> books = fetchJsonData(jsonResponse);

        return books;
    }

    private static List<Book> fetchJsonData(String jsonResponse) {

        try {

            JSONObject object = new JSONObject(jsonResponse);
            String totalItems = object.getString("totalItems");
            Log.d(TAG, "fetchJsonData: "+totalItems);
            JSONArray items = object.getJSONArray("items");
            Log.d(TAG, "fetchJsonData: "+items.length());
            for (int i = 0; i < items.length(); i++) {

                JSONObject currentItem = items.getJSONObject(i);

                JSONObject currentInfo = currentItem.getJSONObject("volumeInfo");
                Log.d(TAG, "fetchJsonData: "+currentItem.toString());

                String pubDate = currentInfo.getString("publishedDate");
                Log.d(TAG, "fetchJsonData: "+pubDate);

                String title = currentInfo.getString("title");
                Log.d(TAG, "fetchJsonData: "+title);

                float rate = 0f;
                if (currentInfo.has("averageRating")) {
                    rate = (float) currentInfo.getDouble("averageRating");
                    Log.d(TAG, "fetchJsonData: " + rate);
                }

                JSONArray authors = currentInfo.getJSONArray("authors");
                Log.d(TAG, "fetchJsonData: "+authors);

                if(authors.length() > 0) {
                    for (int j = 0; j < authors.length(); j++) {
                        Log.d(TAG, "fetchJsonData: " + authors.get(j));
                        authorsString.append(authors.get(j)).append(", ");
                    }
                }
                Log.d(TAG, "fetchJsonData: for loop is done");
                Book book = new Book(title, authorsString.toString(), pubDate, rate);
                mBooks.add(book);

            }
        } catch (JSONException e) {
            Log.e(TAG, "fetchJsonData: error in jsno data");
        }

        return mBooks;
    }

    private static URL createUrl (String stringUrl) {
        URL url = null;
        if (stringUrl == null) {
            return url;
        } else {

            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return url;
    }

    private static String makHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        Log.d(TAG, "makHttpRequest: started");
        if(url == null) {
            return jsonResponse;

        } else {
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                if (urlConnection.getResponseCode() == 200) {
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);

                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(urlConnection != null) {
                    urlConnection.disconnect();
                }
                if(inputStream != null) {
                    inputStream.close();
                }
            }
            return jsonResponse;
        }
    }

    private static String readFromStream(InputStream inputStream) {
        InputStreamReader streamReader;
        BufferedReader bufferedReader;
        StringBuilder result = new StringBuilder();

        Log.d(TAG, "readFromStream: started");
        if(inputStream == null) {
            return null;
        }

        streamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
        bufferedReader = new BufferedReader(streamReader);

        try {
            String line = bufferedReader.readLine();
            while (line != null) {
                result = result.append(line);
                line = bufferedReader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }
}
