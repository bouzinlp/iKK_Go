package com.example.nthucs.prototype.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;


import ai.api.AIDataService;
import ai.api.AIListener;
import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Result;

import com.example.nthucs.prototype.Settings.Health;
import com.example.nthucs.prototype.Settings.HealthDAO;
import com.example.nthucs.prototype.Activity.CalorieConsumptionActivity;
import com.example.nthucs.prototype.Activity.MyProfileActivity;
import com.example.nthucs.prototype.Settings.MyProfileDAO;
import com.example.nthucs.prototype.Settings.Profile;
import com.facebook.login.widget.ProfilePictureView;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nthucs.prototype.R;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.services.concurrency.AsyncTask;

public class ChatBotActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener ,AIListener {
    private ImageButton btnSend;
    private ImageButton btnRecord;
    private AIService aiService;
    private RecyclerView recyclerView;
    private ArrayList messageArrayList;
    private ChatAdapter mAdapter;
    private EditText textMessage;
    private boolean initialRequest;

    private Activity activity = ChatBotActivity.this;
    private int activityIndex = 7;
    private static final int ChATBOT_ACTIVITY = 7;
    private static final int SCAN_FOOD = 2;
    private static final int TAKE_PHOTO = 3;
    private static final String FROM_CAMERA = "scan_food";
    private static final String FROM_GALLERY = "take_photo";
    private int flag = 0; //when flag = 0, language will be chinese ; flag = 1 will be english

    // To get user's blood pressure
    private Health curHealth;
    // data base for profile
    private HealthDAO healthDAO;
    // list of profile
    private List<Health> healthList = new ArrayList<>();

    // data base for profile
    private MyProfileDAO myProfileDAO;
    // list of profile
    private List<Profile> profileList = new ArrayList<>();

    private Profile curProfile;

    MyProfileActivity mypro = new MyProfileActivity();

    //init Ai config
    AIConfiguration config = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("健康管家");
        Fabric.with(this, new Crashlytics()); //to use fabric
        setContentView(R.layout.activity_chat_bot_nav);
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

        //  宣告 recyclerView
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        messageArrayList = new ArrayList<>();
        mAdapter = new ChatAdapter(messageArrayList);
        textMessage = (EditText) findViewById(R.id.message);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        this.textMessage.setText("");
        this.initialRequest = true;

        btnSend = (ImageButton) findViewById(R.id.btn_send);
        btnRecord= (ImageButton) findViewById(R.id.btn_record);

        //init Api ai listener
        //chinese ver.
        config = new AIConfiguration("2bc9ae934d8e44fb979bdd3d896de3c8",
                AIConfiguration.SupportedLanguages.ChineseTaiwan,
                AIConfiguration.RecognitionEngine.System);
        aiService = AIService.getService(this, config);
        aiService.setListener(this);



