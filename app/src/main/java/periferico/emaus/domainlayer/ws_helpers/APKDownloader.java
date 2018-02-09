package periferico.emaus.domainlayer.ws_helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import periferico.emaus.BuildConfig;
import periferico.emaus.presentationlayer.activities.Splash;

/**
 * Created by maubocanegra on 08/01/18.
 */

public class APKDownloader extends AsyncTask<Activity, Void ,Void> {

    private Uri succesUri;

    public Uri getSuccesUri() {
        return succesUri;
    }

    public void setSuccesUri(Uri succesUri) {
        this.succesUri = succesUri;
    }

    /*
    public synchronized static APKDownloader getInstance(Context c){
        if(instance==null){
            instance = new APKDownloader();
            context=c;
        }

        return instance;
    }
    */

    @Override
    protected Void doInBackground(Activity... contexts) {
        try{
            File file = new File(Environment.getExternalStorageDirectory(), "EMAUS.apk");
            final Uri uri = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) ?
                    FileProvider.getUriForFile(contexts[0], BuildConfig.APPLICATION_ID + ".periferico.emaus.provider", file) :
                    Uri.fromFile(file);

            //Delete update file if exists
            //File file = new File(destination);
            if (file.exists())
                //file.delete() - test this, I think sometimes it doesnt work
                file.delete();

            //get url of app on server
            String url = succesUri.toString();

            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL sUrl = new URL(url);
                connection = (HttpURLConnection) sUrl.openConnection();
                connection.connect();

                // download the file
                input = connection.getInputStream();
                output = new FileOutputStream(file);

                byte data[] = new byte[4096];
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button

                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }

            Intent install = new Intent(Intent.ACTION_INSTALL_PACKAGE)
                    .setData(uri)
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            contexts[0].startActivity(install);
            contexts[0].finish();
        }catch(Exception e){e.printStackTrace();}
        return null;
    }
}
