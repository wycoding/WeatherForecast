package pers.wang.weatherforecast;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import pers.wang.weatherforecast.entity.Weather;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private TextView tv_city;
    private Button btn_refresh;
    private ImageView iv_icon;
    private TextView tv_tmp_max;
    private TextView tv_tmp_min;
    private Weather.HeWeather5Bean heWeather5Bean;
    private Handler handle = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    System.out.print("00000000000000000");
                    Bitmap bmp = (Bitmap) msg.obj;
                    iv_icon.setImageBitmap(bmp);
                    break;
            }
        }

        ;
    };
    private TextView tv_update_loc;
    private TextView tv_txt_d;
    private TextView tv_txt_n;
    private TextView tv_txt_center;
    private TextView tv_tmp_center;
    private ImageButton btn_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        tv_city = (TextView) findViewById(R.id.tv_city);

        iv_icon = (ImageView) findViewById(R.id.iv_icon);
        iv_icon.setOnClickListener(this);
        tv_tmp_max = (TextView) findViewById(R.id.tv_tmp_max);
        tv_tmp_max.setOnClickListener(this);
        tv_tmp_min = (TextView) findViewById(R.id.tv_tmp_min);
        tv_tmp_min.setOnClickListener(this);
        tv_update_loc = (TextView) findViewById(R.id.tv_update_loc);
        tv_update_loc.setOnClickListener(this);
        tv_txt_d = (TextView) findViewById(R.id.tv_txt_d);
        tv_txt_d.setOnClickListener(this);
        tv_txt_n = (TextView) findViewById(R.id.tv_txt_n);
        tv_txt_n.setOnClickListener(this);
        tv_txt_center = (TextView) findViewById(R.id.tv_txt_center);
        tv_txt_center.setOnClickListener(this);
        tv_tmp_center = (TextView) findViewById(R.id.tv_tmp_center);
        tv_tmp_center.setOnClickListener(this);

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest("https://free-api.heweather.com/v5/weather?city=beijing&key=9c22c43aaa644d8587970904dff7986f", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse: " + response);
                Gson gson = new Gson();
                Weather weather = gson.fromJson(response, Weather.class);
                heWeather5Bean = weather.getHeWeather5().get(0);
                buildView();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: ", error);
            }
        });
        queue.add(request);
        btn_time = (ImageButton) findViewById(R.id.btn_time);
        btn_time.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_time:
                Intent i = new Intent();
                i.setClass(MainActivity.this,TimeActivity.class);
                startActivity(i);
                break;
        }
    }

    private void buildView() {
        tv_city.setText(heWeather5Bean.getBasic().getCity());
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.print("111111111111111111");
                Bitmap bmp = null;
                String url = "https://cdn.heweather.com/cond_icon/" + heWeather5Bean.getNow().getCond().getCode() + ".png";
                bmp = getHttpBitmap(url);
                Message msg = new Message();
                msg.what = 0;
                msg.obj = bmp;
                handle.sendMessage(msg);
            }
        }).start();
        tv_txt_d.setText(heWeather5Bean.getDaily_forecast().get(0).getCond().getTxt_d());
        tv_txt_n.setText(heWeather5Bean.getDaily_forecast().get(0).getCond().getTxt_n());
        tv_update_loc.setText(heWeather5Bean.getBasic().getUpdate().getLoc());
        tv_tmp_min.setText(heWeather5Bean.getDaily_forecast().get(0).getTmp().getMin() + "℃");
        tv_tmp_max.setText(heWeather5Bean.getDaily_forecast().get(0).getTmp().getMax() + "℃");
        tv_txt_center.setText("转");
        tv_tmp_center.setText("~");
    }

    /**
     * 获取网落图片资源
     *
     * @param url
     * @return
     */
    public static Bitmap getHttpBitmap(String url) {
        URL myFileURL;
        Bitmap bitmap = null;
        try {
            myFileURL = new URL(url);
            //获得连接
            HttpURLConnection conn = (HttpURLConnection) myFileURL.openConnection();
            //设置超时时间为6000毫秒，conn.setConnectionTiem(0);表示没有时间限制
            conn.setConnectTimeout(6000);
            //连接设置获得数据流
            conn.setDoInput(true);
            //不使用缓存
            conn.setUseCaches(false);
            //这句可有可无，没有影响
            //conn.connect();
            //得到数据流
            InputStream is = conn.getInputStream();
            //解析得到图片
            bitmap = BitmapFactory.decodeStream(is);
            //关闭数据流
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;

    }
}

