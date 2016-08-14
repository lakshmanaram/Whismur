package com.combatientes.whismur;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class fskEncoder extends AppCompatActivity {

    public static String ENCODER_DATA = "Hello World! This text has been encoded realtime and then fed to audio playback stream and the FSK decoder that actually displays it. ----";
    protected int numberoftries;
    protected FSKConfig mConfig;
    protected FSKEncoder mEncoder;
    protected AudioTrack mAudioTrack;
    protected static TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fsk_encoder);
        numberoftries = 0;
        textView = (TextView) findViewById(R.id.result1);
        Bundle bundle= getIntent().getExtras();
        String message = bundle.getString("message");
        ENCODER_DATA = message + "----";
        try {
            mConfig = new FSKConfig(FSKConfig.SAMPLE_RATE_44100, FSKConfig.PCM_16BIT, FSKConfig.CHANNELS_MONO, FSKConfig.SOFT_MODEM_MODE_4, FSKConfig.THRESHOLD_10P);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        String[] ans = ENCODER_DATA.split("----");
        textView.setText(ans[0]);

        fskencoder f = new fskencoder();
        f.execute();

    }

    protected void createencoderdecoder() {

        mEncoder = new FSKEncoder(mConfig, new FSKEncoder.FSKEncoderCallback() {

            @Override
            public void encoded(byte[] pcm8, short[] pcm16) {
                if (mConfig.pcmFormat == FSKConfig.PCM_16BIT) {

                    mAudioTrack.write(pcm16, 0, pcm16.length);
                }
            }
        });
        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                mConfig.sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, 1024,
                AudioTrack.MODE_STREAM);

        mAudioTrack.play();

    }
    class fskencoder extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            destroyall();
            if(numberoftries < 10) {
                fskencoder newf = new fskencoder();
                newf.execute();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            byte[] data = ENCODER_DATA.getBytes();

            if (data.length > FSKConfig.ENCODER_DATA_BUFFER_SIZE) {
                //chunk data

                byte[] buffer = new byte[FSKConfig.ENCODER_DATA_BUFFER_SIZE];

                ByteBuffer dataFeed = ByteBuffer.wrap(data);

                while (dataFeed.remaining() > 0) {

                    if (dataFeed.remaining() < buffer.length) {
                        buffer = new byte[dataFeed.remaining()];
                    }

                    dataFeed.get(buffer);

                    mEncoder.appendData(buffer);

                    try {
                        Thread.sleep(100); //wait for encoder to do its job, to avoid buffer overflow and data rejection\
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            else {
                mEncoder.appendData(data);
            }
            try {
                Thread.sleep(2000);

            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.d("text received","Interrupted");
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            createencoderdecoder();
            numberoftries++;
        }
    };

    protected void destroyall(){
        mEncoder.stop();

        mAudioTrack.stop();
        mAudioTrack.release();

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
