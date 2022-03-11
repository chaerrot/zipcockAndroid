package com.kosmo.zipcock;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class NoticeViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_view);

        final Intent intent = getIntent();
        final String num = intent.getStringExtra("mboard_num");
        final String title = intent.getStringExtra("mboard_title");
        final String id = intent.getStringExtra("mboard_id");
        final String content = intent.getStringExtra("mboard_content");

        ((TextView)findViewById(R.id.ViewNum)).setText(String.format(num));
        ((TextView)findViewById(R.id.ViewTitle)).setText(String.format(title));
        ((TextView)findViewById(R.id.ViewId)).setText(String.format(id));
        ((TextView)findViewById(R.id.ViewContent)).setText(String.format(content));
    }
}