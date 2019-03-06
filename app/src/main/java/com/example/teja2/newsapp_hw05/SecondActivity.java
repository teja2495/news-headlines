package com.example.teja2.newsapp_hw05;

/* HW05 - Group 32
Created by
Bala Guna Teja Karlapudi
*/

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SecondActivity extends AppCompatActivity {

    List<news> newsList;

    String src_id=null, thread_id=null;
    ListView headlines;
    ProgressBar pb;
    TextView tv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        sources s=new sources();

        headlines=findViewById(R.id.listView2);
        newsList=new ArrayList<>();
        Bundle extras = getIntent().getExtras();
        s=(sources)extras.getSerializable("source");
        src_id = s.getId();
        setTitle(s.getName());
        pb=findViewById(R.id.progressBar2);
        tv=findViewById(R.id.text2);

        headlines.setVisibility(View.INVISIBLE);

        if(isConnected()){
            new getNews().execute("https://newsapi.org/v2/top-headlines?sources="+src_id+"&apiKey=f01e64e08a69429f90d67de996d08569");
        }
        else
            Toast.makeText(SecondActivity.this, "No Internet Connection", Toast.LENGTH_LONG).show();


        headlines.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(isConnected()){
                    Intent myIntent = new Intent(SecondActivity.this, WebViewActivity.class);
                    myIntent.putExtra("url", newsList.get(position).getUrlToArticle());
                    startActivity(myIntent);
                }
                else
                    Toast.makeText(SecondActivity.this, "No Internet Connection", Toast.LENGTH_LONG).show();
                }
            });
    }

    class customAdapter extends BaseAdapter{
        List<news> displayList;

        public customAdapter(List<news> list) {
            this.displayList=list;
        }

        @Override
        public int getCount() {
            return displayList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            view = getLayoutInflater().inflate(R.layout.custom_layout, null);
            ImageView imageView=view.findViewById(R.id.imageView);
            TextView title=view.findViewById(R.id.title);
            TextView publishedAt=view.findViewById(R.id.publishedAt);
            TextView author=view.findViewById(R.id.author);

            title.setText(displayList.get(position).getTitle());
            author.setText(displayList.get(position).getAuthor());
            publishedAt.setText(displayList.get(position).getPublishedAt());
            if(displayList.get(position).getUrlToImage().equals(""))
                Picasso.get().load("http://gceburdwan.in/admission/images/noimage.jpg").into(imageView);
            else
                Picasso.get().load(displayList.get(position).getUrlToImage()).into(imageView);

            return view;
        }
    }

    class getNews extends AsyncTask< String, Integer, List<news>> {

        @Override
        protected void onPostExecute(List<news> list) {
            customAdapter customAdapter=new customAdapter(list);
            headlines.setAdapter(customAdapter);
            pb.setVisibility(View.INVISIBLE);
            tv.setVisibility(View.INVISIBLE);
            headlines.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<news> doInBackground(String...strings) {
            try {
                String json = getAPIData(strings[0]).toString();
                JSONObject root = new JSONObject(json);
                JSONArray newsArray = root.getJSONArray("articles");
                for (int i = 0; i < newsArray.length(); i++) {
                    JSONObject newsJSON;
                    newsJSON = newsArray.getJSONObject(i);
                    news articles=new news();
                    articles.setTitle(newsJSON.getString("title"));
                    articles.setPublishedAt(newsJSON.getString("publishedAt").substring(0, Math.min(newsJSON.getString("publishedAt").length(), 10)));
                    articles.setUrlToArticle(newsJSON.getString("url"));
                    articles.setUrlToImage(newsJSON.getString("urlToImage"));
                    articles.setAuthor(newsJSON.getString("author"));
                    newsList.add(articles);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return newsList;
        }
    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(SecondActivity.this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            return false;
        }
        return true;
    }

    StringBuilder getAPIData(String strUrl) {
        StringBuilder result = new StringBuilder();
        HttpURLConnection connection = null;
        try {
            URL url = new URL(strUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = r.readLine()) != null) {
                    result.append(line).append('\n');
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
        return result;
    }
}
