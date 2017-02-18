package com.pritz.android.benomovies;

import android.net.http.HttpResponseCache;
import android.util.Log;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.R.attr.data;
import static android.R.attr.id;
import static java.security.AccessController.getContext;

/**
 * Created by HP-PC on 10-02-2017.
 */

public final class QueryUtils {
    public static final String IMG_URL = "https://image.tmdb.org/t/p/original";



    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Return a list of {@link MovieListItem} objects that has been built up from
     * parsing a JSON response.
     */


    public static ArrayList<MovieListItem> extractMoviesByPage(String url) {

        // Create an empty ArrayList that we can start adding earthquakes to
        ArrayList<MovieListItem> moviesByPage = new ArrayList<>();

        // Try to parse the JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // TODO: Parse the response given by the SAMPLE_JSON_RESPONSE string and
            // build up a list of MovieListItem objects with the corresponding data.

            String extractedJSON = getResponse(makeRequest(url));
            if(extractedJSON == null){
                return null;
            }
            JSONObject root = new JSONObject(extractedJSON);
            JSONArray results = root.getJSONArray("results");
            int totalPages = root.getInt("total_pages");
            int currentPage = root.getInt("page");

            if(currentPage > totalPages)
            {
                return null;
            }
            for(int i=0;i<results.length();i++){
                JSONObject featureData = results.getJSONObject(i);
                String movieTitle = featureData.getString("title");
                String movieLang = featureData.getString("original_language");

                long id = featureData.getLong("id");
                String releaseDate = featureData.getString("release_date");
                String overview = featureData.getString("overview");
                String urlPath = featureData.getString("poster_path");
                double popularity = featureData.getDouble("popularity");
                String imageUrl = "";
                if(urlPath != null || urlPath.equals("")) {
                    imageUrl = IMG_URL + urlPath;
                }
                MovieListItem movieListItem = new MovieListItem(id,movieTitle);
                movieListItem.setmImgUrl(imageUrl);
                movieListItem.setmLanguage(movieLang);

                movieListItem.setmReleaseDate(releaseDate);
                movieListItem.setmOverview(overview);
                movieListItem.setmPopularity(popularity);



                moviesByPage.add(movieListItem);




            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("JSON Problem", "Problem parsing the now playing JSON results", e);
        }
        catch(IOException e){
            Log.e("IO Problem", "Problem parsing the now playing JSON results", e);
        }


        // Return the list of movies
        return moviesByPage;
    }

    public static InputStream makeRequest(String urlString) throws IOException{
        URL url;
        HttpURLConnection urlConnection = null;
        try {
            url = new URL(urlString);

            urlConnection = (HttpURLConnection) url.openConnection();


            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");

            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                InputStream inputStream = urlConnection.getInputStream();
                return inputStream;
            }
        }catch (IOException e){
            Log.e("PROBLEM" , "problem",e);
        }finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }

    public static String getResponse(InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            if (inputStream == null)
                return null;


            InputStreamReader reader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader buffReader = new BufferedReader(reader);
            String line = buffReader.readLine();
            while (line != null) {
                stringBuilder.append(line);
                line = buffReader.readLine();
            }
        }

        finally{
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return stringBuilder.toString();
    }

}
