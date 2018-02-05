package com.example.bhuva.store_buddy_group_31;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StreamCorruptedException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;



public class APIRequest extends AsyncTask<String, Integer, String> {

    // API values (API will only grab values from Arizona, to reduce information overload)
    private static final String TM_ROOT_URL = "http://api.walmartlabs.com/v1/search?";

    private static String apiKey;
 private  StringBuilder resultString;
    private HomePage resultClass;
    private String searchQuery;

    private ProgressDialog dialog;

    public APIRequest(HomePage resultClass, String searchQuery, StringBuilder resultString) {
        this.resultClass = resultClass;
        this.searchQuery = searchQuery;
        this.resultString = resultString;
    }

    @Override
    protected void onPreExecute() {

        Context context = resultClass.getContext();
        apiKey = context.getResources().getString(R.string.api_key);
        //resultString = new StringBuilder();
        dialog = new ProgressDialog(resultClass);
        dialog.setMessage("Loading Product Catalog");
        dialog.setCancelable(false);
        //dialog.setInverseBackgroundForced(false); // this was deprecated as of SDK 23

        dialog.show();

    }

    /**
     * @param params - we do not use this
     *
     * @return jsonObject - holds search results from TicketMaster API
     *                    - null if results were not returned
     */
    @Override
    protected String doInBackground(String... params) {

        // check if the app has access to the internet
        if (ContextCompat.checkSelfPermission(
                resultClass.getContext(), android.Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            return null;
        }


        String prodString = "";

        String urlString;
        URL url;
        String encodedSearch ="";
        HttpURLConnection connection = null;
        InputStreamReader stream;
        BufferedReader buffer;

        String line ="";
        String[] products ={"milk","yogurt","tomato","banana","egg","meat","olive","apple"};

        try {

            for(String product : products) {

                encodedSearch =  product+"&sort=price&order=asc&format=json";
                urlString = TM_ROOT_URL + "apikey=" + apiKey + "&query=" + encodedSearch;


                url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                if (connection.getResponseCode() == 200) {

                    // convert connection results to String
                    stream = new InputStreamReader(connection.getInputStream());
                    buffer = new BufferedReader(stream);

                    prodString = "";

                    while ((line = buffer.readLine()) != null) {
                        prodString +=line;
                        resultString.append(line);
                    }


                    System.out.println(prodString);


                } else if (connection.getResponseCode() == 401) {
                    dialog.setMessage("Invalid API key");
                } else {
                    dialog.setMessage("Error Getting Data From Server");
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            if (connection != null) {
                try {
                    connection.disconnect();
                } catch (Exception e) {
                    // disconnect error
                }
            }
        }

        return resultString.toString();
    }

    @Override
    protected void onPostExecute(String json) {

        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }

        resultClass.loadcatalog(searchQuery, json);
    }
}