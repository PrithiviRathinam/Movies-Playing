package com.pritz.android.benomovies;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HP-PC on 10-02-2017.
 */

public class MoviesLoader extends AsyncTaskLoader<List<MovieListItem>>
{
    String mUrl;
    int page;
    public static int counter;

    protected void onStartLoading() {
        forceLoad();
    }
    public MoviesLoader(Context context, String url, int pageNo) {
        super(context);
        mUrl = url;
        page = pageNo;
        ++counter;

    }
    @Override
    public List<MovieListItem> loadInBackground() {


            Uri uri = Uri.parse(mUrl);
            Uri.Builder builder = uri.buildUpon();


            builder.appendQueryParameter("page",String.valueOf(page));



            ArrayList<MovieListItem> mv = QueryUtils.extractMoviesByPage(builder.toString());
            if(mv == null) {
                return null;
            }
        return mv;
    }
}
