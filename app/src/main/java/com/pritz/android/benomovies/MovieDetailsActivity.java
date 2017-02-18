package com.pritz.android.benomovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static android.R.transition.move;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static com.google.android.youtube.player.YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION;
import static com.pritz.android.benomovies.R.id.budget;
import static com.pritz.android.benomovies.R.id.text;


public class MovieDetailsActivity extends YouTubeBaseActivity
{

    private String TMDB_GET_MOVIE = "https://api.themoviedb.org/3/movie/";

    // api_key=677316818993147181a618cbc5982e41&language=en-US&append_to_response=videos

    private String API_KEY = "677316818993147181a618cbc5982e41";

    private String language = "en-US";

    private String lang;

    public static final String IMG_URL = "https://image.tmdb.org/t/p/original/";

    private String YOUTUBE_API = "AIzaSyA_yy_jzI1ceCURQSAK4qSNmitm_wuHaFA";

    private String videoUrl;

    private YouTubePlayerView playerView;
    private YouTubePlayer player;









    private String LOG_TAG = getClass().getName();
    private long longId;
    // https://api.themoviedb.org/3/movie/157336?api_key={api_key}&append_to_response=videos
    private String movieTitle;
    private boolean update;


    private double popValue;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        longId = this.getIntent().getLongExtra("ID",1000);

        playerView = (YouTubePlayerView) findViewById(R.id.player);

        update = false;

