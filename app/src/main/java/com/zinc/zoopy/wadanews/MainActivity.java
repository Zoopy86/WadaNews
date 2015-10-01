package com.zinc.zoopy.wadanews;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements EndlessListView.EndlessListener {
    private final static int ITEM_PER_REQUEST = 10;
    EndlessListView lv;
    List<WNews> list;
    int count = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv = (EndlessListView) findViewById(R.id.el);


        EndlessAdapter adp = new EndlessAdapter(this, new ArrayList<WNews>(), R.layout.row_layout);
        lv.setLoadingView(R.layout.loading_layout);
        lv.setAdapter(adp);

        lv.setListener(this);
        new NewsLoader().execute(count);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private class NewsLoader extends AsyncTask<Integer, Void, List<WNews>> {
        String json = "";
        @Override
        protected List<WNews> doInBackground(Integer... params) {
            list = new ArrayList<>();
            if(params != null && params.length > 0){
                count = params[0];
            }
            String path = "http://novo.wada.vn/1.0/api/timeline/?lang=en&format=json&count="+count+"&page=2";
            try {
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

                        DateFormat sdf = new SimpleDateFormat("HH:mm");
                        long timestamp;
                        for (int y = 0; y < reader.length(); y++) {
                            WNews news = new WNews();
                            JSONObject docs = reader.getJSONObject(y).getJSONArray("docs").getJSONObject(0);
                            if(!docs.isNull("author")) {
                                news.setAuthor(docs.getString("author"));
                            }
                            else news.setAuthor("Unknown");
                            news.setTitle(docs.getString("title"));
                            if(!docs.isNull("thumbnail")) {
                                    news.setImageUrl(docs.getJSONObject("thumbnail").getString("url"));
                            }

                            news.setUrl(docs.getString("url"));
                            timestamp = docs.getLong("timestamp") * 1000;
                            Calendar c = Calendar.getInstance();
                            c.setTimeInMillis(timestamp);
                            String time = sdf.format(c.getTime());
                            news.setDate(time);
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
        protected void onPostExecute(List<WNews> result) {
            super.onPostExecute(result);
            Log.d("WADA", "on post execute");
            lv.addNewData(result);
        }
    }

    @Override
    public void loadData() {
        System.out.println("Loading data");
        count += ITEM_PER_REQUEST;

        // We load more data here
        NewsLoader nl = new NewsLoader();
        nl.execute(count);

    }

}
