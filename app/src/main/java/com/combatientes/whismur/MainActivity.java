package com.combatientes.whismur;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent); // Handle text being sent
            }
        }
    }

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            // TODO: Share the given text or url
        }
    }
    ImageButton record;
    public void record(View v){
        record=(ImageButton)findViewById(R.id.record);
        record.setBackground(getDrawable(R.drawable.stop));
    }
    public void play(View view){
        Intent intent=new Intent(getApplicationContext(),Play.class);
        startActivity(intent);
    }
    public void openURL(View view){
        EditText editText=(EditText)findViewById(R.id.url);
        if(editText.getText()!=null){ openUrlInChrome(editText.getText().toString());return;}
        else{
            Log.d("openURL","null edittext");
        }

    }
    void openUrlInChrome(String url) {
       /* Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);*/


        try {
            Uri uri = Uri.parse("googlechrome://navigate?url="+ url);
            Intent i = new Intent(Intent.ACTION_VIEW, uri);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        } catch (ActivityNotFoundException e) {
            Uri uri = Uri.parse(url);
            // Chrome is probably not installed
            // OR not selected as default browser OR if no Browser is selected as default browser
            Intent i = new Intent(Intent.ACTION_VIEW, uri);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }
    }
}