        Uri uri = Uri.parse(TMDB_GET_MOVIE);
        Uri uri_added = uri.withAppendedPath(uri,String.valueOf(longId));
        Uri.Builder builder = uri_added.buildUpon();
        builder.appendQueryParameter("api_key",API_KEY);
        builder.appendQueryParameter("language",language);
        builder.appendQueryParameter("append_to_response","videos");
        String url = builder.toString();
        movieTitle = getIntent().getStringExtra("title");
        String overview = getIntent().getStringExtra("overview");
        lang = getIntent().getStringExtra("language");
        String releaseDate = getIntent().getStringExtra("release_date");
        playerView.setVisibility(View.INVISIBLE);
        popValue = getIntent().getDoubleExtra("popu",0.0d);
        TextView titleTextView = (TextView) findViewById(R.id.movie_detail_name);
        TextView budgTextView = (TextView) findViewById(R.id.budget);
        TextView reveTextView = (TextView) findViewById(R.id.revenue_id);
        TextView runTime = (TextView) findViewById(R.id.run_time);
        TextView prodTextView = (TextView) findViewById(R.id.prod_value);
        TextView prodCont = (TextView) findViewById(R.id.prod_cont_value);
        TextView textview = (TextView)findViewById(R.id.status_text);
        TextView taglineText = (TextView) findViewById(R.id.movie_tag_line);
        View view = findViewById(R.id.movie_status_id);
        titleTextView.setText(movieTitle);
        ColorDrawable gd = (ColorDrawable) view.getBackground();
        int mColor;
        taglineText.setText(R.string.not_available);
        budgTextView.setText(R.string.not_available);
        reveTextView.setText(R.string.not_available);
        runTime.setText(R.string.not_available);
        prodCont.setText(R.string.not_available);
        prodTextView.setText(R.string.not_available);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/myfont.ttf");
        titleTextView.setTypeface(custom_font);
        titleTextView.setShadowLayer(15, 10, 10, ContextCompat.getColor(this,android.R.color.background_dark));
        if(releaseDate != null || !(releaseDate.equals(""))){

            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
            Date date = null;
            try {
                date = dateFormatter.parse(releaseDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            dateFormatter = new SimpleDateFormat("dd MMM yyyy",new Locale("en"));
            String dateString = dateFormatter.format(date);

            textview.setText(this.getString(R.string.release) + " " + dateString);
        }
        else{

            mColor = ContextCompat.getColor(this,R.color.status_change);
            gd.setColor(mColor);
            textview.setText("Not yet Released");
        }
        TextView overviewText = (TextView) findViewById(R.id.overview_text);
        overviewText.setText(overview);


        if(isNetworkOn()) {
            MovieInfoAsyncTask task = new MovieInfoAsyncTask();
            task.execute(url);
        }

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

    private void updateUi(Movie movie) {
        // Display the earthquake title in the UI
        TextView titleTextView = (TextView) findViewById(R.id.movie_detail_name);
        TextView taglineText = (TextView) findViewById(R.id.movie_tag_line);
        ImageView img = (ImageView) findViewById(R.id.movie_detail_image);
        TextView budgTextView = (TextView) findViewById(R.id.budget);
        TextView reveTextView = (TextView) findViewById(R.id.revenue_id);
        TextView runTime = (TextView) findViewById(R.id.run_time);
        TextView prodTextView = (TextView) findViewById(R.id.prod_value);
        TextView prodCont = (TextView) findViewById(R.id.prod_cont_value);


        if(!(movieTitle.equals(movie.getmOriginalTitle())))
          titleTextView.append(" (" + movie.getmOriginalTitle() + ")");


        if(!(movie.getTagline().equals(""))) {
            taglineText.setText(movie.getTagline());
        }
        budgTextView.setText(R.string.not_available);
        reveTextView.setText(R.string.not_available);
        runTime.setText(R.string.not_available);
        prodCont.setText(R.string.not_available);
        prodTextView.setText(R.string.not_available);
        if(!(movie.getmBackdrop().equals(""))) {
            Picasso.with(this)
                    .load(movie.getmBackdrop())
                    // optional

                    .fit()
                    .into(img);
        }


        TextView[] genresView = new TextView[3];
        genresView[0] = (TextView) findViewById(R.id.genre_1);

      genresView[1] = (TextView) findViewById(R.id.genre_2);

        genresView[2] = (TextView) findViewById(R.id.genre_3);

        if(movie.getmGenres() != null ){

           int size = movie.getmGenres().size();

            for(int i=0;i<size && i<3;i++){
                genresView[i].setText(movie.getmGenres().get(i));
                genresView[i].setVisibility(View.VISIBLE);
            }


           }else{
            genresView[0].setText(R.string.not_available);
            genresView[0].setBackgroundColor(ContextCompat.getColor(this,R.color.details_bg));

        }
        TextView countTextView = (TextView) findViewById(R.id.vote_count_1);
        if(movie.getmVoteCount() != 0) {
            countTextView.setText(String.valueOf(movie.getmVoteCount()));
        }else{
            countTextView.setText(R.string.not_available);
        }
        TextView countAverage = (TextView) findViewById(R.id.vote_average);

        TextView popText = (TextView) findViewById(R.id.popularity_rating);
        DecimalFormat decimalFormatter = new DecimalFormat("0.0");
        double popular = popValue;
        String formPopularity = decimalFormatter.format(popular);
        String countTextVal = decimalFormatter.format(movie.getmAverage());
        if(countTextVal == "0.0") {
            countAverage.setText(R.string.not_available);
        }else{
            countAverage.setText(countTextVal);
        }
        if(formPopularity == "0.0") {
            popText.setText(R.string.not_available);
        }else{
            popText.setText(formPopularity);
        }

        if(lang != null)
        {titleTextView.append("("+lang+")");}
        View circles = findViewById(R.id.circle_id);
        circles.setVisibility(View.VISIBLE);
        long budg = movie.getmBudget();
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        if(budg != 0) {

            String moneyString = formatter.format(budg);

            budgTextView.setText(moneyString);
        }
        long reven = movie.getmRevenue();

        if(reven != 0) {

            String moneyString = formatter.format(movie.getmRevenue());
            reveTextView.setText(moneyString);

        }


        if(movie.getmRuntime() != 0) {
            runTime.setText(movie.getmRuntime() + " min");
        }


        ((TextView)findViewById(R.id.homepage_text)).setText(movie.getHomePage());


        String s = TextUtils.join(" ,",movie.getmProductionCompanies());

        if(!(s.equals(""))) {
            prodCont.setText(s);
        }




        s = TextUtils.join(" ,",movie.getmProductionCountries());
        if(!(s.equals(""))) {
            prodCont.setText(s);
        }


        videoUrl = movie.getmTrailerUrl();

        if(videoUrl == null || videoUrl.equals("")){

            playerView.setVisibility(View.GONE);
        }else{

            playerView.initialize(YOUTUBE_API, new YouTubePlayer.OnInitializedListener() {
                public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youtubeplayer,
                                                    boolean wasRestored) {
                    player = youtubeplayer;

                    // Specify that we want to handle fullscreen

                    player.setFullscreenControlFlags(
                                    YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION|
                                    YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE|
                                    YouTubePlayer.FULLSCREEN_FLAG_CONTROL_SYSTEM_UI);
                    // player.setOnFullscreenListener(this);
                    if (!wasRestored) {
                        Log.v("CAME ", "HERE " + videoUrl);

                        player.cueVideo(videoUrl);


                    }
                }

                @Override
                public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                    Toast.makeText(MovieDetailsActivity.this,"VIDEO COULD NOT BE LOADED, SORRY.",Toast.LENGTH_SHORT).show();
                    playerView.setVisibility(View.GONE);
                }

            });
        }
        playerView.setVisibility(View.VISIBLE);



    }
    private class MovieInfoAsyncTask extends AsyncTask<String, Void, Movie> {

