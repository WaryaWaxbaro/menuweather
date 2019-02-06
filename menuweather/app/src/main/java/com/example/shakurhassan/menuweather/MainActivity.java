package com.example.shakurhassan.menuweather;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements DataAvailableInterface, View.OnClickListener {

    MenuEngine menuEngine = new MenuEngine(this);

    WeatherEngine weatherEngine = new WeatherEngine(this);

    int day;
    int month;
    int year;

    int dataChanger = 0;

    String DEFAULT_CITY= "Oulu";

    String city;

    TextView city_tv, focus_tv;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Ui items
        city_tv = findViewById(R.id.city_tv);
        focus_tv = findViewById(R.id.focus_tv);
        imageView = findViewById(R.id.icon_image_view);

        //Gets search value from CitySearch Activity
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            city = bundle.getString("city");
            city_tv.setText(city);
            weatherEngine.getWeatherData(city_tv.getText().toString());
            focus_tv.setText(weatherEngine.getTemperature());
            Picasso.with(this).load("http://openweathermap.org/img/w/"
                    + weatherEngine.getIconId() + ".png").into(imageView);
        }else{
            setWeatherElements();
        }

        //On click listeners
        findViewById(R.id.next_tv).setOnClickListener(this);
        findViewById(R.id.previous_tv).setOnClickListener(this);
        findViewById(R.id.settings_bt).setOnClickListener(this);

        //Menu dates
        setMenuDates();
        //Getting Menu data
        menuEngine.getMenuData();


    }

    @Override
    public void onClick(View v) {

        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        if(v.getId() == R.id.previous_tv){
            //Decrement date variable
            dataChanger--;
            calendar.add(Calendar.DATE, dataChanger);
            //Setting day, month and year
            day = calendar.get(Calendar.DATE);
            month = calendar.get(Calendar.MONTH) + 1;
            year = calendar.get(Calendar.YEAR);

            //Setting MenuEngine dates
            menuEngine.setDay(day);
            menuEngine.setMonth(month);
            menuEngine.setYear(year);

            //Gets Menu Data for specific date
            menuEngine.getMenuData();
        }
        else if(v.getId() == R.id.next_tv){
            //Increment date variable
            dataChanger++;
            calendar.add(Calendar.DATE, dataChanger);
            day = calendar.get(Calendar.DATE);
            month = calendar.get(Calendar.MONTH) + 1;
            year = calendar.get(Calendar.YEAR);

            //Setting MenuEngine dates when date is added by a day
            menuEngine.setDay(day);
            menuEngine.setMonth(month);
            menuEngine.setYear(year);

            //Gets Menu Data for specific date after adding a day to the date variable
            menuEngine.getMenuData();
        }

        else if(v.getId() == R.id.settings_bt){
            //Starts intent for the CitySearch Activity
            Intent intent = new Intent(this, CitySearch.class);
            startActivity(intent);
        }
    }

    //Set Weather Ui elements
    private void setWeatherElements(){
        String degree = "\u00b0" + "C";
        city = DEFAULT_CITY;
        city_tv.setText(city);
        weatherEngine.getWeatherData(city_tv.getText().toString());
        focus_tv.setText(weatherEngine.getTemperature() + degree);
        Picasso.with(this).load("http://openweathermap.org/img/w/"
                + weatherEngine.getIconId() + ".png").into(imageView);
    }

    //Update Weather Ui Elements
    private void updateWeatherUi(){
        String degree = "\u00b0" + "C";
        city_tv.setText(city);
        weatherEngine.getWeatherData(city_tv.getText().toString());
        focus_tv.setText(weatherEngine.getTemperature() + degree);
        Picasso.with(this).load("http://openweathermap.org/img/w/"
                + weatherEngine.getIconId() + ".png").into(imageView);
    }

    //Sets default menu date
    private void setMenuDates(){

        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        day = calendar.get(Calendar.DATE);
        month = calendar.get(Calendar.MONTH) + 1;
        year = calendar.get(Calendar.YEAR);

        menuEngine.setDay(day);
        menuEngine.setMonth(month);
        menuEngine.setYear(year);
    }

    //Updates menu Ui
    protected void updateMenuUi(){

        ListView listView = findViewById(R.id.menu_list);

        //Shows day's date
        TextView dateTextView = findViewById(R.id.date_textView);
        dateTextView.setText(menuEngine.getDay() + "." +
                            menuEngine.getMonth() + "." +
                            menuEngine.getYear());

        ArrayList<String> menuEngineArrayList = new ArrayList<>();

        if(menuEngine.getMealName() != null){
            //Adding meal names to the Array
            for(int i = 0; i < menuEngine.getMealName().size(); i++){
                String menuNames = menuEngine.getMealName().get(i);
                menuEngineArrayList.add(menuNames);
            }
        }

        //Initializing Array adapter, setting menu layout and parsing menu names
        ArrayAdapter<String> mealsArrayAdapter = new ArrayAdapter<String>
                (this, R.layout.listview_layout, R.id.meal_name, menuEngineArrayList);

        //Checks whether their is a menu for the day, if no menu the list is hidden
        if(menuEngine.getLunchMenuEmpty() || menuEngine.getSetMenuEmpty()){
            listView.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "No menu " + menuEngine.getDay() + "."
                    + menuEngine.getMonth() + "." + menuEngine.getYear(), Toast.LENGTH_LONG).show();
        }
        else {
            //The list is shown if their is a menu for the day
            listView.setVisibility(View.VISIBLE);
            listView.setAdapter(mealsArrayAdapter);
        }

    }

    //Shows menu and weather data if is available
    @Override
    public void DataAvailable() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateMenuUi();
                updateWeatherUi();
            }
        });
    }
}
