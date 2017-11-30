package dougherty.ch10_ex5;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.view.View.OnClickListener;

import dougherty.ch10_ex5.R;

public class MainActivity extends Activity implements OnClickListener {

    // Define variables for the widgets
    private TextView messageTextView;
    private Button startButton;
    private Button stopButton;

    // Define timer object
    private Timer timer;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get references to the widgets
        messageTextView = (TextView) findViewById(R.id.messageTextView);
        startButton = (Button) findViewById(R.id.startButton);
        stopButton = (Button) findViewById(R.id.stopButton);

        // Set the listeners
        startButton.setOnClickListener(this);
        stopButton.setOnClickListener(this);

    }

    private void startTimer() {
        final long startMillis = System.currentTimeMillis();
        timer = new Timer(true);
        int interval = 1000 * 10; // 10 second interval
        TimerTask task = new TimerTask() {
            
            @Override
            public void run() {
                long elapsedMillis = System.currentTimeMillis() - startMillis;
                updateView(elapsedMillis);

                downloadFile();
            }
        };
        timer.schedule(task, 0, interval);
    }

    private void updateView(final long elapsedMillis) {
        // UI changes need to be run on the UI thread
        messageTextView.post(new Runnable() {

            int elapsedSeconds = (int) elapsedMillis/1000;
            int times = elapsedSeconds/10;

            @Override
            public void run() {
                messageTextView.setText("File downloaded: " + times + " time(s)");
            }
        });
    }

    private void stopTimer() {
        if(timer != null) {
            timer.cancel();
        }
    }

    @Override
    protected void onPause() {

        stopTimer();
        super.onPause();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.startButton:
                startTimer();
                break;
            case R.id.stopButton:
                stopTimer();
                break;
        }

    }

    public void downloadFile() {
        try{
            // get the URL
            URL url = new URL("http://rss.cnn.com/rss/cnn_tech.rss");

            // get the input stream
            InputStream in = url.openStream();

            // get the output stream
            FileOutputStream out = openFileOutput("news_feed.xml", MODE_PRIVATE);

            // read input and write output
            byte[] buffer = new byte[1024];
            int bytesRead = in.read(buffer);
            while (bytesRead != -1)
            {
                out.write(buffer, 0, bytesRead);
                bytesRead = in.read(buffer);
            }
            out.close();
            in.close();
        }
        catch (IOException e) {
            Log.e("News reader", e.toString());
        }
    }
}