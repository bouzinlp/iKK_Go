package com.example.nthucs.prototype.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.nthucs.prototype.FoodList.CalorieDAO;
import com.example.nthucs.prototype.FoodList.Food;
import com.example.nthucs.prototype.FoodList.FoodAdapter;
import com.example.nthucs.prototype.FoodList.FoodCal;
import com.example.nthucs.prototype.FoodList.FoodDAO;
import com.example.nthucs.prototype.R;
import com.example.nthucs.prototype.TabsBar.TabsController;
import com.example.nthucs.prototype.TabsBar.ViewPagerAdapter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;

public class MainActivity extends AppCompatActivity {

    private ListView food_list;

    private FoodAdapter foodAdapter;

    // list of foods
    private List<Food> foods;

    private MenuItem add_food, search_food, revert_food, delete_food;

    // element for the bottom of the tab content
    private ViewPager viewPager;
    private TabLayout tabLayout;

    // action number for every activity
    private static final int ADD_FOOD = 0;
    private static final int EDIT_FOOD = 1;
    private static final int SCAN_FOOD = 2;
    private static final int TAKE_PHOTO = 3;
    private int selectedCount = 0;

    // activity string
    private static final String FROM_CAMERA = "scan_food";
    private static final String FROM_GALLERY = "take_photo";

    // data base for storing food list
    private FoodDAO foodDAO;

    // csv reader
    private CSVReader foodCalReader;

    // list of foodCal
    private List<FoodCal> foodCalList = new ArrayList<>();

    // data base for storing calorie data
    private CalorieDAO calorieDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // calorie data base
        calorieDAO = new CalorieDAO(getApplicationContext());

        // if the app is re-install or open in first time, then read csv and store in data base
        if (calorieDAO.isTableEmpty() == true) {
            try {
                openFoodCalCsv();
            } catch (IOException e) {
                System.out.println("open food cal: IO exception");
            }
        // if open app more once time, just get the data base immediately
        } else {
            foodCalList = calorieDAO.getAll();

            // output test
            /*for (int i = 0 ; i < foodCalList.size() ; i++) {
                System.out.println(foodCalList.get(i).getEnglishName());
            }*/
        }

        // initialize tabLayout and viewPager
        viewPager = (ViewPager)findViewById(R.id.viewPager);
        tabLayout = (TabLayout)findViewById(R.id.tabLayout);
        initializeTabLayout();

        // call function to active tabs listener
        TabsController tabsController = new TabsController(0, MainActivity.this, tabLayout, viewPager);
        tabsController.processTabLayout();

        food_list = (ListView)findViewById(R.id.food_list);
        processControllers();

        // food list data base
        foodDAO = new FoodDAO(getApplicationContext());

        foods = foodDAO.getAll();

        foodAdapter = new FoodAdapter(this, R.layout.single_food, foods);
        food_list.setAdapter(foodAdapter);

