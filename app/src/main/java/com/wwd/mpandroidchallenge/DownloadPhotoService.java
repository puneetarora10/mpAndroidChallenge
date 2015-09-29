package com.wwd.mpandroidchallenge;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;

import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Downloads Person's Photo
 */
public class DownloadPhotoService extends Service {
    private Messenger peopleActivityMessenger;
    // Sets the amount of time an idle thread waits before terminating
    private static final int KEEP_ALIVE_TIME = 1;
    // Sets the Time Unit to seconds
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
    /*
     * Gets the number of available cores
     * (not always the same as the maximum number of cores)
     */
    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    // A queue of Runnables
    private final BlockingQueue<Runnable> mDecodeWorkQueue = new LinkedBlockingQueue<Runnable>();


    public DownloadPhotoService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        // get extras using intent
        peopleActivityMessenger = intent.getParcelableExtra("messenger");
        int position = intent.getIntExtra("position", 0);
        String photoUrlString = intent.getStringExtra("photoUrlString");
        String photoLocalName = intent.getStringExtra("photoLocalName");

        // threadPoolExecutor
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(NUMBER_OF_CORES, NUMBER_OF_CORES, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, mDecodeWorkQueue);

        Runnable downloadPhotoThread = new DownloadPhoto(position, photoUrlString, photoLocalName);
        threadPoolExecutor.execute(downloadPhotoThread);

        return START_STICKY;
    }

    /**
     * Downloads photo
     *
     */
    private class DownloadPhoto implements Runnable {
        private int position;
        private String photoUrlString;
        private String photoLocalName;

        public DownloadPhoto(int position1, String photoUrlString1, String photoLocalName1) {
            position = position1;
            photoUrlString = photoUrlString1;
            photoLocalName = photoLocalName1;
        }

        @Override
        public void run() {
            HttpURLConnection httpURLConnection = null;
            try {
                URL imageUrl = new URL(photoUrlString);
                // open connection
                httpURLConnection = (HttpURLConnection) imageUrl.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setInstanceFollowRedirects(true);

                // handle redirects
                if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM || httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP || httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_SEE_OTHER) {// redirect from http -> https or https -> http
                    // open connection
                    httpURLConnection = (HttpURLConnection) httpURLConnection.getURL().openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setInstanceFollowRedirects(true);
                }
                if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    // openFileOutput using MODE_APPEND
                    FileOutputStream fOut = new FileOutputStream(new File(getFilesDir(), photoLocalName));
                    Bitmap bitmap = BitmapFactory.decodeStream(httpURLConnection.getInputStream());
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                    fOut.flush();
                    fOut.close();

                    // send message to downloadPhotoHandler
                    Bundle bundle = new Bundle();
                    bundle.putInt("position", position);
                    bundle.putString("photoLocalName", photoLocalName);
                    Message message = Message.obtain();
                    message.setData(bundle);
                    peopleActivityMessenger.send(message);
                }
            } catch (Exception e) {
                // no need to do anything right now
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }
        }
    }
}
