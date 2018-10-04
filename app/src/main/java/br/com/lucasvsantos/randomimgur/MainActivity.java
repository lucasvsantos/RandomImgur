package br.com.lucasvsantos.randomimgur;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgBaixada = findViewById(R.id.imgBaixada);

    }

    public void generateImage(View view) {
        DownloadAsyncTask downloadAsyncTask = new DownloadAsyncTask();

        int r;
        String t = "http://i.imgur.com/";
//        String t = "http://i.imgur.com/UHIIH.jpg";

        for (int i = 0; i < 5; i++) {
            Double n = Math.floor(Math.random() * charAll.length());
            r = n.intValue();
            t += charAll.charAt(r);
        }

        downloadAsyncTask.execute(t + ".jpg");
    }

    public void saveImage(Context context, Bitmap b, String imageName) {
        FileOutputStream foStream;
        try {
            foStream = context.openFileOutput(imageName, Context.MODE_PRIVATE);
            b.compress(Bitmap.CompressFormat.PNG, 100, foStream);
            foStream.close();
        } catch (Exception e) {
            Toast.makeText(context, "Não foi possível salvar a imagem!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public void download(View view) {
        String fileName = String.valueOf("Arthur " + Calendar.getInstance().getTimeInMillis() + ".jpg");

        saveImage(getBaseContext(), imagem, fileName);

        Toast.makeText(this, "Imagem salva!", Toast.LENGTH_LONG).show();
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