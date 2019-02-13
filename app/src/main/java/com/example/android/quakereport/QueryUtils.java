package com.example.android.quakereport;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
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
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public final class QueryUtils {
    private static final String LOG_TAG = DataAdapter.class.getSimpleName();

    private QueryUtils() {
    }

    public static List<data> extractFeatureFromJson(String earthquakeJson) {
        if(TextUtils.isEmpty(earthquakeJson)){
            return null;
        }

        // Create an empty ArrayList that we can start adding earthquakes to
        List<data> earthquakes = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            JSONObject baseJSONResponse = new JSONObject(earthquakeJson);
            JSONArray earthquakeArray = baseJSONResponse.getJSONArray("features");

            for(int i=0; i<earthquakeArray.length();i++){
                int j;
                boolean off=false;
                String offset = null;
                JSONObject currentEarthquake = earthquakeArray.getJSONObject(i);
                JSONObject properties = currentEarthquake.getJSONObject("properties");
                double output = properties.getDouble("mag");
                DecimalFormat formatter = new DecimalFormat("0.0");
                double magnitude = Double.parseDouble(formatter.format(output));

                String location = properties.getString("place");

                for(j=0;j<location.length();j++){
                    if(location.charAt(j)=='o'&&location.charAt(j+1)=='f'){
                        offset = location.substring(0,j+2);
                        location = location.substring(j+3,location.length());
                        off = true;
                        break;
                    }
                }
                if(off==false){
                    offset = "Near the";
                }

                long time = properties.getLong("time");
                Date dateObject = new Date(time);
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM, DD, yyyy");
                String dateToDisplay = dateFormat.format(dateObject);

                String url = properties.getString("url");
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

                data earthquake = new data(magnitude, offset, location, time, browserIntent);
                earthquakes.add(earthquake);
            }
            // build up a list of Earthquake objects with the corresponding data.


        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of earthquakes
        return earthquakes;
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    public static List<data> fetchEarthquakeData(String requestUrl) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        URL url = createUrl(requestUrl);
        String jsonResponse = null;
        try{
            jsonResponse = makeHttpRequest(url);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        List<data> earthquakes = extractFeatureFromJson(jsonResponse);
        return earthquakes;
    }
}