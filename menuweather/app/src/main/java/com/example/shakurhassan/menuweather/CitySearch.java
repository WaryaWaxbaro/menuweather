package com.example.shakurhassan.menuweather;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CitySearch extends AppCompatActivity {

    EditText editText_search;
    Button buttonGo;

    String searchText;
    String resultToUpper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);

        editText_search = findViewById(R.id.search_city_et);
        buttonGo = findViewById(R.id.search_proceed);

        buttonGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CitySearch.this, MainActivity.class);

                if(!editText_search.getText().equals("")){
                    searchText = editText_search.getText().toString();
                    resultToUpper = searchText.substring(0, 1).toUpperCase()
                            + searchText.substring(1);
                    intent.putExtra("city", resultToUpper);
                    startActivity(intent);
                }
                finish();
                saveSearchState();
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        String value = sharedPreferences.getString("search", "");
        editText_search.setText(value);
    }

    public void saveSearchState(){
        SharedPreferences preferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        SharedPreferences.Editor sharedEditor = preferences.edit();  // Put the values from the UI
        String city = resultToUpper;

        sharedEditor.putString("search", city); // value to store
        // Commit to storage
        sharedEditor.commit();
        //sharedEditor.apply();
    }
}
