package com.example.shakurhassan.menuweather;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

public class WeatherEngine implements OnRequestDoneInterface {

    static final double KELVIN_CONVERT = 273.15;

    private String temperature;
    private String iconId;
    private DataAvailableInterface uiCallBack;

    public WeatherEngine(DataAvailableInterface uiCallBack) {
        this.uiCallBack = uiCallBack;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getIconId() {
        return iconId;
    }

    public void setIconId(String iconId) {
        this.iconId = iconId;
    }

    //Starting new Thread and fetching dat from url
    public void getWeatherData(String city){
        String url = "http://api.openweathermap.org/data/2.5/weather?q="
                + city
                + "&APPID=65dbec3aae5e5bf9000c7a956c8b76f6";
        HttpGetThread httpGetThread = new HttpGetThread();
        httpGetThread.setGetter(this);
        httpGetThread.setUrlString(url);
        httpGetThread.start();
    }

    //Establishing connection and getting JSON data
    @Override
    public void requestGetter(String url) {
        try{
            URL nUrl = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection)nUrl.openConnection();
            urlConnection.connect();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(urlConnection.getInputStream())
            );
            String inputLine;
            while((inputLine = in.readLine()) != null){
                Map<String, Object> parsed = null;

                try {
                    parsed = JsonUtils.jsonToMap(new JSONObject(inputLine));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Map<String, Object> mainElement = (Map)parsed.get("main");
                double temp = (double)mainElement.get("temp");
                double tempInC = temp - KELVIN_CONVERT;
                //this.temperature = String.format("%.1f", tempInC);
                setTemperature(String.format("%.1f", tempInC));

                /*if(getTemperature() == null){
                    this.temperature = String.format("%.1f", 0);
                }*/

                ArrayList<Map<String, Object>> array = (ArrayList<Map<String, Object>>)parsed.get("weather");
                Map<String, Object> weatherElement = array.get(0);
                iconId = (String)weatherElement.get("icon");

                uiCallBack.DataAvailable();
            }
            in.close();
        }catch (MalformedURLException e){
            e.printStackTrace();
        }catch (IOException e){}
    }
}
