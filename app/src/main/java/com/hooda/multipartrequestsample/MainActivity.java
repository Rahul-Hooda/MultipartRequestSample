package com.hooda.multipartrequestsample;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private ImageUploadTask imageUploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startRequest(View view){
        imageUploadTask = new ImageUploadTask();
        imageUploadTask.execute();
    }

    public void stopRequest(View view){
        imageUploadTask.cancelTask();
    }

    /**
     * The class connects with server and uploads the photo
     *
     *
     */
    class ImageUploadTask extends AsyncTask<Void, Void, String> {
        private String webAddressToPost = "https://www.payumoney.com/auth/app/file/uploadImageForWebsite";
        private HttpURLConnection conn = null;

        // private ProgressDialog dialog;
        private ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Uploading...");
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(webAddressToPost);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty ("Authorization", "Bearer 0389eeaa-ef63-431c-a0a9-607ebe58c29b");

                MultipartEntity entity = new MultipartEntity(
                        HttpMultipartMode.BROWSER_COMPATIBLE);

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                Bitmap bitmap = BitmapFactory.decodeResource(MainActivity.this.getResources(), R.drawable.ic_image_2);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                byte[] data = bos.toByteArray();
                ByteArrayBody bab = new ByteArrayBody(data, "test.jpg");
                entity.addPart("Filedata", bab);
                entity.addPart("isLogo", new StringBody("1"));
                conn.addRequestProperty("Content-length", entity.getContentLength() + "");
                conn.addRequestProperty(entity.getContentType().getName(), entity.getContentType().getValue());

                OutputStream os = conn.getOutputStream();
                entity.writeTo(conn.getOutputStream());
                os.close();
                conn.connect();


                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    return readStream(conn.getInputStream());
                }


            } catch (Exception e) {
                e.printStackTrace();
                // something went wrong. connection with the server error
            }
            finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
            return null;
        }

        public void cancelTask(){
            this.cancel(true);
            dialog.dismiss();
            if (conn != null){
                conn.disconnect();
                Toast.makeText(MainActivity.this, "Connection Closed !!", Toast.LENGTH_SHORT).show();
            }

        }

        private String readStream(InputStream in) {
            BufferedReader reader = null;
            StringBuilder builder = new StringBuilder();
            try {
                reader = new BufferedReader(new InputStreamReader(in));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return builder.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            Toast.makeText(getApplicationContext(), "file uploaded, result : > "+result,
                    Toast.LENGTH_LONG).show();
        }

    }

/*    private class SendHttpRequestTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                Bitmap bm = BitmapFactory.decodeResource(MainActivity.this.getResources(), R.drawable.ic_image_2);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 75, bos);
                byte[] data = bos.toByteArray();
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost postRequest = new HttpPost(
                        "https://www.payumoney.com/auth/app/file/uploadImageForWebsite");
                ByteArrayBody bab = new ByteArrayBody(data, "forest.jpg");
                MultipartEntity reqEntity = new MultipartEntity(
                        HttpMultipartMode.BROWSER_COMPATIBLE);
                reqEntity.addPart("uploaded", bab);
                reqEntity.addPart("isLogo",new StringBody("1"));
                postRequest.setEntity(reqEntity);
                postRequest.setHeader("Authorization","Bearer 0389eeaa-ef63-431c-a0a9-607ebe58c29b");
                HttpResponse response = httpClient.execute(postRequest);
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        response.getEntity().getContent(), "UTF-8"));
                String sResponse;
                StringBuilder s = new StringBuilder();

                while ((sResponse = reader.readLine()) != null) {
                    s = s.append(sResponse);
                }
                System.out.println("Response: " + s);
            } catch (Exception e) {
                // handle exception here
                Log.e(e.getClass().getName(), e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void data) {
        }

    }*/
}
