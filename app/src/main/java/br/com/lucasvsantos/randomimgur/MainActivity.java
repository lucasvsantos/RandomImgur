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
import android.widget.Button;
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
    Button button;
    String charAll = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE }, 10);
        }
        imgBaixada = findViewById(R.id.imgBaixada);
        button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateImage();
            }
        });
    }

    public void generateImage(){
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
        try {
            foStream = context.openFileOutput(imageName, Context.MODE_PRIVATE);
            b.compress(Bitmap.CompressFormat.PNG, 100, foStream);

            foStream.close();
            Toast.makeText(context, "Imagem salva em: " + getFilesDir().getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context, "Não foi possível salvar a imagem!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void download(View view) {
        String fileName = String.valueOf("IMG_" + Calendar.getInstance().getTimeInMillis() + ".png");
        saveImage(getBaseContext(), imagem, fileName);
    }

    class DownloadAsyncTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            if(progressDialog == null || !progressDialog.isShowing()) {
                progressDialog = ProgressDialog.show(
                        MainActivity.this,
                        "Aguarde...",
                        "Gerando sua imagem");
            }
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
                if (bitmap.getWidth() == 161 && bitmap.getHeight() == 81){
                    generateImage();
                } else {
                    imgBaixada.setImageBitmap(bitmap);
                    progressDialog.dismiss();
                }
            }
        }

    }
}