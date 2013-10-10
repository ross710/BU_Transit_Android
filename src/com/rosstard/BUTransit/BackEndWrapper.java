package com.rosstard.BUTransit;
import java.net.*;
import java.io.*;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;


public class BackEndWrapper extends AsyncTask<String, Void, String> {
	private final String URL_STOPS = "http://api.transloc.com/1.2/stops.json?agencies=bu";
	private final String URL_VEHICLES = "http://api.transloc.com/1.2/vehicles.json?agencies=bu";
	private final String URL_ARRIVAL_ESTIMATES = "http://api.transloc.com/1.2/arrival-estimates.json?agencies=bu";
	private final String URL_ROUTES = "http://api.transloc.com/1.2/routes.json?agencies=bu";
	
    @Override
    protected String doInBackground(String... urls) {
      String response = "";
      String url = URL_STOPS;
//      for (String url : urls) {
        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        try {
          HttpResponse execute = client.execute(httpGet);
          InputStream content = execute.getEntity().getContent();

          BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
          String s = "";
          while ((s = buffer.readLine()) != null) {
            response += s;
          }

        } catch (Exception e) {
          e.printStackTrace();
        }
//      }
      return response;
    }


    @Override
    protected void onPostExecute(String result) {
     Log.v("test", result);
    }
  }
