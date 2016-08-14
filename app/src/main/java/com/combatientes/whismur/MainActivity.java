package com.combatientes.whismur;


import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.net.URL;

import io.chirp.sdk.CallbackRead;
import io.chirp.sdk.ChirpSDK;
import io.chirp.sdk.ChirpSDKListener;
import io.chirp.sdk.model.Chirp;
import io.chirp.sdk.model.ChirpError;

public class MainActivity extends Activity {

    ImageButton send;
//    ImageButton listen;
    ChirpSDK chirpSDK;
    Context context;
    EditText message;
    private final String TAG="DEBUG";
    TextView result;
    SharedPreferences sharedPreferences;
    private static final int RESULT_REQUEST_RECORD_AUDIO = 0;

    private static boolean play=true;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(com.combatientes.whismur.R.layout.activity_main);

        context=getApplicationContext();
        send= (ImageButton)findViewById(com.combatientes.whismur.R.id.send);
        sharedPreferences=getSharedPreferences("User details", MODE_PRIVATE);

        /*Fab fab = (Fab) findViewById(R.id.fab);
        View sheetView = findViewById(R.id.fab_sheet);
        View overlay = findViewById(R.id.dim_overlay);
        int sheetColor = getResources().getColor(R.color.fab_sheet_color);
        int fabColor = getResources().getColor(R.color.fab_color);

        // Initialize material sheet FAB
        materialSheetFab = new MaterialSheetFab<>(fab, sheetView, overlay,
                sheetColor, fabColor);*/
        //listen=(ImageButton)findViewById(com.combatientes.whismur.R.id.listen);
        result=(TextView)findViewById(R.id.result);
        try {
            Intent intent = getIntent();
            String action = intent.getAction();
            String type = intent.getType();

            if (Intent.ACTION_SEND.equals(action) && type != null) {
                    handleSendText(intent); // Handle text being sent
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        message=(EditText)findViewById(com.combatientes.whismur.R.id.message);

        chirpSDK = new ChirpSDK(context,"ErnLh5hPX7GQGNOnX7OgYrT5N","LshAQXDH3JISYOsRoXlfWKUoAxloskvDpBj9YDG8Vq8xocwFfI");

        chirpSDK.setListener(chirpSDKListener);

        send.setOnClickListener(Send);
//        listen.setOnClickListener(Start);


    }
    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {

            try {
                Intent inten = new Intent(getApplicationContext(), Send.class);
                inten.putExtra("message", sharedText);
                startActivity(inten);
            }catch (Exception e){
                Toast.makeText(getApplicationContext(),"Unable to Send",Toast.LENGTH_LONG).show(); ;
                e.printStackTrace();
            }
        }
    }
    /*@Override
    public void onBackPressed() {
        if (materialSheetFab.isSheetVisible()) {
            materialSheetFab.hideSheet();
        } else {
            super.onBackPressed();
        }
    }*/
    private ChirpSDKListener chirpSDKListener = new ChirpSDKListener()
    {
        /*------------------------------------------------------------------------------
         * onChirpHeard is triggered when a Chirp tone is received.
         * Obtain the chirp's 10-character identifier with `getIdentifier`.
         *----------------------------------------------------------------------------*/
        @Override
        public void onChirpHeard(Chirp chirp)
        {
            /*------------------------------------------------------------------------------
             * We're encoding the properties of each gem within the identifier:
             * Position, orientation, and colour.
             *
             * Create and display a new gem with these properties.
             *----------------------------------------------------------------------------*/
            final String gemId = chirp.getIdentifier();
            Log.d(TAG, "Chirp heard: " + gemId);
            readChirp(chirp);

        }

        /*------------------------------------------------------------------------------
         * onChirpError is triggered when an error occurs -- for example,
         * authentication failure or muted device.
         *
         * See the documentation on ChirpError for possible error codes.
         *----------------------------------------------------------------------------*/
        @Override
        public void onChirpError(ChirpError chirpError) {
            Log.d(TAG, "Identifier received error: " + chirpError.getMessage());
        }
    };

//    OnClickListener Start=new OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            if(play){
//                chirpSDK.stop();
//                listen.setImageResource(android.R.color.transparent);
//                listen.setBackground(getDrawable(com.combatientes.whismur.R.drawable.play));
//                play=false;
//            }
//            else{
//                chirpSDK.start();
//                listen.setImageResource(android.R.color.transparent);
//                listen.setBackground(getDrawable(com.combatientes.whismur.R.drawable.stop));
//                play=true;
//            }
//
//        }
//    };



    OnClickListener Send=new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (message.getText() != null) {
                try {
                    Intent intent = new Intent(context, Send.class);
                    intent.putExtra("message", message.getText().toString());
                    startActivity(intent);
                }catch (Exception e){
                    Toast.makeText(context,"Unable to Send",Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }
    };




    @Override
    protected void onResume() {
        super.onResume();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.RECORD_AUDIO}, RESULT_REQUEST_RECORD_AUDIO);
        }
        else {
            chirpSDK.start();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RESULT_REQUEST_RECORD_AUDIO: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    chirpSDK.start();
                }
                return;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        chirpSDK.stop();
    }
    private void readChirp(Chirp chirp)
    {
        /*------------------------------------------------------------------------------
         * ChirpSDK.read queries the Chirp API for extended data associated with a
         * given chirp. It requires an internet connection.
         *----------------------------------------------------------------------------*/
        chirpSDK.read(chirp, new CallbackRead()
        {
            /*------------------------------------------------------------------------------
             * The associated data is a single JSON structured object of key-value pairs.
             * You can define arbitrary nested data structure within this.
             * Here, we simply retrieve the "text" key.
             *----------------------------------------------------------------------------*/
            @Override
            public void onReadResponse(final Chirp chirp)
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final String receivedText;
                        try {
                            receivedText = (String) chirp.getJsonData().get("text");
                            result.setText(receivedText);
                            if(Url_check(receivedText)){
                                openUrlInChrome(receivedText);
                            }
                            else {
                                Toast.makeText(context,"Not a link",Toast.LENGTH_LONG);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }



                    }
                });
                Log.d(TAG, "onReadResponse: ");
            }

            /*------------------------------------------------------------------------------
             * If an error occurs contacting the Chirp API, generate an error.
             *----------------------------------------------------------------------------*/
            @Override
            public void onReadError(ChirpError chirpError) {
                try{
                    /*runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Error reading Chirp", Toast.LENGTH_SHORT).show();
                        }
                    });*/

                    Log.d(TAG, "onReadError: " + chirpError.getMessage());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
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
    boolean Url_check(String s){
        if (!s.contains("http://"))
            s= "http://" + s;

        URL url=null;
        try {
            url = new URL(s);
        } catch (Exception e) {
            Log.v("myApp", "bad url entered");
            return false;
        }
        if (url == null)
            return false;
        else
            return true;
    }
   /* public byte[] extractBytes (String ImageName) throws IOException {
        // open image
        File imgPath = new File(ImageName);
        BufferedImage bufferedImage = ImageIO.read(imgPath);

        // get DataBufferBytes from Raster
        WritableRaster raster = bufferedImage .getRaster();
        DataBufferByte data   = (DataBufferByte) raster.getDataBuffer();

        return ( data.getData() );
    }*/
    public String shareImage(Bitmap someImg){
        String imgString = Base64.encodeToString(getBytesFromBitmap(someImg),
                Base64.NO_WRAP);
        return imgString;

    }
    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        return stream.toByteArray();
    }
}



