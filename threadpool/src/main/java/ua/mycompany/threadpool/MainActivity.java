package ua.mycompany.threadpool;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    final String LOG_TAG = "myLogs";
    GridView gridView;
    String[] urlArray;
    Bitmap[] pictures;
    static int t = -1;
    long filesLeftToDownload = 0;
    long filesDownloaded = 0;
    long averageTime = 0;
    long totalTime = 0;
    int quantityOfFiles = 100;
    long remainingTime = 0;
    static int barProgress = 0;
    TextView tVDownloadingText;
    ProgressBar progressBar;



    static Handler h;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //fill array of url
        urlArray = createArrayOfURL();

        gridView = (GridView) findViewById(R.id.gridView);
        tVDownloadingText = (TextView) findViewById(R.id.downloadingText);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(100);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.attachToListView(gridView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doExecutorService();
            }
        });


        pictures = new Bitmap[urlArray.length+10];

        h = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                addGridView();
                remainingTime = (remainingTime - (averageTime)/5);
                Long tmp = TimeUnit.MILLISECONDS.toSeconds(remainingTime);

                progressBar.setProgress((barProgress++) * (100 / quantityOfFiles));
                tVDownloadingText.setText("Downloading ends approximately in " +
                        tmp + " sec.");

                Log.d(LOG_TAG,"time - " + tmp);
                if ((barProgress) * (100 / quantityOfFiles) == 98) {
                    progressBar.setVisibility(View.GONE);
                    tVDownloadingText.setVisibility(View.GONE);
                }
            }
        };

    }

    private void addGridView() {
        Log.d(LOG_TAG, "IN addGridView");
        OneBitmap[] bitmaps = new OneBitmap[pictures.length];
        for (int i = 0; i < pictures.length; i++) {
            bitmaps[i] = new OneBitmap(pictures[i]);
        }

        gridView.setAdapter(new BitmapAdapter(this, bitmaps));
    }

    /**
     * multithreading. Downloading pictures.
     */
    private void doExecutorService() {
        ExecutorService service = Executors.newFixedThreadPool(5);
        Runnable[] queue = new Runnable[(quantityOfFiles / 10)];
        for (int u = 0; u < (quantityOfFiles / 10); u++) {
            queue[u] = new Runnable() {
                @Override
                public void run() {
                    int i;
                    t++;
                    if (t == 0) {
                        i = 0;
                    } else {
                        i = 1;
                    }
                    int x = (i * t * 10) + 10;
                    for (i = x - 10; i < x; i++) {
                        Log.d(LOG_TAG, "Thread: t =" + t + " i =" + i);
                        pictures[i] = getBitmapFromURL(urlArray[i]);
                        h.sendEmptyMessage(i);

                    }
                }
            };
        }
        Log.d(LOG_TAG, "LENGHT - " + queue.length);
        for (int y = 0; y < (quantityOfFiles / 10); y++) {
            service.submit(queue[y]);
        }
    }

    /**
     * Create array of URL. Read from file, put in array.
     */
    private String[] createArrayOfURL() {
        String[] res = new String[quantityOfFiles+50];
        BufferedReader reader;
        try {
            String b = "links2";
            final InputStream file = getAssets().open(b);
            Log.d(LOG_TAG, "Reading...");
            reader = new BufferedReader(new InputStreamReader(file));
            String line = reader.readLine();
            int i = 0;
            res[i] = line;
            Log.d(LOG_TAG, "Line is -" + i + " " + line);
            while (line != null) {
                i++;
                line = reader.readLine();
                res[i] = line;
                Log.d(LOG_TAG, "Line is -" + i + " " + line);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            Log.d(LOG_TAG, "Crash to read...");
        }
        return res;
    }

    /**
     * Get bitmap from URL. Download and resize him.
     */
    public Bitmap getBitmapFromURL(String src) {
        try {
            long startTime = System.currentTimeMillis();
            java.net.URL url = new java.net.URL(src);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            //resize bitmap
            long elapsedTime = System.currentTimeMillis() - startTime;

            averageTime = elapsedTime;
            if (remainingTime == 0) remainingTime = (averageTime*quantityOfFiles)/5;
//            remainingTime = calculateRemainingTime(elapsedTime);

            Bitmap resized = Bitmap.createScaledBitmap(myBitmap, 160, 160, true);
            return resized;
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(LOG_TAG, "Picture not loaded");
            return null;
        }
    }

    private long calculateRemainingTime(long elapsedTime) {
        Log.d(LOG_TAG, "In calculate");

        if (remainingTime == 0) filesLeftToDownload = quantityOfFiles;
        if (averageTime == 0) totalTime = elapsedTime;
        averageTime = (totalTime + elapsedTime)/filesDownloaded;
        long res = 0;
        res = (averageTime*filesLeftToDownload)/5;
        return res;
    }
}

