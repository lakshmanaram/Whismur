package com.combatientes.whismur;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

public class fskMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fsk_main);
        ImageButton share = (ImageButton) findViewById(R.id.share);
        ImageButton listen = (ImageButton) findViewById(R.id.listen);
        final EditText txt = (EditText) findViewById(R.id.fsktxt);
        assert share != null;
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), fskEncoder.class);
                intent.putExtra("message", txt.getText().toString());
                startActivity(intent);
            }
        });
        assert listen != null;
        listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), fskDecoder.class);
                intent.putExtra("message", txt.getText().toString());
                startActivity(intent);
            }
        });
    }
}
