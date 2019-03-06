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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ArrayAdapter<String> adapter;
    List<sources> sourceList;
    List<String> sourceNames;
    sources source;
    ListView sc;
    ProgressBar pb;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sourceList=new ArrayList<>();
        sourceNames=new ArrayList<>();
        sc=findViewById(R.id.listView);
        pb=findViewById(R.id.progressBar1);
        tv=findViewById(R.id.text1);
        setTitle("Main Activity");

        sc.setVisibility(View.INVISIBLE);

        if(isConnected()){
            new getSources().execute("https://newsapi.org/v2/sources?apiKey=f01e64e08a69429f90d67de996d08569");
        }
        else
            Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_LONG).show();



        sc.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                if(isConnected()){
                    Intent myIntent = new Intent(MainActivity.this, SecondActivity.class);
                    myIntent.putExtra("source", sourceList.get(position));
                    startActivity(myIntent);
                }
                else
                    Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_LONG).show();
            }
        });
    }

    class getSources extends AsyncTask< String, Integer, List<sources>>{


        @Override
        protected void onPostExecute(List<sources> string) {
            super.onPostExecute(string);
            for(int i=0;i<string.size();i++){
                sourceNames.add(string.get(i).getName());
            }
            adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, sourceNames);
            sc.setAdapter(adapter);

            pb.setVisibility(View.INVISIBLE);
            tv.setVisibility(View.INVISIBLE);
            sc.setVisibility(View.VISIBLE);

        }

        @Override
        protected List<sources> doInBackground(String...strings) {
            try {
                String json = getAPIData(strings[0]).toString();
                JSONObject root = new JSONObject(json);
                JSONArray sources = root.getJSONArray("sources");
                for (int i = 0; i < sources.length(); i++) {
                    JSONObject sourceJSON = new JSONObject();
                    sourceJSON = sources.getJSONObject(i);
                    source = new sources();
                    source.setId(sourceJSON.getString("id"));
                    source.setName(sourceJSON.getString("name"));
                    sourceList.add(source);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return sourceList;
        }
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

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(MainActivity.this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            return false;
        }
        return true;
    }
}
