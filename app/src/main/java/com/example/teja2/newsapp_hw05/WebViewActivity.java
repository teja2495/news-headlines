package com.example.teja2.newsapp_hw05;

/* HW05 - Group 32
Created by
Bala Guna Teja Karlapudi
*/

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Toast;

public class WebViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        setTitle("WebView Activity");

        Bundle extras = getIntent().getExtras();
        String url = extras.getString("url");

        if(isConnected()){
            WebView web =new WebView(WebViewActivity.this);
            setContentView(web);
            web.loadUrl(url);
        }
        else
            Toast.makeText(WebViewActivity.this, "No Internet Connection", Toast.LENGTH_LONG).show();

    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(WebViewActivity.this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            return false;
        }
        return true;
    }
}
