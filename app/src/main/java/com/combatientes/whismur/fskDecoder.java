package com.combatientes.whismur;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class fskDecoder extends AppCompatActivity {


    protected FSKConfig mConfig;
    protected FSKDecoder mDecoder;

    protected TextView view;
    protected ArrayList<String> readdata = new ArrayList<>();

    protected String requiredtext;
    protected boolean gottext;

    protected AudioRecord mRecorder;
    protected int mBufferSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fsk_decoder);
        view = (TextView) findViewById(R.id.result3);
        try {
            mConfig = new FSKConfig(FSKConfig.SAMPLE_RATE_44100, FSKConfig.PCM_16BIT, FSKConfig.CHANNELS_MONO, FSKConfig.SOFT_MODEM_MODE_4, FSKConfig.THRESHOLD_10P);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        gottext = false;

        fskdecoder fd = new fskdecoder();
        fd.execute();
    }

    protected void destroyall() {
        mRecorder.stop();
        mRecorder.release();

        mDecoder.stop();

    }

    protected void initialize() {
        mDecoder = new FSKDecoder(mConfig, new FSKDecoder.FSKDecoderCallback() {

            @Override
            public void decoded(byte[] newData) {

                final String text = new String(newData);

                runOnUiThread(new Runnable() {
                    public void run() {

                        CharSequence received_till_now = view.getText()+text;
                        view.setText(received_till_now);
//                        if(((String)recieved_till_now).contains("----")) {
//                            String[] ans = ((String)received_till_now).split("----");
//                            if(readdata.size()==0||!readdata.contains(ans[0])){
//                                readdata.add(ans[0]);
//                                view.setText(ans[1]);
//                            } else if (readdata.contains(ans[0])){
//                                requiredtext = ans[0];
//                                gottext = true;
//                            }
//                            Toast.makeText(getApplicationContext(),ans[0]+" text consumed",Toast.LENGTH_SHORT).show();
//                        }
                    }
                });
            }
        });

        ///

        //make sure that the settings of the recorder match the settings of the decoder
        //most devices cant record anything but 44100 samples in 16bit PCM format...
        mBufferSize = AudioRecord.getMinBufferSize(FSKConfig.SAMPLE_RATE_44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);

        //scale up the buffer... reading larger amounts of data
        //minimizes the chance of missing data because of thread priority
        mBufferSize *= 10;

        //again, make sure the recorder settings match the decoder settings
        mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, FSKConfig.SAMPLE_RATE_44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, mBufferSize);
    }
    @Override
    protected void onDestroy() {

        super.onDestroy();
    }
    class fskdecoder extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... params) {
            if (mRecorder.getState() == AudioRecord.STATE_INITIALIZED) {
                mRecorder.startRecording();
                while (mRecorder.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING && !gottext) {

                    short[] data = new short[mBufferSize/2]; //the buffer size is in bytes

                    // gets the audio output from microphone to short array samples
                    mRecorder.read(data, 0, mBufferSize/2);

                    mDecoder.appendSignal(data);
                }
            }
            else {
                Log.i("FSKDecoder", "Please check the recorder settings, something is wrong!");
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            initialize();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    view.setText(requiredtext);
                }
            });
            destroyall();

        }
    }
}
