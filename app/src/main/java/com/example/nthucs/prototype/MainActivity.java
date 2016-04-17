package com.example.nthucs.prototype;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    private ListView food_list;

    private FoodAdapter foodApapter;

    private List<Food> foods;

    private MenuItem add_item, search_item, revert_item, delete_item;

    private int selectedCount = 0;

    private FoodDAO foodDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        food_list = (ListView)findViewById(R.id.food_list);

        foodDAO = new FoodDAO(getApplicationContext());

        foods = foodDAO.getAll();

        foodApapter = new FoodAdapter(this, R.layout.single_food, foods);
        food_list.setAdapter(foodApapter);
    }
}
