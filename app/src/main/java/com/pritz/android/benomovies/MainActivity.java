package com.pritz.android.benomovies;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.HttpResponseCache;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

import static android.R.id.empty;
import static android.R.id.list;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;
import static android.support.v7.widget.AppCompatDrawableManager.get;
import static com.pritz.android.benomovies.MoviesLoader.counter;
import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<MovieListItem>> {
    private ArrayList<MovieListItem>  movies;
    private static final String TAG = getContext().getClass().getName();
    private static final String TMDB_REQUEST_URL = "https://api.themoviedb.org/3/movie/now_playing?api_key=677316818993147181a618cbc5982e41&language=en-US";
    private ArrayList<MovieListItem> backup;
    private MovieAdapter adapter;
    private ProgressBar spinner;
    private boolean flag_loading = false;
    private int pageNo;
    private static final int LOAD_ID = 100;
    private View footerView;
    private View footerView2;
    private ListView listview;
    private int endOfListIndicator;
    private int came;
    private View empty;

    private LoaderManager loadermanager;

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume(){
        super.onResume();
    }


    @Override
    protected void onStop() {
        super.onStop();
        HttpResponseCache cache = HttpResponseCache.getInstalled();
        if (cache != null) {
            cache.flush();
        }


    }

    @Override
    protected void onStart(){
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadermanager = getLoaderManager();

        setTitle(R.string.title_screen);
        pageNo = 1;
        try {
            File httpCacheDir = new File(this.getCacheDir(), "http");
            long httpCacheSize = 5 * 1024 * 1024;
            HttpResponseCache.install(httpCacheDir, httpCacheSize);
        } catch (IOException e) {
            Log.i(TAG, "HTTP response cache installation failed:" + e);
        }

        listview = (ListView) findViewById(R.id.list_view);

        empty = findViewById(R.id.empty_view);

        toggleVisibility(empty);


        footerView = getLayoutInflater().inflate(R.layout.base_list_item_loading_footer, null);
        footerView2 = getLayoutInflater().inflate(R.layout.ending_footer, null);


        if (listview.getFooterViewsCount() == 0) {
            listview.addFooterView(footerView);
            listview.addFooterView(footerView2);
        }
        listview.removeFooterView(footerView);
        listview.removeFooterView(footerView2);

        listview.setOnScrollListener(new NewScrollListener());
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setSelected(true);
                MovieListItem mv = adapter.getItem(position);
                long movieId = mv.getId();

                Intent intent = new Intent(MainActivity.this, MovieDetailsActivity.class);
                intent.putExtra("ID", movieId);
                intent.putExtra("title",mv.getTitle());
                intent.putExtra("original_language",mv.getLanguage());
                intent.putExtra("overview",mv.getOverview());
                intent.putExtra("popu",mv.getPopularity());
                intent.putExtra("release_date",mv.getReleaseDate());

                if(isNetworkOn()) {
                    startActivity(intent);
                }
            }
        });
        movies = new ArrayList<>();
        adapter = new MovieAdapter(this, movies);
        listview.setAdapter(adapter);


        spinner = (ProgressBar) findViewById(R.id.loading_spinner);
        Button b1 = (Button) empty.findViewById(R.id.empty_view_refresh);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkOn()) {

                    int mColor = ContextCompat.getColor(MainActivity.this, R.color.colorPrimary);
                    listview.setBackgroundColor(mColor);

                    spinner.setVisibility(View.VISIBLE);
                    loadermanager.initLoader(LOAD_ID, null, MainActivity.this);
                    findViewById(R.id.empty_view).setVisibility(View.GONE);
                }
                }
        });

        if (isNetworkOn()) {
            listview.setEmptyView(empty);

            loadermanager.initLoader(LOAD_ID, null, this);

        } else {
            spinner.setVisibility(View.GONE);
            toggleVisibility(empty);
            listview.setEmptyView(empty);
            TextView text = (TextView) empty.findViewById(R.id.empty_view_text);
            int mColor = ContextCompat.getColor(this, android.R.color.white);
            listview.setBackgroundColor(mColor);
            int emptyTextViewColor = ContextCompat.getColor(this, android.R.color.black);
            listview.setBackgroundColor(mColor);
            text.setTextColor(emptyTextViewColor);
            text.setText(R.string.no_internet);


        }


    }

    public void toggleVisibility(View empty){


         TextView x1 = (TextView) empty.findViewById(R.id.empty_view_text);
        ImageView x2 = (ImageView) empty.findViewById(R.id.empty_view_drawing);
        Button b1 = (Button) empty.findViewById(R.id.empty_view_refresh);

            int toggle1 = (x1.getVisibility() == View.VISIBLE) ? View.INVISIBLE : View.VISIBLE;
            x1.setVisibility(toggle1);
        int toggle2 = (x2.getVisibility() == View.VISIBLE) ? View.INVISIBLE : View.VISIBLE;
            x2.setVisibility(toggle2);
        int toggle3 = (b1.getVisibility() == View.VISIBLE) ? View.INVISIBLE : View.VISIBLE;
            b1.setVisibility(toggle3);

    }
    public boolean isNetworkOn(){
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()){
            return true;
        }
        return false;
    }

    @Override
    public Loader<List<MovieListItem>> onCreateLoader(int id, Bundle args) {


        int pageNumber = (args == null) ? 1 : args.getInt("PAGE_NO");


        return new MoviesLoader(this, TMDB_REQUEST_URL, pageNumber);
    }

    @Override
    public void onLoadFinished(Loader<List<MovieListItem>> loader, List<MovieListItem> data) {
        spinner.setVisibility(View.GONE);
        if (footerView != null) {
            listview.removeFooterView(footerView);

            footerView.setVisibility(View.GONE);

        }
        if (data == null && isNetworkOn()) {


            listview.removeFooterView(footerView);
            listview.addFooterView(footerView2);

            endOfListIndicator = 1;
        }

        if(!isNetworkOn()){
            endOfListIndicator = 0;
            listview.addFooterView(footerView2);
        }
        if (data != null && !data.isEmpty()) {
            if(counter > 0) {
                int size = data.size();
                ArrayList<MovieListItem> dat = new ArrayList<>();
                for (int i = 0; i < size; i++) {

                    if (!(movies.contains(data.get(i)))) {
                        dat.add(data.get(i));
                    }
                }
                adapter.addAll(dat);
            }else{
                adapter.addAll(data);
            }

        }



        flag_loading = false;

    }

    @Override
    public void onLoaderReset(Loader<List<MovieListItem>> loader) {
        adapter.clear();


    }

    private class NewScrollListener implements AbsListView.OnScrollListener {

        public void onScrollStateChanged(AbsListView view, int scrollState) {


        }

    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {



        if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount != 0) {
            if (flag_loading == false) {

                final Bundle bund = new Bundle();
                ++pageNo;
                bund.putInt("PAGE_NO", pageNo );



                footerView = MainActivity.this.getLayoutInflater().inflate(R.layout.base_list_item_loading_footer, null);

                if (listview.getFooterViewsCount() == 0 && endOfListIndicator != 1) {
                    listview.addFooterView(footerView);

                }
                if(!isNetworkOn()){

                    listview.removeFooterView(footerView);
                    listview.addFooterView(footerView2);
                    listview.setOnScrollListener(null);

                   footerView2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(MainActivity.this.isNetworkOn()){
                                ++pageNo;

                                bund.putInt("PAGE_NO",pageNo );
                                loadermanager.restartLoader(LOAD_ID, bund, MainActivity.this);
                                listview.removeFooterView(footerView2);
                                listview.setOnScrollListener(new NewScrollListener());
                            }
                        }
                    });



                }
                if (endOfListIndicator == 1) {
                    listview.removeFooterView(footerView);
                    listview.addFooterView(footerView2);
                    listview.setOnScrollListener(null);
                    ((TextView)footerView2.findViewById(R.id.text_change)).setText("That's it");
                    return;
                }else{

                    loadermanager.restartLoader(LOAD_ID, bund, MainActivity.this);
                }
                flag_loading = true;



            }
        }
    }
}
}