        // other activity's back to take photo from gallery or camera
        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().getInt(FROM_CAMERA) == SCAN_FOOD) {
                Intent intent_camera = new Intent("com.example.nthucs.prototype.TAKE_PICT");
                startActivityForResult(intent_camera, SCAN_FOOD);
            } else if (getIntent().getExtras().getInt(FROM_GALLERY) == TAKE_PHOTO) {
                Intent intent_gallery = new Intent("com.example.nthucs.prototype.TAKE_PHOTO");
                startActivityForResult(intent_gallery, TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {

            // Get food data
            Food food = (Food) data.getExtras().getSerializable("com.example.nthucs.prototype.FoodList.Food");

            // Add new food list
            if (requestCode == ADD_FOOD) {
                food = foodDAO.insert(food);

                foods.add(food);
                foodAdapter.notifyDataSetChanged();
            // Edit food list
            } else if (requestCode == EDIT_FOOD) {
                int position = data.getIntExtra("position", -1);

                if (position != -1) {
                    foodDAO.update(food);

                    foods.set(position, food);
                    foodAdapter.notifyDataSetChanged();
                }
            // Scan picture as adding food
            } else if (requestCode == SCAN_FOOD) {
                food = foodDAO.insert(food);

                foods.add(food);
                foodAdapter.notifyDataSetChanged();
            // Take photo from library(gallery)
            } else if (requestCode == TAKE_PHOTO) {
                food = foodDAO.insert(food);

                foods.add(food);
                foodAdapter.notifyDataSetChanged();
            }

            // Always select food list tab after return
            selectTab(0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);

        add_food = menu.findItem(R.id.add_food);
        search_food = menu.findItem(R.id.search_food);
        revert_food = menu.findItem(R.id.revert_food);
        delete_food = menu.findItem(R.id.delete_food);

        processMenu(null);

        return true;
    }

    // Open food calories
    private void openFoodCalCsv() throws IOException {
        // Build reader instance
        foodCalReader = new CSVReader(new InputStreamReader(getAssets().open("food_cal.csv")));

        // Read all rows at once
        ArrayList<String[]> allRows= (ArrayList)foodCalReader.readAll();

        //Read CSV line by line
        for (int i = 1; i < allRows.size() ; i++) {
            // temporary declarer
            FoodCal foodCal= new FoodCal();
            foodCal.setIdx(allRows.get(i)[0]);
            foodCal.setCategory(allRows.get(i)[1]);
            foodCal.setChineseName(allRows.get(i)[2]);
            foodCal.setEnglishName(allRows.get(i)[3]);
            foodCal.setCalorie(Integer.parseInt(allRows.get(i)[4]));
            foodCal.setModifiedCalorie(Integer.parseInt(allRows.get(i)[5]));

            // fetch data with not null english name temporary
            if (allRows.get(i)[3] != null) calorieDAO.insert(foodCal);
        }
    }

    // Initialize tab layout
    private void initializeTabLayout() {
        ViewPagerAdapter pagerAdapter =
                new ViewPagerAdapter(getSupportFragmentManager(), this);

        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        // set custom icon for every tab
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(pagerAdapter.getTabView(i));
            }
        }
    }

    private void processControllers() {
        // 建立選單食物點擊監聽物件
        AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Food food = foodAdapter.getItem(position);

                if (selectedCount > 0) {
                    processMenu(food);
                    foodAdapter.set(position, food);
                } else {
                    Intent intent = new Intent(
                            "com.example.nthucs.prototype.EDIT_FOOD");

                    intent.putExtra("position", position);
                    intent.putExtra("com.example.nthucs.prototype.FoodList.Food", food);
                    startActivityForResult(intent, EDIT_FOOD);
                }
            }
        };

        // 註冊選單食物點擊監聽物件
        food_list.setOnItemClickListener(itemListener);

        // 建立選單食物長按監聽物件
        AdapterView.OnItemLongClickListener itemLongListener = new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                Food food = foodAdapter.getItem(position);

                processMenu(food);
                foodAdapter.set(position, food);
                return true;
            }
        };

        // 註冊選單食物長按監聽物件
        food_list.setOnItemLongClickListener(itemLongListener);
    }

    private void processMenu(Food food) {
        if (food != null) {
            food.setSelected(!food.isSelected());

            if (food.isSelected())
                selectedCount++;
            else
                selectedCount--;
        }

        add_food.setVisible(selectedCount == 0);
        search_food.setVisible(selectedCount==0);
        revert_food.setVisible(selectedCount > 0);
        delete_food.setVisible(selectedCount > 0);
    }

    public void clickMenuItem(MenuItem item) {
        int foodId = item.getItemId();

        switch (foodId) {
            case R.id.add_food:
                Intent intent3 = new Intent("com.example.nthucs.prototype.ADD_FOOD");
                startActivityForResult(intent3, ADD_FOOD);
                break;
            case R.id.search_food:

                break;
            case R.id.revert_food:
                for (int i = 0 ; i < foodAdapter.getCount() ; i++) {
                    Food food = foodAdapter.getItem(i);

                    if (food.isSelected()) {
                        food.setSelected(false);
                        foodAdapter.set(i, food);
                    }
                }
                selectedCount = 0;
                processMenu(null);
                break;
            case R.id.delete_food:
                if (selectedCount == 0) break;

                AlertDialog.Builder d = new AlertDialog.Builder(this);
                String message = getString(R.string.delete_food);
                d.setTitle(R.string.delete).setMessage(String.format(message, selectedCount));
                d.setPositiveButton(android.R.string.yes,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int index = foodAdapter.getCount() - 1;

                                while (index > -1) {
                                    Food food = foodAdapter.get(index);

                                    if (food.isSelected()) {
                                        foodAdapter.remove(food);
                                        foodDAO.delete(food.getId());
                                    }
                                    index--;
                                }
                                foodAdapter.notifyDataSetChanged();
                            }
                        });
                d.setNegativeButton(android.R.string.no, null);
                d.show();

                break;
        }
    }

    // select specific tab
    private void selectTab(int index) {
        TabLayout.Tab tab = tabLayout.getTabAt(index);
        tab.select();
    }
}
