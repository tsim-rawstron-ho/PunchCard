package com.codepath.punchcard.models;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import com.codepath.punchcard.R;
import java.util.Date;
import java.util.Locale;
import org.json.JSONObject;

/**
 * Created by harris on 3/7/15.
 */
public class Weather {
  private String humidity;
  private String pressure;
  private String description;
  private String temp;
  private String icon;

  public String getDescription() {
    return description;
  }

  public String getTemp() {
    return temp;
  }

  public String getIcon() {
    return icon;
  }

  public static Weather fromJSON(JSONObject json, Context context){
    Weather weather = new Weather();
    try {
      JSONObject details = json.getJSONArray("weather").getJSONObject(0);
      JSONObject main = json.getJSONObject("main");
      weather.humidity = main.getString("humidity");
      weather.pressure = main.getString("pressure");
      weather.description = details.getString("description").toUpperCase(Locale.US);
      weather.temp = String.format("%.2f", main.getDouble("temp")) + " ℃";
      weather.icon = findWeatherIcon(context, details.getInt("id"), json.getJSONObject("sys").getLong("sunrise") * 1000, json.getJSONObject("sys").getLong("sunset") * 1000);
    }catch(Exception e){
      Log.e("SimpleWeather", "One or more fields not found in the JSON data");
    }
    return weather;
  }

  private static String findWeatherIcon(Context context, int actualId, long sunrise, long sunset){
    int id = actualId / 100;
    String icon = "";
    if(actualId == 800){
      long currentTime = new Date().getTime();
      if(currentTime>=sunrise && currentTime<sunset) {
        icon = context.getString(R.string.weather_sunny);
      } else {
        icon = context.getString(R.string.weather_clear_night);
      }
    } else {
      switch(id) {
        case 2 : icon = context.getString(R.string.weather_thunder);
          break;
        case 3 : icon = context.getString(R.string.weather_drizzle);
          break;
        case 7 : icon = context.getString(R.string.weather_foggy);
          break;
        case 8 : icon = context.getString(R.string.weather_cloudy);
          break;
        case 6 : icon = context.getString(R.string.weather_snowy);
          break;
        case 5 : icon = context.getString(R.string.weather_rainy);
          break;
      }
    }
    return icon;
  }
}
