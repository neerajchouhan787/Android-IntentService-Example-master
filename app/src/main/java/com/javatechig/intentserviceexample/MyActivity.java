package com.javatechig.intentserviceexample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MyActivity extends Activity implements DownloadResultReceiver.Receiver {
    private ListView listView = null;
    private Button btn;
    private ArrayAdapter arrayAdapter = null;
    private DownloadResultReceiver downloadResultReceiver;
    //final String url = "http://javatechig.com/api/get_category_posts/?dev=1&slug=android";
    //https://www.stacktips.com/api/get_category_posts/?dev=1&slug=android
    final String url = "https://stacktips.com/api/get_category_posts/?dev=1&slug=android";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* Allow activity to show indeterminate progressbar */
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        /* Set activity layout */
        setContentView(R.layout.activity_my);
        /* Initialize listView */
        listView = (ListView) findViewById(R.id.listView);
        btn = (Button) findViewById(R.id.btn);
        /* Starting Download Service */
        downloadResultReceiver = new DownloadResultReceiver(new Handler());
        downloadResultReceiver.setReceiver(this);
        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, DownloadService.class);
        /* Send optional extras to Download IntentService */
        intent.putExtra("url", url);
        intent.putExtra("receiver", downloadResultReceiver);
        intent.putExtra("requestId", 101);
        startService(intent);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), SecActivity.class);
                startActivity(i);
                finish();
            }});
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        System.out.println("MyActivity resultCode "+resultCode);
        ArrayList alist = new ArrayList();
        switch (resultCode) {

            case DownloadService.STATUS_RUNNING:
                setProgressBarIndeterminateVisibility(true);
                break;

            case DownloadService.STATUS_FINISHED:
                /* Hide progress & extract result from bundle */
                setProgressBarIndeterminateVisibility(false);
                String[] results = resultData.getStringArray("result");
                for(String a : results){
                    System.out.println("MyActivity result a "+a);
                    alist.add(a);
                }
                /* Update ListView with result */
                //arrayAdapter = new ArrayAdapter(MyActivity.this, android.R.layout.simple_list_item_2, results);
                arrayAdapter = new ArrayAdapter(MyActivity.this, R.layout.row_list, results);
                listView.setAdapter(arrayAdapter);
                break;

            case DownloadService.STATUS_ERROR:
                /* Handle the error */
                String error = resultData.getString(Intent.EXTRA_TEXT);
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                break;
        }
    }
}