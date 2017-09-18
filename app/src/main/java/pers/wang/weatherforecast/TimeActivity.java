package pers.wang.weatherforecast;

import android.graphics.Bitmap;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.Date;

@RequiresApi(api = Build.VERSION_CODES.N)
public class TimeActivity extends AppCompatActivity {
    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
    private Handler handle = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    tv_time.setText(formatter.format(new Date(System.currentTimeMillis())));
                    break;
            }
        }

        ;
    };
    private TextView tv_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time);
        initView();
    }

    private void initView() {
        tv_time = (TextView) findViewById(R.id.tv_time);
        new Thread(new Runnable() {
            @Override
            public void run() {
                handle.postDelayed(this,1000);
                Message msg = new Message();
                msg.what = 0;
                handle.sendMessage(msg);
            }
        }).start();
    }
}
