package weather.young.com.xiaomiweather.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.StringUtils;
import cn.trinea.android.common.util.ToastUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import weather.young.com.xiaomiweather.R;
import weather.young.com.xiaomiweather.bean.Constant;
import weather.young.com.xiaomiweather.utils.ParseJson;
import weather.young.com.xiaomiweather.utils.SharedPreferencesUtil;

/**
 * Description:
 * Author: 0027008122 [yang.jianan@zte.com.cn]
 * Time: 2016/3/15 9:14
 * Version: 1.0
 */
public class MainActivity extends AppCompatActivity {

    /*
     *  懒人框架获取组件
     */

    @Bind(R.id.city)
    TextView city;
    @Bind(R.id.weather_state)
    TextView weatherState;
    @Bind(R.id.temp)
    TextView temp;
    @Bind(R.id.following_hour_list)
    ListView followingHourListView;
    @Bind(R.id.btn)
    Button btn;
    @Bind(R.id.cityName)
    EditText edCityName;

    //下拉刷新 组件
    @Bind(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    List<HashMap<String, Object>> followListView;

    Context context = MainActivity.this;

    String sharedPrefsName = Constant.sharedPrefsName;

    InputStream is;
    InputStreamReader isr;

    // 城市 id
    String cityId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Logger.init("MainActivity");

        //重新打开,默认读取上一次的数据
        city.setText(SharedPreferencesUtil.get(context, sharedPrefsName, "city"));
        weatherState.setText(SharedPreferencesUtil.get(context, sharedPrefsName, "weather"));
        temp.setText(SharedPreferencesUtil.get(context, sharedPrefsName, "temp"));

        //刷新动画设置
        setSwipeEvent();

    }

    private void setSwipeEvent() {
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark,
                R.color.colorPrimary,
                R.color.colorPrimaryDark,
                R.color.colorPrimary);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //当下拉刷洗, 此时需要点击GET按钮
                Button button = (Button) findViewById(R.id.btn);
                button.performClick();
            }
        });
    }

    @OnClick(R.id.btn)
    void btn() {
        findCityId();
        new Thread(runnable).start();
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {

                //设置睡眠2秒,达到 刷新动画效果持续!
                Thread.sleep(2000);

                String url = Constant.url + cityId;

                OkHttpClient okHttpClient = new OkHttpClient();
                Request request = new Request.Builder().url(url).build();
                Response response = okHttpClient.newCall(request).execute();
                String ponse = response.body().string();

                Message message = new Message();
                message.obj = ponse;
                handler.sendMessage(message);

            } catch (Exception e) {
                if (e != null) {
                    Logger.d(e.getMessage());
                }

            }
        }
    };

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (msg.obj.toString() == null) {
                ToastUtils.show(context, "没有" + "该城市" + "的天气信息!");

            } else {
                String json = msg.obj.toString();
                if (!StringUtils.isEmpty(json)) {
                    System.out.println(json);
                    if (!StringUtils.isEmpty(new ParseJson(json).getCity(json))) {
                        String cityName = new ParseJson(json).getCity(json);
                        city.setText(cityName);
                        //通过 sharedpreference 将查询到的城市天气数据 存储在本地
                        SharedPreferencesUtil.set(context, sharedPrefsName, "city", cityName);
                    }

                    Map<String, String> realtime = new HashMap<String, String>();
                    if (new ParseJson(json).tianQi(json).size() > 0) {
                        realtime = new ParseJson(json).tianQi(json);
                        if (!StringUtils.isEmpty(realtime.get("weather"))) {
                            weatherState.setText(realtime.get("weather"));
                            temp.setText(realtime.get("temp") + getString(R.string.weather_unit));

                            //通过 sharedpreference 将查询到的城市天气数据 存储在本地
                            SharedPreferencesUtil.set(context, sharedPrefsName, "temp", realtime.get("temp") + getString(R.string.weather_unit));
                            SharedPreferencesUtil.set(context, sharedPrefsName, "weather", realtime.get("weather"));
                        }

                    }

                }

            }
            //停止刷新
            swipeRefreshLayout.setRefreshing(false);
        }
    };

    /**
     * 查询 city code
     * 这种是每次去读取文件的方式
     * 还可以用数据库,把文件写入sqlite(可以用 ormlite 框架)
     * 数据库存储的见: https://github.com/yangxiaoge/queryCityId
     */
    private void findCityId() {

        try {
            // 两种读取都可以
            InputStreamReader inputReader = new InputStreamReader(getResources().getAssets().open("citycode.txt"));
//            InputStreamReader inputReader = new InputStreamReader(this.getClass()
//                    .getClassLoader()
//                    .getResourceAsStream("assets/" + "citycode.txt")
//                    , "utf-8"
//            );
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            String[] str;

            int i = 1;

            while ((line = bufReader.readLine()) != null) {
                str = line.split("=");
                String cityName = edCityName.getText().toString().trim();
                cityName = !cityName.isEmpty() ? cityName : "南京";
                if (str.length == 2 && null != str[1] && !"".equals(str[1]) && str[1].equals(cityName)) {
                    System.out.println("city name: " + str[1] + " city id: " + str[0]);

                    cityId = str[0];
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

}
