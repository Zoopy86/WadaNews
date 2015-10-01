package com.zinc.zoopy.wadanews;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 30-09-15.
 */
public class GetNewsTask extends AsyncTask<Integer, Void, List<WNews>> {
    private int count;
    ArrayList<WNews> list = new ArrayList<>();
    String json = "";
    @Override
    protected List<WNews> doInBackground(Integer... params) {

        if(params != null && params.length > 0){
            count = params[0];
        }
        String path = "http://novo.wada.vn/1.0/api/timeline/?lang=en&format=json&count="+count+"&page=2";
        try{
            URL url = new URL(path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null){
                    json += line;
                }
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    JSONArray reader = jsonObject.getJSONArray("layers").getJSONObject(0).getJSONArray("groups");

                    DateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                    long timestamp;
                    for (int i = 0; i < reader.length(); i++) {
                        WNews news = new WNews();
                        JSONObject docs = reader.getJSONObject(i).getJSONArray("docs").getJSONObject(0);
                        news.setAuthor(docs.getString("author"));
                        news.setTitle(docs.getString("title"));
//                            if(docs.getJSONObject("min_image").getString("url")!= null) {
//                                news.setImageUrl(docs.getJSONObject("min_image").getString("url"));
//                            }
                        news.setUrl(docs.getString("url"));
                        timestamp = docs.getLong("timestamp") * 1000;
                        Date date = new Date(timestamp);
                        sdf.format(date);
                        news.setDate(sdf.toString());
                        list.add(news);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    protected void onPostExecute(List<WNews> listNews) {
    }
}
