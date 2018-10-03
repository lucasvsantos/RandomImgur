package br.com.lucasvsantos.randomimgur;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    ImageView imgBaixada;
    ProgressDialog progressDialog;

    String charAll = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgBaixada = findViewById(R.id.imgBaixada);

    }

    public void download(View view) {
        DownloadAsyncTask downloadAsyncTask = new DownloadAsyncTask();

        int r;
        String t = "http://i.imgur.com/";
//        String t = "i.imgur.com/";

        for (int i = 0; i < 5; i++) {
            Double n = Math.floor(Math.random() * charAll.length());
            r = n.intValue();
            t += charAll.charAt(r);
        }

//        downloadAsyncTask.execute("http://maxippoi.s365.xrea.com/imgurproxy.php/"+t+".jpg");
        downloadAsyncTask.execute(t + ".jpg");
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
                Bitmap imagem;

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