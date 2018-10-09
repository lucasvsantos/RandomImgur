package br.com.lucasvsantos.randomimgur;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    ImageView imgBaixada;
    ProgressDialog progressDialog;
    Bitmap imagem;

    String charAll = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermission()) {
                Toast.makeText(this, "Permissão concedida!", Toast.LENGTH_SHORT).show();
            } else {
                requestPermission();
            }
        } else {
            // Code for Below 23 API Oriented Device
            // Do next code
        }

        imgBaixada = findViewById(R.id.imgBaixada);
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(MainActivity.this, "Precisamos de permissão para salvar a imagem, caso contrário, não será possível.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }

    public void generateImage(View view) {
        DownloadAsyncTask downloadAsyncTask = new DownloadAsyncTask();

        int r;
        StringBuilder t = new StringBuilder("http://i.imgur.com/");

        for (int i = 0; i < 5; i++) {
            Double n = Math.floor(Math.random() * charAll.length());
            r = n.intValue();
            t.append(charAll.charAt(r));
        }

        downloadAsyncTask.execute(t + ".jpg");
    }

    public void saveImage(Context context, Bitmap b, String imageName) {
        FileOutputStream foStream;
        if (checkPermission()) {
            Toast.makeText(this, "Permissão concedida!", Toast.LENGTH_SHORT).show();
            try {
                foStream = context.openFileOutput(imageName, Context.MODE_PRIVATE);
//            foStream = new FileOutputStream(file);
                b.compress(Bitmap.CompressFormat.PNG, 100, foStream);

                foStream.close();
                Toast.makeText(context, "Imagem salva em: " + getFilesDir().getAbsolutePath(), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(context, "Não foi possível salvar a imagem!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else {
            requestPermission();
        }
    }

    public void download(View view) {
        String fileName = String.valueOf("IMG_" + Calendar.getInstance().getTimeInMillis() + ".png");
        saveImage(getBaseContext(), imagem, fileName);
    }

    class DownloadAsyncTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(
                    MainActivity.this,
                    "Aguarde...",
                    "Gerando sua imagem");
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                InputStream inputStream;

                URL endereco = new URL(strings[0]);

                inputStream = endereco.openStream();
                imagem = BitmapFactory.decodeStream(inputStream);

                inputStream.close();

                return imagem;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            if (bitmap != null) {
                imgBaixada.setImageBitmap(bitmap);
            }

            progressDialog.dismiss();
        }
    }
}