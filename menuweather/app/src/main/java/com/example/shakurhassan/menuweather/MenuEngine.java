package com.example.shakurhassan.menuweather;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MenuEngine implements OnRequestDoneInterface {

    private int day;
    private int month;
    private int year;
    private List<String> mealName;

    private boolean isLunchMenuEmpty;
    private boolean isSetMenuEmpty;

    private DataAvailableInterface uiCallBack;

    public MenuEngine(DataAvailableInterface uiCallBack) {
        this.uiCallBack = uiCallBack;
    }

    public List<String> getMealName() {
        return mealName;
    }

    public void setMealName(List<String> mealName) {
        this.mealName = mealName;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Boolean getLunchMenuEmpty() {
        return isLunchMenuEmpty;
    }

    public void setLunchMenuEmpty(Boolean lunchMenuEmpty) {
        isLunchMenuEmpty = lunchMenuEmpty;
    }

    public Boolean getSetMenuEmpty() {
        return isSetMenuEmpty;
    }

    public void setSetMenuEmpty(Boolean setMenuEmpty) {
        isSetMenuEmpty = setMenuEmpty;
    }

    //Starts new Thread and getting dat from the url
    public void getMenuData(){

        String url = "https://www.amica.fi/api/restaurant/menu/day?date=" +
                getYear() +
                "-" +
                getMonth() +
                "-" +
                getDay() +
                "&language=en&restaurantPageId=66287";

        HttpGetThread httpGetThread = new HttpGetThread();
        httpGetThread.setGetter(this);
        httpGetThread.setUrlString(url);
        httpGetThread.start();
    }

    //Getting JSON data and fetching as well as updating Ui callback if
    //Their is data available or no data is available
    @Override
    public void requestGetter(String url) {
        List<String> mealsList = new ArrayList<>();
        String [] newMenu = new String[] {};
        try{
            URL nUrl = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) nUrl.openConnection();
            urlConnection.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(urlConnection.getInputStream()));
            String inputLine;
            StringBuilder stringBuilder = new StringBuilder();
            while((inputLine = in.readLine()) != null){
                stringBuilder.append(inputLine).append("\n");
            }
            in.close();

            JSONObject obj = null;
            try {
                obj = new JSONObject(stringBuilder.toString());
                if(obj.isNull("LunchMenu")){
                    setLunchMenuEmpty(true);
                    uiCallBack.DataAvailable();
                }else {
                    JSONObject jsonObject = obj.getJSONObject("LunchMenu");
                    //String dayOfWeek = jsonObject.getString("DayOfWeek");
                    //String lunchDate = jsonObject.getString("Date");
                    //JSONArray arr = obj.getJSONArray("SetMenus");
                    JSONArray setMenus = jsonObject.getJSONArray("SetMenus");
                    if(setMenus.length() > 0){
                        for (int i = 0; i < setMenus.length(); i++)
                        {
                            StringBuilder sb = new StringBuilder();
                            JSONObject setMenuItems = setMenus.getJSONObject(i);
                            JSONArray mMeals = setMenuItems.getJSONArray("Meals");
                            for(int j = 0; j < mMeals.length(); j++){
                                JSONObject mealValues = mMeals.getJSONObject(j);
                                String mealName = mealValues.getString("Name");
                                sb.append(mealName + "\n");
                            }
                            String mealNameStrings = sb.toString();
                            sb.setLength(0);
                            if(!mealNameStrings.equals("")){
                                newMenu = new String[] {mealNameStrings};
                                mealsList.add(Arrays.asList(newMenu).toString()
                                        .replace("[","")
                                        .replace("]","")
                                        .replace("\n\n", "\n"));
                                setMealName(mealsList);
                            }
                        }
                        setSetMenuEmpty(false);
                    }else{
                        setSetMenuEmpty(true);
                    }
                    setLunchMenuEmpty(false);
                    uiCallBack.DataAvailable();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }catch (IOException e){}
    }
}
