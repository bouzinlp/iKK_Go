package com.example.nthucs.prototype.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nthucs.prototype.R;
import com.example.nthucs.prototype.Settings.Health;
import com.example.nthucs.prototype.Settings.HealthDAO;
import com.example.nthucs.prototype.Settings.SettingAdapter;
import com.example.nthucs.prototype.TabsBar.TabsController;
import com.example.nthucs.prototype.TabsBar.ViewPagerAdapter;
import com.example.nthucs.prototype.Utility.DBFunctions;
import com.example.nthucs.prototype.Utility.MyDBHelper;
import com.facebook.login.widget.ProfilePictureView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by user on 2016/7/16.
 */
public class SettingsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    // list view adapter for setting list
    private SettingAdapter settingAdapter;
    private Activity activity = SettingsActivity.this;
    private int activityIndex = 4;
    private static final int SETTING_ACTIVITY = 4;
    private static final int SCAN_FOOD = 2;
    private static final int TAKE_PHOTO = 3;
    private static final String FROM_CAMERA = "scan_food";
    private static final String FROM_GALLERY = "take_photo";

    // settings' title
    private static final String myProfile = "My Profile";
    private static final String myCurrentExercise = "My Current Exercise";//運動紀錄
    private static final String myWeightLossGoal = "My Weight Loss Goal";
    private static final String displayAsMetricImperial = "Display as metric/imperial";
    private static final String weightChart = "Weight Chart";
    private static final String calorieConsumption = "Calorie Consumption";
    private static final String myFavourites = "My Favourites";
    //private static final String myBloodPressure = "My Blood Pressure";
    private static final String drinkWaterDiary = "Drink Water Diary";
    private static final String myTemperatureRecord = "My Temperature Record";
    private static final String testJsoup = "Test Jsoup";
	// , drinkWaterDiary , myTemperatureRecord
    private String[] titleStr = new String[]{myProfile, myCurrentExercise, myWeightLossGoal, displayAsMetricImperial, weightChart, calorieConsumption, myFavourites , drinkWaterDiary, myTemperatureRecord ,testJsoup};

    // list view for including textView
    private ListView setting_list;

    // string list for every setting item's title
    private List<String> setting_title;
    private ArrayAdapter<String> adapter;

    // element for the bottom of the tab content
    private ViewPager viewPager;
    private TabLayout tabLayout;

    //DB Class to perform DB related operations
    DBFunctions dbFunctions;
    //Progress Dialog Object
    ProgressDialog prgDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Settings");
        setContentView(R.layout.activity_settings_nav);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //constuct db object
        dbFunctions = new DBFunctions(this.getApplicationContext());
        //Initialize Progress Dialog properties
        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Synching SQLite Data with Remote MySQL DB. Please wait...");
        prgDialog.setCancelable(false);
        // initialize tabLayout and viewPager
        //viewPager = (ViewPager)findViewById(R.id.viewPager);
        //tabLayout = (TabLayout)findViewById(R.id.tabLayout);
        //initializeTabLayout();

        // call function to active tabs listener
        //TabsController tabsController = new TabsController(4, SettingsActivity.this, tabLayout, viewPager);
        //tabsController.processTabLayout();

        // initialize setting list and process controllers
        setting_list = (ListView)findViewById(R.id.setting_list);
        processControllers();

        // initialize and set adapter, pass title with string
        //setting_title = new ArrayList<>(Arrays.asList(titleStr));
        //settingAdapter = new SettingAdapter(this, R.layout.single_setting, setting_title);
        //setting_list.setAdapter(settingAdapter);

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


        setting_list = (ListView) findViewById(R.id.setting_list);
        setting_title = new ArrayList<>(Arrays.asList(titleStr));
        adapter = new ArrayAdapter<>(this, R.layout.custom_list_view, setting_title);
        setting_list.setAdapter(adapter);

        //selectTab(4);
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

    @Override
    protected void onResume() {
        super.onResume();

        // Always select tab 4
        //selectTab(4);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.setting_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // select specific tab
    private void selectTab(int index) {
        TabLayout.Tab tab = tabLayout.getTabAt(index);
        tab.select();
    }

    // process list click listener
    private void processControllers() {
        // construct settings list click listener
        AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String title = adapter.getItem(position);
                switch (title) {
                    // go to my profile activity
                    case myProfile:
                        Intent intent_profile = new Intent();
                        intent_profile.setClass(SettingsActivity.this, MyProfileActivity.class);
                        startActivity(intent_profile);
                        finish();
                        break;
                    case myCurrentExercise:
                        Intent intent_current_exercise = new Intent();
                        intent_current_exercise.setClass(SettingsActivity.this, MyCurrentExerciseActivity.class);
                        startActivity(intent_current_exercise);
                        finish();
                        break;
                    case myWeightLossGoal:
                        Intent intent_weight_loss = new Intent();
                        intent_weight_loss.setClass(SettingsActivity.this, MyWeightLossGoalActivity.class);
                        startActivity(intent_weight_loss);
                        finish();
                        break;
                    case displayAsMetricImperial:
                        Intent intent_display = new Intent();
                        intent_display.setClass(SettingsActivity.this, DisplayAsMetricImperialActivity.class);
                        startActivity(intent_display);
                        finish();
                        break;
                    case weightChart:
                        Intent intent_weight_chart = new Intent();
                        intent_weight_chart.setClass(SettingsActivity.this, WeightChartActivity.class);
                        startActivity(intent_weight_chart);
                        finish();
                        break;
                    case calorieConsumption:
                        Intent intent_calorie_consumption = new Intent();
                        intent_calorie_consumption.setClass(SettingsActivity.this, CalorieConsumptionActivity.class);
                        startActivity(intent_calorie_consumption);
                        finish();
                        break;
                    case myFavourites:
                        Intent intent_my_favourites = new Intent();
                        intent_my_favourites.setClass(SettingsActivity.this, MyFavouritesActivity.class);
                        startActivity(intent_my_favourites);
                        finish();
                        break;
//                    case myBloodPressure:
//                        Intent intent_my_blood_pressure = new Intent();
//                        intent_my_blood_pressure.setClass(SettingsActivity.this , MyBloodPressure.class);
//                        startActivity(intent_my_blood_pressure);
//                        finish();
//                        break;
                    case drinkWaterDiary:
                        Intent intent_drink_water_diary = new Intent();
                        intent_drink_water_diary.setClass(SettingsActivity.this , DrinkWaterDiary.class);
                        startActivity(intent_drink_water_diary);
                        finish();
                        break;
                    case myTemperatureRecord:
                        Intent intent_my_temperature_record = new Intent();
                        intent_my_temperature_record.setClass(SettingsActivity.this , MyTemperatureRecord.class);
                        startActivity(intent_my_temperature_record);
                        finish();
                        break;
					case testJsoup:
                        Intent intent_test_jsoup = new Intent();
                        intent_test_jsoup.setClass(SettingsActivity.this , TestJsoup.class);
                        startActivity(intent_test_jsoup);
                        finish();
                        break;

                }
            }
        };

        // register settings list click listener
        setting_list.setOnItemClickListener(itemListener);
    }


    public void syncSQLiteMySQLDB(MenuItem item){
        //Create AsycHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        ArrayList<HashMap<String, String>> userList = dbFunctions.getAllUsers();
        if(userList.size()!=0){
            if(dbFunctions.dbSyncCount() != 0){
                prgDialog.show();
                params.put("usersJSON", dbFunctions.composeUserfromSQLite());
                params.put("foodJSON", dbFunctions.composeFoodfromSQLite());
                params.put("sportJSON",dbFunctions.composeSportfromSQLite());
                params.put("healthJSON",dbFunctions.composeHealthfromSQLite());
                System.out.println("AAAAAAAAAAA = "+dbFunctions.composeHealthfromSQLite());
                client.setTimeout(10000);
                client.post("http://140.114.88.136:80/mhealth/insertuser.php",params ,new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int status, cz.msebera.android.httpclient.Header[] headers, byte[] bytes) {
                        prgDialog.hide();
                        try {
                            String str = new String(bytes,"UTF-8");
                            System.out.println(str);
                            Toast.makeText(getApplicationContext(), "DB Sync completed!", Toast.LENGTH_LONG).show();


                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int status, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {
                        prgDialog.hide();
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

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.home) {
            Intent intent_home = new Intent();
            intent_home.setClass(SettingsActivity.this, HomeActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("BACK", 1);
            intent_home.putExtras(bundle);
            startActivity(intent_home);
            finish();
        }
        else if (id == R.id.food_list) {
            Intent intent_main = new Intent();
            intent_main.setClass(SettingsActivity.this, MainActivity.class);
            startActivity(intent_main);
            finish();
            //Toast.makeText(this, "Open food list", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.calendar) {
            Intent intent_calendar = new Intent();
            intent_calendar.setClass(SettingsActivity.this, CalendarActivity.class);
            startActivity(intent_calendar);
            finish();
            //Toast.makeText(this, "Open calendar", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.Import) {
            selectImage();
            //Toast.makeText(this, "Import food", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.message) {
            Intent intent_message = new Intent();
            intent_message.setClass(SettingsActivity.this, MessageActivity.class);
            startActivity(intent_message);
            finish();
            //Toast.makeText(this, "Send message", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.setting_list) {
            Intent intent_setting = new Intent();
            intent_setting.setClass(SettingsActivity.this, SettingsActivity.class);
            startActivity(intent_setting);
            finish();
        } else if (id == R.id.blood_pressure){
            Intent intent_blood_pressure = new Intent();
            intent_blood_pressure.setClass(SettingsActivity.this, MyBloodPressure.class);
            startActivity(intent_blood_pressure);
            finish();
        } else if (id == R.id.mail){
            Intent intent_mail = new Intent();
            intent_mail.setClass(SettingsActivity.this, MailActivity.class);
            startActivity(intent_mail);
            finish();
        } else if (id == R.id.new_calendar){
            Intent intent_new_calendar = new Intent();
            intent_new_calendar.setClass(SettingsActivity.this, NewCalendarActivity.class);
            startActivity(intent_new_calendar);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void selectImage(){
        final CharSequence[] items = { "Take with Camera", "Choose from Gallery", "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Select Image");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int index) {
                if (items[index].equals("Take with Camera")) {
                    if (activityIndex == SETTING_ACTIVITY) {
                        Intent intent_camera = new Intent("com.example.nthucs.prototype.TAKE_PICT");

                        activity.startActivityForResult(intent_camera, SCAN_FOOD);
                    } else {
                        // back to setting activity
                        Intent result = new Intent();
                        result.putExtra(FROM_CAMERA, SCAN_FOOD);
                        result.setClass(activity, SettingsActivity.class);
                        activity.startActivity(result);
                        activity.finish();
                    }
                } else if (items[index].equals("Choose from Gallery")) {
                    if (activityIndex == SETTING_ACTIVITY) {
                        Intent intent_gallery = new Intent("com.example.nthucs.prototype.TAKE_PHOTO");
                        //intent_gallery.putParcelableArrayListExtra(calDATA, foodCalList);
                        activity.startActivityForResult(intent_gallery, TAKE_PHOTO);
                    } else {
                        // back to setting activity
                        Intent result = new Intent();
                        result.putExtra(FROM_GALLERY, TAKE_PHOTO);
                        result.setClass(activity, SettingsActivity.class);
                        activity.startActivity(result);
                        activity.finish();
                    }
                } else if (items[index].equals("Cancel")) {
                    dialog.dismiss();
                    Intent intent = new Intent();
                    intent.setClass(SettingsActivity.this, SettingsActivity.class);
                    startActivity(intent);
                }
            }
        });
        builder.show();
    }
}
