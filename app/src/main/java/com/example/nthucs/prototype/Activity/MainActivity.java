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

import com.commonsware.cwac.merge.MergeAdapter;
import com.example.nthucs.prototype.FoodList.CalorieDAO;
import com.example.nthucs.prototype.FoodList.Food;
import com.example.nthucs.prototype.FoodList.FoodAdapter;
import com.example.nthucs.prototype.FoodList.FoodCal;
import com.example.nthucs.prototype.FoodList.FoodDAO;
import com.example.nthucs.prototype.R;
import com.example.nthucs.prototype.SportList.Sport;
import com.example.nthucs.prototype.SportList.SportAdapter;
import com.example.nthucs.prototype.SportList.SportDAO;
import com.example.nthucs.prototype.TabsBar.TabsController;
import com.example.nthucs.prototype.TabsBar.ViewPagerAdapter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;

public class MainActivity extends AppCompatActivity {

    // data base for storing food list
    private FoodDAO foodDAO;

    // data base for storing sport list
    private SportDAO sportDAO;

    // combine food and sport to event list view
    private ListView event_list;
    //private ListView food_list, sport_list;

    // use merge adapter
    private MergeAdapter eventAdapter;

    private FoodAdapter foodAdapter;

    private SportAdapter sportAdapter;

    // list of foods
    private List<Food> foods;

    // list of sports
    private List<Sport> sports;

    private MenuItem add_event, search_event, revert_event, delete_event;

    // element for the bottom of the tab content
    private ViewPager viewPager;
    private TabLayout tabLayout;

    // action number for every activity
    private static final int ADD_FOOD = 0;
    private static final int EDIT_FOOD = 1;
    private static final int SCAN_FOOD = 2;
    private static final int TAKE_PHOTO = 3;
    private static final int ADD_SPORT = 4;
    private static final int EDIT_SPORT = 5;
    private int selectedCount = 0;

    // activity string
    private static final String FROM_CAMERA = "scan_food";
    private static final String FROM_GALLERY = "take_photo";

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

        // food list data base
        foodDAO = new FoodDAO(getApplicationContext());
        foods = foodDAO.getAll();

        // sport list data base
        sportDAO = new SportDAO(getApplicationContext());
        sports = sportDAO.getAll();

        // initialize food & sport adapter
        foodAdapter = new FoodAdapter(this, R.layout.single_food, foods);
        sportAdapter = new SportAdapter(this, R.layout.single_sport, sports);

        // merge to event adapter
        eventAdapter = new MergeAdapter();
        eventAdapter.addAdapter(foodAdapter);
        eventAdapter.addAdapter(sportAdapter);

        // initialize event list, process controllers, and set merge adapter
        event_list = (ListView)findViewById(R.id.event_list);
        processEventListControllers();
        event_list.setAdapter(eventAdapter);

        //food_list = (ListView)findViewById(R.id.food_list);
        //processFoodListControllers();

        //sport_list = (ListView)findViewById(R.id.sport_list);
        //processSportListControllers();

        //food_list.addFooterView(new View(this));
        //food_list.setAdapter(foodAdapter);
        //sport_list.setAdapter(sportAdapter);

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

            // Sport add event first
            if (requestCode == ADD_SPORT) {
                // get sport data
                Sport sport = (Sport)data.getExtras().getSerializable("com.example.nthucs.prototype.SportList.Sport");

                sport = sportDAO.insert(sport);

                sports.add(sport);
                sportAdapter.notifyDataSetChanged();

                // Always select food list tab after return
                selectTab(0);
                return;
            // Sport edit event
            } else if (requestCode == EDIT_SPORT) {
                // get sport data
                Sport sport = (Sport)data.getExtras().getSerializable("com.example.nthucs.prototype.SportList.Sport");

                int position = data.getIntExtra("position", -1);

                if (position != -1) {
                    // update data base
                    sportDAO.update(sport);

                    sports.set(position, sport);
                    sportAdapter.notifyDataSetChanged();
                }
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

        add_event = menu.findItem(R.id.add_event);
        search_event = menu.findItem(R.id.search_event);
        revert_event = menu.findItem(R.id.revert_event);
        delete_event = menu.findItem(R.id.delete_event);

        processMenu(null);
        processMenuFromSport(null);

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Always select tab 0
        selectTab(0);
    }

    // Open food calories
    private void openFoodCalCsv() throws IOException {
        // Build reader instance
        foodCalReader = new CSVReader(new InputStreamReader(getAssets().open("food_cal.csv")));

        // Read all rows at once
        ArrayList<String[]> allRows= (ArrayList)foodCalReader.readAll();

        // Read CSV line by line
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

    // Event list controller: single click and double click
    private  void processEventListControllers() {
        // construct event list item click listener
        // 建立選單食物點擊監聽物件
        AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // click position in food adapter
                if (position < foodAdapter.getCount()) {
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
                // click position in sport adapter
                } else {
                    // minus position to original one
                    int sport_position = position - foodAdapter.getCount();
                    Sport sport = sportAdapter.getItem(sport_position);

                    if (selectedCount > 0) {
                        processMenuFromSport(sport);
                        sportAdapter.set(sport_position, sport);
                    } else {
                        Intent intent = new Intent(
                                "com.example.nthucs.prototype.EDIT_SPORT");

                        intent.putExtra("position", sport_position);
                        intent.putExtra("com.example.nthucs.prototype.SportList.Sport", sport);
                        startActivityForResult(intent, EDIT_SPORT);
                    }
                }
            }
        };

