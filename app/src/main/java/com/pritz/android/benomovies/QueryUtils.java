package com.pritz.android.benomovies;

import android.util.Log;

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

/**
 * Created by HP-PC on 10-02-2017.
 */

public final class QueryUtils {

    /** Sample JSON response for a USGS query */

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Return a list of {@link Movie} objects that has been built up from
     * parsing a JSON response.
     */
    public static ArrayList<Movie> extractEarthquakes(String url) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Create an empty ArrayList that we can start adding earthquakes to
        ArrayList<Movie> earthquakes = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // TODO: Parse the response given by the SAMPLE_JSON_RESPONSE string and
            // build up a list of Earthquake objects with the corresponding data.

            String extractedJSON = getResponse(makeRequest(url));
            JSONObject root = new JSONObject(extractedJSON);
            JSONArray features = root.getJSONArray("features");
            for(int i=0;i<features.length();i++){
                JSONObject featureData = features.getJSONObject(i);
                JSONObject properties = featureData.getJSONObject("properties");
                Double mag = properties.getDouble("mag");
                String location = properties.getString("place");
                long time = properties.getLong("time");
                String urlid = properties.getString("url");
                Movie movie = new Movie("Deadpool", "en", false);
                earthquakes.add(movie);
            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }
        catch(IOException e){
            Log.e("Problem", "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of earthquakes
        return earthquakes;
    }

    public static InputStream makeRequest(String urlString) throws IOException{
        URL url = null;
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
