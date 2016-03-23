package weather.young.com.xiaomiweather.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yang.jianan on 2016/2/24.
 */
public class ParseJson {
    String json;
    public ParseJson(String josn){
        this.json = json;
    }

    public  String getCity(String json){
        JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
        if(jsonObject.get("forecast")!=null){
            JsonObject forecast = jsonObject.get("forecast").getAsJsonObject();
            String cityName = forecast.get("city").getAsString();
            return cityName;
        }
        return null;
    }

    public  Map<String,String> tianQi(String json){
        Map<String,String> realtime = new HashMap<String,String>();
        JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
        JsonObject forecast = jsonObject.get("realtime").getAsJsonObject();
        String tianqi = forecast.get("weather").getAsString();
        String temp = forecast.get("temp").getAsString();
        realtime.put("weather",tianqi);
        realtime.put("temp",temp);
        return realtime;
    }

}
