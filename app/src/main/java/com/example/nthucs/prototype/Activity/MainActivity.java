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

import com.example.nthucs.prototype.FoodList.Food;
import com.example.nthucs.prototype.FoodList.FoodAdapter;
import com.example.nthucs.prototype.FoodList.FoodDAO;
import com.example.nthucs.prototype.R;
import com.example.nthucs.prototype.TabsBar.ViewPagerAdapter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;

public class MainActivity extends AppCompatActivity {

    private ListView food_list;

    private FoodAdapter foodAdapter;

    // list of foods
    private List<Food> foods;

    //private MenuItem calendar_food, take_photo, scan_food;
    private MenuItem add_food, search_food, revert_food, delete_food;

    // element for the bottom of the tab content
    private ViewPager viewPager;
    private TabLayout tabLayout;

    // action number for every activity
    private static final int ADD_FOOD = 0;
    private static final int EDIT_FOOD = 1;
    private static final int SCAN_FOOD = 2;
    private static final int TAKE_PHOTO = 3;
    private static final int CALENDAR = 4;
    private static final int SETTINGS = 5;

    private int selectedCount = 0;

    // data base for storing food list
    private FoodDAO foodDAO;

    // csv reader
    private CSVReader foodCalReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = (ViewPager)findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        processTabLayout();

        food_list = (ListView)findViewById(R.id.food_list);
        processControllers();

        foodDAO = new FoodDAO(getApplicationContext());

        foods = foodDAO.getAll();

        foodAdapter = new FoodAdapter(this, R.layout.single_food, foods);
        food_list.setAdapter(foodAdapter);

        try {
            openFoodCalCsv();
        } catch (IOException e) {
            System.out.println("open food cal: IO exception");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {

            System.out.println("Main "+(data.getExtras() == null));

            // Return form calendar
            if (requestCode == CALENDAR && data.getExtras() == null) {
                selectTab(0);
                // Because calendar have no food return yet
                return;
            }

            // Return from settings
            if (requestCode == SETTINGS && data.getExtras() == null) {
                selectTab(0);
                return;
            }

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

        /*calendar_food = menu.findItem(R.id.calendar_food);
        take_photo = menu.findItem(R.id.take_photo);
        scan_food = menu.findItem(R.id.scan_food);*/
        add_food = menu.findItem(R.id.add_food);
        search_food = menu.findItem(R.id.search_food);
        revert_food = menu.findItem(R.id.revert_food);
        delete_food = menu.findItem(R.id.delete_food);

        processMenu(null);

        return true;
    }

    // Open food calories
    private void openFoodCalCsv() throws IOException {
        System.out.println("@@@@@@@");
        foodCalReader = new CSVReader(new InputStreamReader(getAssets().open("food_cal.csv")));
        System.out.println("@@@@@@@");

        String [] nextLine;

        /*while ((nextLine = foodCalReader.readNext()) != null) {
            if (nextLine != null) {
                System.out.println(Arrays.toString(nextLine));
            }
        }*/

    }

    // Initialize tab layout and listener
    private void processTabLayout() {
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

        // enable tab selected listener
        tabLayout.setOnTabSelectedListener(
                new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        super.onTabSelected(tab);
                        if (tab.getPosition() == 0) {
                            // main activity itself
                        } else if (tab.getPosition() == 1) {
                            Intent intent_calendar = new Intent("com.example.nthucs.prototype.CALENDAR");
                            startActivityForResult(intent_calendar, CALENDAR);
                        } else if (tab.getPosition() == 2) {
                            selectImage();
                        } else if (tab.getPosition() == 3) {

                        } else if (tab.getPosition() == 4) {
                            Intent intent_settings = new Intent("com.example.nthucs.prototype.SETTINGS");
                            startActivityForResult(intent_settings, SETTINGS);
                        }
                        //System.out.println(tab.getPosition());
                    }
                }
        );
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

        /*calendar_food.setVisible(selectedCount==0);
        take_photo.setVisible(selectedCount==0);
        scan_food.setVisible(selectedCount==0);*/
        add_food.setVisible(selectedCount == 0);
        search_food.setVisible(selectedCount==0);
        revert_food.setVisible(selectedCount > 0);
        delete_food.setVisible(selectedCount > 0);
    }

    public void clickMenuItem(MenuItem item) {
        int foodId = item.getItemId();

        switch (foodId) {
            /*case R.id.calendar_food:
                Intent intent_calender = new Intent("com.example.nthucs.prototype.CALENDAR");
                intent_calender.setClass(MainActivity.this , CalendarActivity.class );
                startActivity(intent_calender);
                break;
            case R.id.take_photo:
                Intent intent = new Intent("com.example.nthucs.prototype.TAKE_PHOTO");
                startActivityForResult(intent, TAKE_PHOTO);
                break;
            case R.id.scan_food:
                Intent intent2 = new Intent("com.example.nthucs.prototype.TAKE_PICT");
                startActivityForResult(intent2, SCAN_FOOD);
                break;*/
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

    // select image with two way
    private void selectImage() {
        final CharSequence[] items = { "Take with Camera", "Choose from Gallery", "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Select Image");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int index) {
                if (items[index].equals("Take with Camera")) {
                    Intent intent_camera = new Intent("com.example.nthucs.prototype.TAKE_PICT");
                    startActivityForResult(intent_camera, SCAN_FOOD);
                } else if (items[index].equals("Choose from Gallery")) {
                    Intent intent_gallery = new Intent("com.example.nthucs.prototype.TAKE_PHOTO");
                    startActivityForResult(intent_gallery, TAKE_PHOTO);
                } else if (items[index].equals("Cancel")) {
                    dialog.dismiss();
                    selectTab(0);
                }
            }
        });
        builder.show();
    }
}