        btnRecord.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                    aiService.startListening();
            }
        });


        //by texting
        btnSend.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                senMessage();
            }
        });

    }

    public void onResult(final AIResponse response) {
        Result result = response.getResult();

        final String speech = result.getFulfillment().getSpeech();
        // Get parameters
        String parameterString = "";
        //TODO

        parameterString += aiResponses(parameterString,result);

        parameterString += speech;

        //TODO

        Message inputMessage = new Message();
        inputMessage.setMessage(result.getResolvedQuery());
        inputMessage.setAct(result.getAction());
        //inputMessage.setAns(parameterString);
        inputMessage.setId("1");
        messageArrayList.add(inputMessage);
        mAdapter.notifyDataSetChanged();
        this.initialRequest = false;

  //      response
        Message outMes = new Message();
        outMes.setMessage(parameterString);
        outMes.setId("2");
        messageArrayList.add(outMes);


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
                recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount()-1);
            }
        });


    }

    private void senMessage(){

        final String inputmes = textMessage.getText().toString().trim();

        final AIDataService aiDataService = new AIDataService(config);
        final AIRequest aiRequest = new AIRequest();
        aiRequest.setQuery(inputmes);
        textMessage.setText("");


        new AsyncTask<AIRequest, Void, AIResponse>() {
            @Override
            protected AIResponse doInBackground(AIRequest... requests) {
                final AIRequest request = requests[0];
                try {
                    final AIResponse response = aiDataService.request(request);
                    return response;
                } catch (AIServiceException e) {
                }
                return null;
            }
            @Override
            protected void onPostExecute(AIResponse aiResponse) {
                if (aiResponse != null) {
                    String parameterString = "";
                    Result result = aiResponse.getResult();
                    final String speech = result.getFulfillment().getSpeech();

                    parameterString += aiResponses(parameterString,result);
                    parameterString += speech;

                    final Message inputMessage = new Message();
                    inputMessage.setMessage(result.getResolvedQuery());
                    inputMessage.setAct(result.getAction());

                    inputMessage.setId("1");
                    messageArrayList.add(inputMessage);
                    mAdapter.notifyDataSetChanged();
                    initialRequest = false;

                    Message outMes = new Message();
                    outMes.setMessage(parameterString);
                    outMes.setId("2");
                    messageArrayList.add(outMes);


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount()-1);
                        }
                    });

                }
            }
        }.execute(aiRequest);
    }

    private String aiResponses(String parameterString,Result result) {
        //////to ger blood presure
        // initialize data base
        healthDAO = new HealthDAO(getApplicationContext());
        // get all health data from data base
        healthList = healthDAO.getAll();
        // get the last health data in the list
        if (healthDAO.isTableEmpty() == true) {
            curHealth = new Health();
        } else {
            int cnt = 0;
            for (int i = 0; i < healthList.size(); i++) {
                if (healthList.get(i).getDiastolicBloodPressure() != 0
                        || healthList.get(i).getSystolicBloodPressure() != 0 || healthList.get(i).getPulse() != 0) {
                    curHealth = healthList.get(i);
                    cnt = 1;
                }
            }
            if (cnt == 0) curHealth = new Health();
        }

        //to get user profile
        // initialize data base
        myProfileDAO = new MyProfileDAO(getApplicationContext());
        profileList = myProfileDAO.getAll();

        // get the last profile data in the list
        if (myProfileDAO.isTableEmpty() == true) {
            curProfile = new Profile();
        } else {
            curProfile = profileList.get(profileList.size() - 1);
        }
        for (final Map.Entry<String, JsonElement> entry : result.getParameters().entrySet()) {

            float sys_pre = curHealth.getSystolicBloodPressure();
            float dia_pre = curHealth.getDiastolicBloodPressure();
            float pro_height = curProfile.getHeight();
            float pro_weight = curProfile.getWeight();
            float hi = (pro_height / 100);
            float pro_BMI = pro_weight / (hi * hi);

            if (flag == 1) {
                switch (result.getAction()) {
                    case "Get_pressure":
                        parameterString += "your blood pressure is " + String.valueOf(sys_pre) +
                                "/" + String.valueOf(dia_pre) + ".";
                        if (sys_pre < 90.0 || dia_pre < 60.0)
                            parameterString += " you have low blood pressure,go to see a doctor, plz.";
                        else if (sys_pre >= 140.0 || dia_pre >= 90.0)
                            parameterString += " you have high blood pressure,go to see a doctor, plz.";
                        else parameterString += " your blood pressure is normal, keep on hold";
                        break;
                    case "Get_systolic_blood_pressure":
                        parameterString += "your systolic pressure is " + String.valueOf(sys_pre) + ".";
                        if (sys_pre < 90.0)
                            parameterString += " you have low blood pressure,go to see a doctor, plz.";
                        else if (sys_pre >= 140.0)
                            parameterString += " you have high blood pressure,go to see a doctor, plz.";
                        else parameterString += " your systolic pressure is normal, keep on hold";
                        break;

                    case "Get_diastolic_blood_pressure":
                        parameterString += "your diastolic pressure is " + String.valueOf(sys_pre);
                        if (sys_pre < 90.0)
                            parameterString += " you have low blood pressure,go to see a doctor, plz.";
                        else if (sys_pre >= 140.0)
                            parameterString += " you have high blood pressure,go to see a doctor, plz.";
                        else parameterString += " your diastolic pressure is normal, keep on hold";
                        break;

                    case "Get_BMI":
                        parameterString += ("your BMI is " + String.valueOf(pro_BMI));
                        if (pro_BMI >= 24) {
                            parameterString += " You are overweight. Do exercise and control diet, plz";
                        } else if (pro_BMI < 18.5) {
                            parameterString += " You are overweight. pay attention to balanced diet and do exercise";
                        } else parameterString += " Your BMI is normal. Congrats!";
                        break;
                    case "Get_height":
                        parameterString += ("Your height is " + String.valueOf(pro_height));
                        break;
                    case "Get_weight":
                        parameterString += ("Your weight is " + String.valueOf(pro_weight));
                        break;
                }
            } else {
                switch (result.getAction()) {
                    case "get_pressure_info":
                        parameterString += "你的收縮壓/舒張壓為 " + String.valueOf(sys_pre) +
                                "/" + String.valueOf(dia_pre);
                        if (sys_pre < 90.0 || dia_pre < 60.0)
                            parameterString += "正常收縮壓/舒張壓為90~140/60~90。您可能有低血壓，請休息幾分鐘再次測量。";
                        else if (sys_pre >= 140.0 || dia_pre >= 90.0)
                            parameterString += "正常收縮壓/舒張壓為90~140/60~90。您可能有高血壓，請休息幾分鐘再次測量。";
                        else parameterString += "您的血壓正常，請繼續保持";
                        break;
                    case "get_systolic_pressure_info":
                        parameterString += "你的收縮壓為 " + String.valueOf(sys_pre);
                        if (sys_pre < 90.0)
                            parameterString += "正常收縮壓為90~140/60~90。您可能有低血壓，請休息幾分鐘再次測量。";
                        else if (sys_pre >= 140.0)
                            parameterString += "正常收縮壓為90~140。您可能有高血壓，請休息幾分鐘再次測量。";
                        else parameterString += "您的收縮壓正常，請繼續保持";
                        break;
                    case "get_diastolic_pressure_info":
                        parameterString += "你的舒張壓為 " + String.valueOf(dia_pre);
                        if (dia_pre < 60.0) parameterString += "正常舒張壓為60~90。您可能有低血壓，請休息幾分鐘再次測量。";
                        else if (dia_pre >= 90.0)
                            parameterString += "正常舒張壓為90~140/60~90。您可能有高血壓，請休息幾分鐘再次測量。";
                        else parameterString += "您的舒張壓正常，請繼續保持";
                        break;
                    case "get_pressure_high_or_low":
                        if (sys_pre < 90.0 || dia_pre < 60.0) parameterString += "您有低血壓，請前往醫院了解詳情";
                        else if (sys_pre >= 140.0 || dia_pre >= 90.0)
                            parameterString += "您有高血壓，請前往醫院了解詳情";
                        else parameterString += "您的血壓正常，請繼續保持";
                        break;
                    case "get_bmi_info":

                        parameterString += ("您的BMI為" + String.valueOf(pro_BMI));
                        if (pro_BMI >= 24) {
                            parameterString += " 正常範圍是18.5~24。您過重了，請適量運動並控制飲食，並定期檢查BMI";
                        } else if (pro_BMI < 18.5) {
                            parameterString += " 正常範圍是18.5~24。您過輕了 均衡飲食有助身體健康";
                        } else parameterString += " BMI正常，請繼續保持";
                        break;
                    case "get_height_info":
                        parameterString += ("您的身高為" + String.valueOf(pro_height));
                        break;
                    case "get_weight_info":
                        parameterString += ("您的體重為" + String.valueOf(pro_weight));
                        break;
                }
            }
        }

        return parameterString;
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.home) {
            Intent intent_home = new Intent();
            intent_home.setClass(ChatBotActivity.this, HomeActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("BACK", 1);
            intent_home.putExtras(bundle);
            startActivity(intent_home);
            finish();
        } else if (id == R.id.food_list) {
            // Handle the camera action
            Intent intent_main = new Intent();
            intent_main.setClass(ChatBotActivity.this, MainActivity.class);
            startActivity(intent_main);
            finish();
            //Toast.makeText(this, "Open food list", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.new_calendar) {
            Intent intent_new_calendar = new Intent();
            intent_new_calendar.setClass(ChatBotActivity.this, NewCalendarActivity.class);
            startActivity(intent_new_calendar);
            finish();
            //Toast.makeText(this, "Open calendar", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.Import) {
            selectImage();
            //Toast.makeText(this, "Import food", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.message) {
            Intent intent_message = new Intent();
            intent_message.setClass(ChatBotActivity.this, MessageActivity.class);
            startActivity(intent_message);
            finish();
            //Toast.makeText(this, "Send message", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.setting_list) {
            Intent intent_setting = new Intent();
            intent_setting.setClass(ChatBotActivity.this, SettingsActivity.class);
            startActivity(intent_setting);
            finish();
        } else if (id == R.id.blood_pressure) {
            Intent intent_blood_pressure = new Intent();
            intent_blood_pressure.setClass(ChatBotActivity.this, MyBloodPressure.class);
            startActivity(intent_blood_pressure);
            finish();
        } else if (id == R.id.mail) {
            Intent intent_mail = new Intent();
            intent_mail.setClass(ChatBotActivity.this, MailActivity.class);
            startActivity(intent_mail);
            finish();
        } else if (id == R.id.chat) {
            Intent intent_chat_bot = new Intent();
            intent_chat_bot.setClass(ChatBotActivity.this, ChatBotActivity.class);
            startActivity(intent_chat_bot);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onError(final AIError error) {

    }

    @Override
    public void onListeningStarted() {
    }

    @Override
    public void onListeningCanceled() {
    }

    @Override
    public void onListeningFinished() {
    }

    @Override
    public void onAudioLevel(final float level) {
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void selectImage() {
        final CharSequence[] items = {"照相", "從相簿中選取", "取消"};
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("新增食物");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int index) {
                if (items[index].equals("照相")) {
                    if (activityIndex == ChATBOT_ACTIVITY) {
                        Intent intent_camera = new Intent("com.example.nthucs.prototype.TAKE_PICT");

                        activity.startActivityForResult(intent_camera, SCAN_FOOD);
                    } else {
                        // back to mail activity
                        Intent result = new Intent();
                        result.putExtra(FROM_CAMERA, SCAN_FOOD);
                        result.setClass(activity, MainActivity.class);
                        activity.startActivity(result);
                        activity.finish();
                    }
                } else if (items[index].equals("從相簿中選取")) {
                    if (activityIndex == ChATBOT_ACTIVITY) {
                        Intent intent_gallery = new Intent("com.example.nthucs.prototype.TAKE_PHOTO");
                        //intent_gallery.putParcelableArrayListExtra(calDATA, foodCalList);
                        activity.startActivityForResult(intent_gallery, TAKE_PHOTO);
                    } else {
                        // back to mail activity
                        Intent result = new Intent();
                        result.putExtra(FROM_GALLERY, TAKE_PHOTO);
                        result.setClass(activity, MainActivity.class);
                        activity.startActivity(result);
                        activity.finish();
                    }
                } else if (items[index].equals("取消")) {
                    dialog.dismiss();
//                    Intent intent = new Intent();
//                    intent.setClass(ChatBotActivity.this, MailActivity.class);
//                    startActivity(intent);
                }
            }
        });
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chat_bot, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.language_change){
            if(flag == 0){
                config = new AIConfiguration("3f5da70a97c44731b8d7ac44b6acb7ef",
                        AIConfiguration.SupportedLanguages.English,
                        AIConfiguration.RecognitionEngine.System);
                aiService = AIService.getService(this, config);
                aiService.setListener(this);
                flag = 1;
                Toast.makeText(ChatBotActivity.this, "英文管家", Toast.LENGTH_SHORT).show();
            }
            else{
                config = new AIConfiguration("2bc9ae934d8e44fb979bdd3d896de3c8",
                        AIConfiguration.SupportedLanguages.ChineseTaiwan,
                        AIConfiguration.RecognitionEngine.System);
                aiService = AIService.getService(this, config);
                aiService.setListener(this);
                flag = 0;
                Toast.makeText(ChatBotActivity.this, "中文管家", Toast.LENGTH_SHORT).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }


}
