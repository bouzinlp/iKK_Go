package com.example.nthucs.prototype.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.cardemulation.HostNfcFService;
import android.os.Bundle;
import android.os.Handler;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.nthucs.prototype.Utility.DBFunctions;
import com.facebook.login.widget.ProfilePictureView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    //to check if it's the first execution
    SharedPreferences prefs = null;
    private Activity activity = MainActivity.this;
    private int activityIndex = 1;
    private static final int MAIN_ACTIVITY = 1;

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

    public static boolean metricFlag = true;

    DBFunctions dbFunctions;

    Handler handler = new Handler();
    SyncThread syncThread;

    Boolean IsDataChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("飲食和運動");
        //first run settings
        System.out.print("==============HOME FIRST RUN TEST=============");
        Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRun", true);
        System.out.println("first run"+isFirstRun);
        if (isFirstRun) {
            //show start activity
            startActivity(new Intent(MainActivity.this, MyProfileActivity.class));
            Toast.makeText(MainActivity.this, "First Run", Toast.LENGTH_LONG).show();
        }
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putBoolean("isFirstRun", false).commit();

        //reset the content view
        setContentView(R.layout.activity_main_nav);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        TextView facebookUsername = (TextView) headerView.findViewById(R.id.Facebook_name);
        facebookUsername.setText("Hello, "+LoginActivity.facebookName);
        ProfilePictureView profilePictureView = (ProfilePictureView) headerView.findViewById(R.id.Facebook_profile_picture);
        profilePictureView.setProfileId(LoginActivity.facebookUserID);
        // calorie data base
        calorieDAO = new CalorieDAO(getApplicationContext());
        dbFunctions = new DBFunctions(this.getApplicationContext());

        IsDataChanged = getIntent().getBooleanExtra("DataChanged", false);
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
        //viewPager = (ViewPager)findViewById(R.id.viewPager);
        //tabLayout = (TabLayout)findViewById(R.id.tabLayout);
        //initializeTabLayout();

        // call function to active tabs listener
        //TabsController tabsController = new TabsController(0, MainActivity.this, tabLayout, viewPager);
        //tabsController.processTabLayout();

        // food list data base
        Intent intent = getIntent();
        foodDAO = new FoodDAO(getApplicationContext());
        if (intent.getIntExtra("year", 0) == 0) foods = foodDAO.getAll();
        else foods = foodDAO.getSelectedDate(intent.getIntExtra("year", 1970),
                intent.getIntExtra("month", 1),
                intent.getIntExtra("day", 1));

        // sport list data base
        sportDAO = new SportDAO(getApplicationContext());
        if (intent.getIntExtra("year", 0) == 0) sports = sportDAO.getAll();
        else sports = sportDAO.getSelectedDate(intent.getIntExtra("year", 1970),
                intent.getIntExtra("month", 1),
                intent.getIntExtra("day", 1));

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
                //selectTab(0);
                Intent intent = new Intent();
                intent.putExtra("DataChanged", true);
                intent.setClass(MainActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
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
                    IsDataChanged = true;
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
            //selectTab(0);
            Intent intent = new Intent();
            intent.putExtra("DataChanged", true);
            intent.setClass(MainActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
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
        //selectTab(0);
    }

    private class SyncThread extends Thread{
        @Override
        public void run() {
            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            ArrayList<HashMap<String, String>> userList = dbFunctions.getAllUsers();
            if(userList.size()!=0){
                if(dbFunctions.dbSyncCount() != 0){
                    params.put("usersJSON", dbFunctions.composeUserfromSQLite());
                    params.put("foodJSON", dbFunctions.composeFoodfromSQLite());
                    params.put("sportJSON",dbFunctions.composeSportfromSQLite());
                    params.put("healthJSON",dbFunctions.composeHealthfromSQLite());
                    System.out.println("AAAAAAAAAAA = "+dbFunctions.composeFoodfromSQLite());
                    client.setTimeout(10000);
                    client.post("http://140.114.88.144/mhealth/insertuser.php",params ,new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int status, cz.msebera.android.httpclient.Header[] headers, byte[] bytes) {
                            try {
                                String str = new String(bytes,"UTF-8");
                                System.out.println(str);
                                Toast.makeText(getApplicationContext(), "資料庫同步完成！", Toast.LENGTH_LONG).show();


                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int status, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {
                            if(status == 404){
                                Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                            }
                            else if(status == 500){
                                try {
                                    String str = new String(bytes,"UTF-8");
                                    System.out.println("500 "+str);
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                                Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "Status ="+status, Toast.LENGTH_LONG).show();
                                Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet]", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(getApplicationContext(), "SQLite and Remote MySQL DBs are in Sync!", Toast.LENGTH_LONG).show();
                }
            }
            else{
                Toast.makeText(getApplicationContext(), "No data in SQLite DB, please do enter User name to perform Sync action", Toast.LENGTH_LONG).show();
            }

            //handler.post(syncThread);
        }


    }

    @Override
    public void onStop(){
        super.onStop();

        if (IsDataChanged) {
            syncThread = new SyncThread();
            handler.post(syncThread);
            IsDataChanged = false;
        }

        //System.out.println("Sync executed");
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

            if(!(allRows.get(i)[7].equals("") || allRows.get(i)[7] == null)){
                foodCal.setProtein(Float.parseFloat(allRows.get(i)[7]));
            }
            else foodCal.setProtein(0);

            if(!(allRows.get(i)[8].equals("") || allRows.get(i)[8] == null)){
                foodCal.setFat(Float.parseFloat(allRows.get(i)[8]));
            }
            else foodCal.setFat(0);

            if(!(allRows.get(i)[10].equals("") || allRows.get(i)[10] == null)){
                foodCal.setCarbohydrates(Float.parseFloat(allRows.get(i)[10]));
            }
            else  foodCal.setCarbohydrates(0);


            if(!(allRows.get(i)[11].equals("") || allRows.get(i)[11] == null)){
                foodCal.setDietaryFiber(Float.parseFloat(allRows.get(i)[11]));
            }
            else foodCal.setDietaryFiber(0);

            if(!(allRows.get(i)[18].equals("") || allRows.get(i)[18] == null)){
                foodCal.setSodium(Float.parseFloat(allRows.get(i)[18]));
            }
            else foodCal.setSodium(0);

            if(!(allRows.get(i)[20].equals("") || allRows.get(i)[20] == null)){
                foodCal.setCalcium(Float.parseFloat(allRows.get(i)[20]));
            }
            else foodCal.setCalcium(0);
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
                    //System.out.println("Main date =" + food.getYYYYMD());

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
                    //System.out.println("Main date = " + sport.getYYYYMD());

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
                final CharSequence[] items = { "食物", "運動", "取消" };

                // use alert dialog to select add new food or sport event
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("新增事件");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int index) {
                        if (items[index].equals("食物")) {
                            Intent intent_food = new Intent("com.example.nthucs.prototype.ADD_FOOD");
                            startActivityForResult(intent_food, ADD_FOOD);
                        } else if (items[index].equals("運動")) {
                            Intent intent_sport = new Intent("com.example.nthucs.prototype.ADD_SPORT");
                            startActivityForResult(intent_sport, ADD_SPORT);
                        } else if (items[index].equals("取消")) {
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
                                IsDataChanged = true;
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.home) {
            Intent intent_home = new Intent();
            intent_home.setClass(MainActivity.this, HomeActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("BACK", 1);
            intent_home.putExtras(bundle);
            startActivity(intent_home);
            finish();
        } else if (id == R.id.food_list) {
            Intent intent_main = new Intent();
            intent_main.setClass(MainActivity.this, MainActivity.class);
            startActivity(intent_main);
            finish();
            //Toast.makeText(this, "Open food list", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.Import) {
            selectImage();
            //Toast.makeText(this, "Import food", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.chat) {
            Intent intent_chat_bot = new Intent();
            intent_chat_bot.setClass(MainActivity.this, ChatBotActivity.class);
            startActivity(intent_chat_bot);
            finish();
        } else if (id == R.id.new_calendar){
            Intent intent_new_calendar = new Intent();
            intent_new_calendar.setClass(MainActivity.this, NewCalendarActivity.class);
            startActivity(intent_new_calendar);
            finish();
        } else if (id == R.id.blood_pressure){
            Intent intent_blood_pressure = new Intent();
            intent_blood_pressure.setClass(MainActivity.this, MyBloodPressure.class);
            startActivity(intent_blood_pressure);
            finish();
        } else if (id == R.id.temp_record){
            Intent intent_temp_record = new Intent();
            intent_temp_record.setClass(MainActivity.this, MyTemperatureRecord.class);
            startActivity(intent_temp_record);
            finish();
        } else if (id == R.id.water_record){
            Intent intent_water_record = new Intent();
            intent_water_record.setClass(MainActivity.this, DrinkWaterDiary.class);
            startActivity(intent_water_record);
            finish();
        } else if (id == R.id.message) {
            Intent intent_message = new Intent();
            intent_message.setClass(MainActivity.this, MessageActivity.class);
            startActivity(intent_message);
            finish();
            //Toast.makeText(this, "Send message", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.mail){
            Intent intent_mail = new Intent();
            intent_mail.setClass(MainActivity.this, MailActivity.class);
            startActivity(intent_mail);
            finish();
        } else if (id == R.id.setting_list) {
            Intent intent_setting = new Intent();
            intent_setting.setClass(MainActivity.this, SettingsActivity.class);
            startActivity(intent_setting);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void selectImage(){
        final CharSequence[] items = { "照相", "從相簿中選取", "取消" };
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("新增食物");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int index) {
                if (items[index].equals("照相")) {
                    if (activityIndex == MAIN_ACTIVITY) {
                        Intent intent_camera = new Intent("com.example.nthucs.prototype.TAKE_PICT");
                        IsDataChanged = false;
                        activity.startActivityForResult(intent_camera, SCAN_FOOD);
                    } else {
                        // back to main activity
                        Intent result = new Intent();
                        result.putExtra(FROM_CAMERA, SCAN_FOOD);
                        result.putExtra("DataChanged", true);
                        result.setClass(activity, MainActivity.class);
                        activity.startActivity(result);
                        activity.finish();
                    }
                } else if (items[index].equals("從相簿中選取")) {
                    if (activityIndex == MAIN_ACTIVITY) {
                        Intent intent_gallery = new Intent("com.example.nthucs.prototype.TAKE_PHOTO");
                        //intent_gallery.putParcelableArrayListExtra(calDATA, foodCalList);
                        IsDataChanged = false;
                        activity.startActivityForResult(intent_gallery, TAKE_PHOTO);
                    } else {
                        // back to main activity
                        Intent result = new Intent();
                        result.putExtra(FROM_GALLERY, TAKE_PHOTO);
                        result.putExtra("DataChanged", true);
                        result.setClass(activity, MainActivity.class);
                        activity.startActivity(result);
                        activity.finish();
                    }
                } else if (items[index].equals("取消")) {
                    dialog.dismiss();
//                    Intent intent = new Intent();
//                    intent.setClass(MainActivity.this, MainActivity.class);
//                    startActivity(intent);
                }
            }
        });
        builder.show();
    }

    public void searchMeal(MenuItem menuItem){
        int searchId = menuItem.getItemId();

        switch (searchId){
            case R.id.search_all:
                foods = foodDAO.getAll();
                sports = sportDAO.getAll();

                foodAdapter = new FoodAdapter(this, R.layout.single_food, foods);
                sportAdapter = new SportAdapter(this, R.layout.single_sport, sports);

                eventAdapter = new MergeAdapter();
                eventAdapter.addAdapter(foodAdapter);
                eventAdapter.addAdapter(sportAdapter);
                event_list = (ListView)findViewById(R.id.event_list);
                processEventListControllers();
                event_list.setAdapter(eventAdapter);
                break;
            case R.id.search_breakfast:
                foods = foodDAO.getSelectedType(0);
                sports.clear();

                foodAdapter = new FoodAdapter(this, R.layout.single_food, foods);
                sportAdapter = new SportAdapter(this, R.layout.single_sport, sports);

                eventAdapter = new MergeAdapter();
                eventAdapter.addAdapter(foodAdapter);
                eventAdapter.addAdapter(sportAdapter);
                event_list = (ListView)findViewById(R.id.event_list);
                processEventListControllers();
                event_list.setAdapter(eventAdapter);
                break;
            case R.id.search_lunch:
                foods = foodDAO.getSelectedType(1);
                sports.clear();

                foodAdapter = new FoodAdapter(this, R.layout.single_food, foods);
                sportAdapter = new SportAdapter(this, R.layout.single_sport, sports);


                eventAdapter = new MergeAdapter();
                eventAdapter.addAdapter(foodAdapter);
                eventAdapter.addAdapter(sportAdapter);
                event_list = (ListView)findViewById(R.id.event_list);
                processEventListControllers();
                event_list.setAdapter(eventAdapter);
                break;
            case R.id.search_dinner:
                foods = foodDAO.getSelectedType(2);
                sports.clear();

                foodAdapter = new FoodAdapter(this, R.layout.single_food, foods);
                sportAdapter = new SportAdapter(this, R.layout.single_sport, sports);

                eventAdapter = new MergeAdapter();
                eventAdapter.addAdapter(foodAdapter);
                eventAdapter.addAdapter(sportAdapter);
                event_list = (ListView)findViewById(R.id.event_list);
                processEventListControllers();
                event_list.setAdapter(eventAdapter);
                break;
            case R.id.search_snack:
                foods = foodDAO.getSelectedType(3);
                sports.clear();

                foodAdapter = new FoodAdapter(this, R.layout.single_food, foods);
                sportAdapter = new SportAdapter(this, R.layout.single_sport, sports);

                eventAdapter = new MergeAdapter();
                eventAdapter.addAdapter(foodAdapter);
                eventAdapter.addAdapter(sportAdapter);
                event_list = (ListView)findViewById(R.id.event_list);
                processEventListControllers();
                event_list.setAdapter(eventAdapter);
                break;
            case R.id.search_night_snack:
                foods = foodDAO.getSelectedType(4);
                sports.clear();

                foodAdapter = new FoodAdapter(this, R.layout.single_food, foods);
                sportAdapter = new SportAdapter(this, R.layout.single_sport, sports);

                eventAdapter = new MergeAdapter();
                eventAdapter.addAdapter(foodAdapter);
                eventAdapter.addAdapter(sportAdapter);
                event_list = (ListView)findViewById(R.id.event_list);
                processEventListControllers();
                event_list.setAdapter(eventAdapter);
                break;
            case R.id.search_other:
                foods = foodDAO.getSelectedType(5);
                sports.clear();

                foodAdapter = new FoodAdapter(this, R.layout.single_food, foods);
                sportAdapter = new SportAdapter(this, R.layout.single_sport, sports);

                eventAdapter = new MergeAdapter();
                eventAdapter.addAdapter(foodAdapter);
                eventAdapter.addAdapter(sportAdapter);
                event_list = (ListView)findViewById(R.id.event_list);
                processEventListControllers();
                event_list.setAdapter(eventAdapter);
                break;
            case R.id.search_sport:
                foods.clear();
                sports = sportDAO.getAll();

                foodAdapter = new FoodAdapter(this, R.layout.single_food, foods);
                sportAdapter = new SportAdapter(this, R.layout.single_sport, sports);

                eventAdapter = new MergeAdapter();
                eventAdapter.addAdapter(foodAdapter);
                eventAdapter.addAdapter(sportAdapter);
                event_list = (ListView)findViewById(R.id.event_list);
                processEventListControllers();
                event_list.setAdapter(eventAdapter);
                break;
        }
    }
}