        // register event list item click listener
        event_list.setOnItemClickListener(itemListener);

        // construct event list item long click listener
        AdapterView.OnItemLongClickListener itemLongListener = new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                // long click position in food adapter
                if (position < foodAdapter.getCount()) {
                    Food food = foodAdapter.getItem(position);

                    processMenu(food);
                    foodAdapter.set(position, food);
                // long click position in sport adapter
                } else {
                    // minus position to original one
                    int sport_position = position - foodAdapter.getCount();
                    Sport sport = sportAdapter.getItem(sport_position);

                    processMenuFromSport(sport);
                    sportAdapter.set(sport_position, sport);
                }
                return true;
            }
        };

        // register event list item long click listener
        event_list.setOnItemLongClickListener(itemLongListener);
    }

    // Food list controller: single click and double click
    private void processFoodListControllers() {
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
        //food_list.setOnItemClickListener(itemListener);

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
        //food_list.setOnItemLongClickListener(itemLongListener);
    }

    // Sport list controller: single click and double click
    private void processSportListControllers() {
        // construct sport list item click listener
        AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Sport sport = sportAdapter.getItem(position);

                if (selectedCount > 0) {
                    processMenuFromSport(sport);
                    sportAdapter.set(position, sport);
                } else {
                    Intent intent = new Intent(
                            "com.example.nthucs.prototype.EDIT_SPORT");

                    intent.putExtra("position", position);
                    intent.putExtra("com.example.nthucs.prototype.SportList.Sport", sport);
                    startActivityForResult(intent, EDIT_SPORT);
                }
            }
        };

        // register sport list item click listener
        //sport_list.setOnItemClickListener(itemListener);

        // construct sport list item long click listener
        AdapterView.OnItemLongClickListener itemLongListener = new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                Sport sport = sportAdapter.getItem(position);

                processMenuFromSport(sport);
                sportAdapter.set(position, sport);
                return true;
            }
        };

        // register sport list item long click listener
        //sport_list.setOnItemLongClickListener(itemLongListener);
    }

    // Process main menu depends on selected food
    private void processMenu(Food food) {
        if (food != null) {
            food.setSelected(!food.isSelected());

            if (food.isSelected())
                selectedCount++;
            else
                selectedCount--;
        }

        add_event.setVisible(selectedCount == 0);
        search_event.setVisible(selectedCount==0);
        revert_event.setVisible(selectedCount > 0);
        delete_event.setVisible(selectedCount > 0);
    }

    // Process main menu depends on selected sport
    private void processMenuFromSport(Sport sport) {
        if (sport != null) {
            sport.setSelected(!sport.isSelected());

            if (sport.isSelected())
                selectedCount++;
            else
                selectedCount--;
        }

        add_event.setVisible(selectedCount == 0);
        search_event.setVisible(selectedCount==0);
        revert_event.setVisible(selectedCount > 0);
        delete_event.setVisible(selectedCount > 0);
    }

    public void clickMenuItem(MenuItem item) {
        int foodId = item.getItemId();

        switch (foodId) {
            case R.id.add_event:
                // string in dialog
                final CharSequence[] items = { "New Food", "New Sport", "Cancel" };

                // use alert dialog to select add new food or sport event
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Add Event");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int index) {
                        if (items[index].equals("New Food")) {
                            Intent intent_food = new Intent("com.example.nthucs.prototype.ADD_FOOD");
                            startActivityForResult(intent_food, ADD_FOOD);
                        } else if (items[index].equals("New Sport")) {
                            Intent intent_sport = new Intent("com.example.nthucs.prototype.ADD_SPORT");
                            startActivityForResult(intent_sport, ADD_SPORT);
                        } else if (items[index].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
                break;
            case R.id.search_event:

                break;
            case R.id.revert_event:
                // revert food event
                for (int i = 0 ; i < foodAdapter.getCount() ; i++) {
                    Food food = foodAdapter.getItem(i);

                    if (food.isSelected()) {
                        food.setSelected(false);
                        foodAdapter.set(i, food);
                    }
                }

                // revert sport event
                for (int i = 0 ; i < sportAdapter.getCount() ; i++) {
                    Sport sport = sportAdapter.getItem(i);

                    if (sport.isSelected()) {
                        sport.setSelected(false);
                        sportAdapter.set(i, sport);
                    }
                }

                selectedCount = 0;
                processMenu(null);
                processMenuFromSport(null);
                break;
            case R.id.delete_event:
                if (selectedCount == 0) break;

                AlertDialog.Builder d = new AlertDialog.Builder(this);
                String message = getString(R.string.delete_event);
                d.setTitle(R.string.delete).setMessage(String.format(message, selectedCount));
                d.setPositiveButton(android.R.string.yes,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int index = foodAdapter.getCount() - 1;

                                // remove selected food event
                                while (index > -1) {
                                    Food food = foodAdapter.get(index);

                                    if (food.isSelected()) {
                                        foodAdapter.remove(food);
                                        foodDAO.delete(food.getId());
                                    }
                                    index--;
                                }
                                foodAdapter.notifyDataSetChanged();

                                int index_sport = sportAdapter.getCount() - 1;

                                // remove selected sport event
                                while (index_sport > -1) {
                                    Sport sport = sportAdapter.get(index_sport);

                                    if (sport.isSelected()) {
                                        sportAdapter.remove(sport);
                                        sportDAO.delete(sport.getId());
                                    }
                                    index_sport--;
                                }
                                sportAdapter.notifyDataSetChanged();
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
