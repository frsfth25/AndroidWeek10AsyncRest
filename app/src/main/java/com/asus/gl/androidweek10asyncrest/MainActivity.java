package com.asus.gl.androidweek10asyncrest;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


public class MainActivity extends AppCompatActivity {
    public static String TAG = "ASYNC";

    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




    }

    public void onDownload(View view) {
        try {
            URL uri = new URL("http://ybu.edu.tr/muhendislik/bilgisayar/contents/images/3855.jpg");
            ImageDownloader downloader = new ImageDownloader();
            Log.i(TAG, "Async task starting..");
            downloader.execute(uri);

        }catch (MalformedURLException e){
            Log.e(TAG, "Error on URL:" + e.getLocalizedMessage());
        }


    }

    public void getJoke(View view) {
        YoYo.with(Techniques.Landing).duration(1000).playOn(findViewById(R.id.imgViewDownloaded));
        YoYo.with(Techniques.FadeIn).duration(1000).playOn(findViewById(R.id.startDownload));

        Ion.with(this)
                .load("http://api.icndb.com/jokes/random")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        Log.i(TAG,result.toString());
                        parseJoke(result);

                    }
                });

    }

    //{
    //  "type":"success",
    //  "value":    {
    //                  "id":549,
    //                  "joke":"Chuck Norris killed two stones with one bird.",
    //                  "categories":[]
    //              }
    // }
    private void parseJoke(JsonObject obj){
        String joke = obj.getAsJsonObject("value").get("joke").getAsString();
        TextView t = findViewById(R.id.txtJOKE);
        t.setText(joke);


    }

    private class ImageDownloader extends AsyncTask<URL,Integer,Bitmap>{

        @Override
        protected void onPreExecute() {
            //super.onPreExecute();
            Log.i(TAG,"onPreExecute is called.");
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setTitle(getResources().getString(R.string.imgdownloadwarn));
            progressDialog.setMax(100);
            progressDialog.setProgress(0);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.show();
        }

        @Override
        protected Bitmap doInBackground(URL... urls) {
            Log.i(TAG,"doInBackground is called.");
            Log.i(TAG,"User URL param count =" + urls.length);
            URL currentURL = urls[0];
            Bitmap resultingBitmap;


            try {

                for(int i=0;i<10;i++){
                    Thread.sleep(100);
                    publishProgress(i+1);
                }

                //now download image
                URLConnection connection = currentURL.openConnection();
                connection.connect();
                InputStream is = connection.getInputStream();
                if(is!=null){
                    resultingBitmap = BitmapFactory.decodeStream(is);
                    return resultingBitmap;
                }
            }catch (Exception e){
                Log.e(TAG, e.getLocalizedMessage());
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            //super.onProgressUpdate(values);
            Log.i(TAG,"onProgressUpdate is called.");
            int currentVal = values[0];
            progressDialog.setProgress(currentVal*10);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            //super.onPostExecute(bitmap);
            Log.i(TAG,"onPostExecute is called.");
            ImageView imgView = findViewById(R.id.imgViewDownloaded);
            if(bitmap !=null){
                imgView.setImageBitmap(bitmap);
            }
            else{
                Log.i(TAG,"There is no image on the URL.");
            }

            progressDialog.hide();

        }
    }
}