        @Override
        protected Movie doInBackground(String... urls) {
            // Create URL object

            URL url = createUrl(urls[0]);
            // Perform HTTP request to the URL and receive a JSON response back
            String jsonResponse = "";
            try {
                jsonResponse = QueryUtils.getResponse(QueryUtils.makeRequest(url.toString()));
            } catch (IOException e) {
                // TODO Handle the IOException
            }

            // Extract relevant fields from the JSON response and create an {@link Event} object
            Movie movie = extractFeatureFromJson(jsonResponse);


            // Return the {@link Event} object as the result fo the {@link TsunamiAsyncTask}
            return movie;
        }

        private URL createUrl(String stringUrl) {
            URL url = null;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException exception) {
                Log.e(LOG_TAG, "Error with creating URL", exception);
                return null;
            }
            return url;
        }



        /**
         * Update the screen with the given earthquake (which was the result of the
         * {@link MovieInfoAsyncTask}).
         */
        @Override
        protected void onPostExecute(Movie movie) {
            if (movie == null) {
                return;
            }

            updateUi(movie);
        }

        /* This method is to extract movie Information

         */
        private Movie extractFeatureFromJson(String movieJSON) {



            try {

                JSONObject root = new JSONObject(movieJSON);
                JSONObject videos = root.getJSONObject("videos");



                String tagline = root.getString("tagline");

                JSONArray videoResult = videos.getJSONArray("results");
                String videoId = "";
                for(int i=0;i<videoResult.length();i++){
                    JSONObject object = videoResult.getJSONObject(i);
                    if(object.getString("site").equals("YouTube")){
                    if(object.getString("type").equals("Trailer")){
                        videoId = object.getString("key");
                }}}
                long budget = root.getLong("budget");
                JSONArray genre_json = root.getJSONArray("genres");

                ArrayList<String> genr_arr = new ArrayList<>();
                for(int i=0;i<genre_json.length();i++){
                    genr_arr.add(genre_json.getJSONObject(i).getString("name"));
                }
                String homepage = root.getString("homepage");
                String original_title = root.getString("original_title");
                JSONArray prod_comp = root.getJSONArray("production_companies");
                ArrayList<String> prod_array = new ArrayList<>();
                for(int i=0;i<prod_comp.length();i++){
                    prod_array.add(prod_comp.getJSONObject(i).getString("name"));
                }
                JSONArray prod_cont = root.getJSONArray("production_countries");
                ArrayList<String> cont_array = new ArrayList<>();
                for(int i=0;i<prod_cont.length();i++){
                    cont_array.add(prod_cont.getJSONObject(i).getString("iso_3166_1"));
                }
                Integer runtime = root.getInt("runtime");
                double vote_average = root.getDouble("vote_average");
                int vote_count = root.getInt("vote_count");
                long revenue = root.getLong("revenue");

                String img_path = root.getString("backdrop_path");
                String backpath = "";
                if(!(img_path.equals(""))) {
                    backpath = IMG_URL + img_path;
                }

                // Set the page Data for UI Update
                Movie movie = new Movie(original_title,
                        tagline,
                        prod_array,
                       cont_array,
                        genr_arr,
                        runtime,
                        videoId,
                        homepage,
                        vote_count,
                         vote_average,
                         revenue,
                         budget,
                        backpath);

                return movie;


            } catch (JSONException e) {
                Log.e("JSON Problem", "Problem parsing the now playing JSON results", e);
            }

            return null;
        }
    }
}
