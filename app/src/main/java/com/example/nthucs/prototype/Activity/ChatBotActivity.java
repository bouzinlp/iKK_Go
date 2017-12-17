package com.example.nthucs.prototype.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.example.nthucs.prototype.FoodList.CalorieDAO;
import com.example.nthucs.prototype.FoodList.Food;
import com.example.nthucs.prototype.FoodList.FoodCal;
import com.example.nthucs.prototype.FoodList.FoodDAO;
import com.example.nthucs.prototype.R;
import com.example.nthucs.prototype.Settings.Health;
import com.example.nthucs.prototype.Settings.HealthDAO;
import com.example.nthucs.prototype.Settings.MyProfileDAO;
import com.example.nthucs.prototype.Settings.Profile;
import com.example.nthucs.prototype.SportList.Sport;
import com.example.nthucs.prototype.SportList.SportDAO;
import com.facebook.login.widget.ProfilePictureView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ai.api.AIDataService;
import ai.api.AIListener;
import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.GoogleAssistantResponseMessages;
import ai.api.model.Result;
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
    private long local_datetime;
    private static boolean heart_disease = false;
    private static boolean diabetes_disease = false;

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

    // data base for storing calorie data
    private CalorieDAO calorieDAO;

    // list of foodCal
    private List<FoodCal> foodCalList = new ArrayList<>();

    // data base for storing food list
    private FoodDAO foodDAO;

    // list of foods
    private List<Food> foods;

    // data base for storing sport list
    private SportDAO sportDAO;

    // list of sports
    private List<Sport> sports;

    private List<Food> todayFoods = new ArrayList<Food>();

    private List<Sport> todaySports = new ArrayList<Sport>();

    private float idel_absorb_cal, idel_consume_cal;

    //boolean attribute that controls sound
    private boolean sound_open = false;

    MyWeightLossGoalActivity mwlga;

    //init Ai config
    AIConfiguration config = null;

    TextToSpeech text_to_audio = null;

    private String food_name = new String("");

    String A_Food = new String("A_Food");String A_Food1 = new String("A_Food1");String A_Food2 = new String("A_Food2");
    String B_Food = new String("B_Food");String B_Food1 = new String("B_Food1");String B_Food2 = new String("B_Food2");
    String D_Food = new String("D_Food");String D_Food1 = new String("D_Food1");String D_Food2 = new String("D_Food2");
    String E_Food = new String("E_Food");String E_Food1 = new String ("E_Food1");String E_Food2 = new String ("E_Food2");
    String F_Food = new String("F_Food");String F_Food1 = new String("F_Food1");String O_Food3 = new String("O_Food3");
    String G_Food = new String("G_Food");String G_Food1 = new String("G_Food1");String G_Food2 = new String("G_Food2");
    String I_Food = new String("I_Food");String I_Food1 = new String("I_Food1");String I_Food2 = new String("I_Food2");
    String J_Food = new String("J_Food");String J_Food1 = new String("J_Food1");String J_Food2 = new String("J_Food2");
    String K_Food = new String("K_Food");String K_Food1 = new String("K_Food1");String K_Food2 = new String("K_Food2");
    String L_Food = new String("L_Food");String L_Food1 = new String("L_Food1");String L_Food2 = new String("L_Food2");
    String O_Food = new String("O_Food");String O_Food1 = new String("O_Food1");String O_Food2 = new String("O_Food2");


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
        recyclerView.scrollToPosition(messageArrayList.size()-1);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        this.textMessage.setText("");
        this.initialRequest = true;
        //ProfilePictureView profilePicture_chatbot = (ProfilePictureView)recyclerView.get
        //profilePicture_chatbot.setCropped(true);
        //profilePicture_chatbot.setProfileId(LoginActivity.facebookUserID);

        btnSend = (ImageButton) findViewById(R.id.btn_send);
        btnRecord= (ImageButton) findViewById(R.id.btn_record);

        //text to audio implementation (By YuJui Chen)
        // TextToAudio English Version
        text_to_audio = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status!=TextToSpeech.ERROR){
                    text_to_audio.setLanguage(Locale.TAIWAN);
                }
            }
        }
        );

        //init Api ai listener
        //chinese ver.
        //change server on 09/03
        config = new AIConfiguration("a772958d63a149b39bf9f11cfad29889",
                AIConfiguration.SupportedLanguages.ChineseTaiwan,
                AIConfiguration.RecognitionEngine.System);
        aiService = AIService.getService(this, config);
        aiService.setListener(this);

        //Instruction (chatbot使用說明 => 讓user清楚知道目前聊天機器人有甚麼功能)
        Message ini_message = new Message();
        ini_message.setId("3");
        ini_message.setMessage("您好"+LoginActivity.facebookName +"\n"+
                "本聊天機器人目前支援功能有\n" +
                "1.詢問/設定 身高、體重、bmi\n" +
                "2.詢問飲食順序\n" +
                "3.詢問/設定 血壓、脈搏狀況\n"+
        "4.查詢目前消耗/吸收熱量\n"+
        "5.查詢今日所吃的食物 ");


        messageArrayList.add(ini_message);

        mAdapter.notifyDataSetChanged();

        btnRecord.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                aiService.startListening();
                btnRecord.setImageResource(R.drawable.icons8_ic_mic_red_24px);
                Toast.makeText(v.getContext(),"錄音中",Toast.LENGTH_SHORT).show();
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
        btnRecord.setImageResource(R.drawable.ic_mic_black_48dp);

        Result result = response.getResult();

        final String speech = result.getFulfillment().getSpeech();
        // Get parameters

        String parameterString = "";
        parameterString += aiResponses(parameterString,result);

        parameterString += speech;

        if(sound_open == true)
            text_to_audio.speak(parameterString.toString(),TextToSpeech.QUEUE_FLUSH,null);

        Message inputMessage = new Message();
        inputMessage.setMessage(result.getResolvedQuery());
        inputMessage.setAct(result.getAction());
        //inputMessage.setAns(parameterString);
        inputMessage.setId("1");
        messageArrayList.add(inputMessage);
        mAdapter.notifyDataSetChanged();
        this.initialRequest = false;

        //response
        Message outMes = new Message();
        outMes.setMessage(parameterString);
        outMes.setId("2");
        messageArrayList.add(outMes);

        //

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
                recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount()-1);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void senMessage_test(){

        final String inputmes = textMessage.getText().toString().trim();

        if(!inputmes.isEmpty()) {
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

                        parameterString += aiResponses(parameterString, result);
                        parameterString += speech;

                        /*final Message inputMessage = new Message();
                        inputMessage.setMessage(result.getResolvedQuery());
                        inputMessage.setAct(result.getAction());

                        inputMessage.setId("1");
                        messageArrayList.add(inputMessage);
                        mAdapter.notifyDataSetChanged();*/
                        initialRequest = false;

                        Message outMes = new Message();
                        outMes.setMessage(parameterString);
                        outMes.setId("2");
                        messageArrayList.add(outMes);


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.notifyDataSetChanged();
                                recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);
                            }
                        });

                    }
                }
            }.execute(aiRequest);
        }
        else{
            Toast.makeText(this,"訊息不可為空！",Toast.LENGTH_SHORT).show();
        }
    }

    private void senMessage(){

        final String inputmes = textMessage.getText().toString().trim();

        if(!inputmes.isEmpty()) {
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

                        parameterString += aiResponses(parameterString, result);
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
                                recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);
                            }
                        });

                    }
                }
            }.execute(aiRequest);
        }
        else{
            Toast.makeText(this,"訊息不可為空！",Toast.LENGTH_SHORT).show();
        }
    }

    private String aiResponses(String parameterString,Result result) {
        //////to get blood presure
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
        //for (final Map.Entry<String, JsonElement> entry : result.getParameters().entrySet()) {

        float sys_pre = curHealth.getSystolicBloodPressure();
        float dia_pre = curHealth.getDiastolicBloodPressure();
        float pro_height = curProfile.getHeight();
        float pro_weight = curProfile.getWeight();
        float pulse = curHealth.getPulse();
        float hi = (pro_height / 100);
        float pro_BMI = pro_weight / (hi * hi);
        float tem = curHealth.getTemperature();
        int water_drunk = curHealth.getDrunkWater();


        String date = new String("date");
        String sys_height = new String("number");
        String sys_weight = new String("number");
        String sys_height_eng = new String("unit-length");
        String sys_weight_eng = new String("unit-weight");
        String pressure_num1 = new String("number");
        String pressure_num2 = new String("number1");
        String pressure_parameter = new String("blood_pressure_problem");
        String pressure_para_eng = new String("Pressure_Problem");
        String pressure_para_eng1 = new String("Pressure_Problem1");
        String disease = new String("disease");

        float total_absorb_calories=0;
        float total_consume_calories=0;

        // to get user already have eaten food
        foodDAO = new FoodDAO(getApplicationContext());
        foods = foodDAO.getAll();

        // to get user already have exercised
        sportDAO = new SportDAO(getApplicationContext());
        sports = sportDAO.getAll();

        //database
        calorieDAO = new CalorieDAO(getApplicationContext());
        foodCalList = calorieDAO.getAll();

        //parameterString += foodCalList.get(foodCalList.size()-1).getChineseName();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/M/d");  //定義時間格式
        Date dt = new Date();  //取得目前時間
        String dts = sdf.format(dt);  //經由SimpleDateFormat將時間轉為字串

        // to get user's weight loss goal
        SharedPreferences sharedPreferences = getSharedPreferences("LossGoal",MODE_PRIVATE);

        //get today's total food
        for(int i=0;i<foods.size();i++){
            if(dts.equals(foods.get(i).getYYYYMD())){
                todayFoods.add(foods.get(i));
            }
        }

        for(int i=0;i<sports.size();i++){
            if(dts.contentEquals(sports.get(i).getYYYYMD())){
                todaySports.add(sports.get(i));
            }
        }

            if (flag == 1) {
                switch (result.getAction()) {
                    case "Get_pressure":
                        if(sys_pre ==0 || dia_pre == 0)
                            parameterString += ("Please set the info about blood pressure first!!");
                        else {
                            parameterString += "Your blood pressure is " + String.valueOf(sys_pre) + "(mmHg)"+
                                    "/" + String.valueOf(dia_pre) + "(mmHg)" +".";
                            if (sys_pre < 90.0 || dia_pre < 60.0)
                                parameterString += " You have low blood pressure,go to see a doctor, plz.";
                            else if (sys_pre >= 140.0 || dia_pre >= 90.0)
                                parameterString += " You have high blood pressure,go to see a doctor, plz.";
                            else parameterString += " Your blood pressure is normal, keep on hold";
                        }
                        break;
                    case "Get_systolic_blood_pressure":
                        if(sys_pre == 0)
                            parameterString += ("Please set the info about blood pressure first!!");
                        else {
                            parameterString += "Your systolic pressure is " + String.valueOf(sys_pre) + " (mmHg) "+ ".";
                            if (sys_pre < 90.0)
                                parameterString += " You have low blood pressure,go to see a doctor, plz.";
                            else if (sys_pre >= 140.0)
                                parameterString += " You have high blood pressure,go to see a doctor, plz.";
                            else
                                parameterString += " Your systolic pressure is normal, keep on hold";
                        }
                        break;
                    case "Get_diastolic_blood_pressure":
                        if(dia_pre == 0)
                            parameterString += ("Please set the info about blood pressure first!!");
                        else {
                            parameterString += "Your diastolic pressure is " + String.valueOf(dia_pre) + " (mmHg)" +".";
                            if (dia_pre < 60.0)
                                parameterString += " You have low blood pressure,go to see a doctor, plz.";
                            else if (dia_pre >= 90.0)
                                parameterString += " You have high blood pressure,go to see a doctor, plz.";
                            else
                                parameterString += " Your diastolic pressure is normal, keep on hold";
                        }
                        break;
                    case "blood_pressure_high_or_low":
                        if(dia_pre ==0 || sys_pre ==0)
                            parameterString += ("Please set the info about blood pressure first!!");
                        else {
                            if (dia_pre < 60.0)
                                parameterString += " You have low blood pressure,go to see a doctor, plz.";
                            else if (dia_pre >= 90.0)
                                parameterString += " You have high blood pressure,go to see a doctor, plz.";
                            else parameterString += " Your blood pressure is normal, keep on hold";
                        }
                        break;
                    case "set_pressure":
                        if(result.getFloatParameter(pressure_num1)!=0 && result.getFloatParameter(pressure_num2)!=0){
                            if(result.getStringParameter(pressure_para_eng).equals("systolic blood pressure")) {
                                parameterString += ("Congrats!! You update the info successfully." + "\n");
                                curHealth.setSystolicBloodPressure(result.getFloatParameter(pressure_num1));
                                curHealth.setDiastolicBloodPressure(result.getFloatParameter(pressure_num2));
                                //curHealth.setDatetime(calendar.getTimeInMillis());
                                curHealth.setLastModify(new Date().getTime());
                            }
                            else if(result.getStringParameter(pressure_para_eng).equals("diastolic blood pressure")) {
                                parameterString += ( "Congrats!! You update the info successfully." + "\n");
                                curHealth.setSystolicBloodPressure(result.getFloatParameter(pressure_num2));
                                curHealth.setDiastolicBloodPressure(result.getFloatParameter(pressure_num1));
                            }
                            else{
                                parameterString += ("Please specify which kind of blood pressure you want to update (systolic blood or diastolic blood pressure)");
                            }
                            healthDAO.update(curHealth); //update database
                            sys_pre = curHealth.getSystolicBloodPressure();
                            dia_pre = curHealth.getDiastolicBloodPressure();
                            parameterString += ("Your systolic blood pressure is" + String.valueOf(sys_pre) + "(mmHg)"+"\n");
                            parameterString += ("Your diastolic blood pressure is " + String.valueOf(dia_pre) + "(mmHg)");
                        }
                        else if(result.getFloatParameter(pressure_num1)!=0 && result.getFloatParameter(pressure_num2)==0){
                            if(result.getStringParameter(pressure_para_eng).equals("systolic blood pressure")){
                                parameterString += ("Congrats!! You update the info successfully." + "\n");
                                curHealth.setSystolicBloodPressure(result.getFloatParameter(pressure_num1));
                                sys_pre = curHealth.getSystolicBloodPressure();
                                parameterString += ("Your systolic blood pressure is" + String.valueOf(sys_pre) + "(mmHg)");
                                healthDAO.update(curHealth);
                            }else if(result.getStringParameter(pressure_para_eng).equals("diastolic blood pressure")){
                                parameterString += ("Congrats!! You update the info successfully." + "\n");
                                curHealth.setDiastolicBloodPressure(result.getFloatParameter(pressure_num1));
                                dia_pre = curHealth.getDiastolicBloodPressure();
                                parameterString += ("Your diastolic blood pressure is" + String.valueOf(dia_pre) + "(mmHg)");
                                healthDAO.update(curHealth);
                            }else{
                                parameterString += ("Please specify which kind of blood pressure you want to update (systolic blood or diastolic blood pressure)");
                            }
                        }
                        else{
                            parameterString +=("Please specify which kind of blood pressure you want to update (systolic blood or diastolic blood pressure)");
                        }
                        break;
                    case "Get_BMI":
                        parameterString += ("Your BMI is " + String.valueOf(pro_BMI) + ".");
                        if (pro_BMI >= 24) {
                            parameterString += " You are overweight. Do exercise and control diet, plz";
                        } else if (pro_BMI < 18.5) {
                            parameterString += " You are overweight. pay attention to balanced diet and do exercise";
                        } else parameterString += " Your BMI is normal. Congrats!";
                        break;
                    case "BMI_high_or_low":
                        if (pro_BMI >= 24) {
                            parameterString += " You are overweight. Do exercise and control diet, plz";
                        } else if (pro_BMI < 18.5) {
                            parameterString += " You are overweight. pay attention to balanced diet and do exercise";
                        } else parameterString += " Your BMI is normal. Congrats!";
                        break;
                    case "Get_height":
                        if(pro_height == 0)
                            parameterString += ("Please set the info about height first!!");
                        else
                            parameterString += ("Your height is " + String.valueOf(pro_height) + "cm "+".");
                        break;
                    case "set_height":
                        if(result.getFloatParameter(sys_height)!=0) {
                            parameterString += ("Congrats!! You update the info successfully" + "\n");
                            curProfile.setHeight(result.getFloatParameter(sys_height));
                            myProfileDAO.update(curProfile); // update database
                            pro_height = curProfile.getHeight();
                            parameterString += ("Your height is " + String.valueOf(pro_height) + "cm"+" now");
                        }
                        break;
                    case "Get_weight":
                        if(pro_weight == 0)
                            parameterString += ("Please set the info about weight first!!");
                        else
                            parameterString += ("Your weight is " + String.valueOf(pro_weight) + "kg"+".");
                        break;
                    case "set_weight":
                        if(result.getFloatParameter(sys_weight)!=0) {
                            parameterString += ("Congrats!! You update the info successfully" + "\n");
                            curProfile.setWeight(result.getFloatParameter(sys_weight));
                            myProfileDAO.update(curProfile); // update database
                            pro_weight = curProfile.getWeight();
                            parameterString += ("Your weight is " + String.valueOf(pro_weight) + "kg"+" now");
                        }
                        break;
                    case "choose_food_lunch_include":
                        parameterString += (todayFoods.get(1).toString());
                        break;
                    case "choose_food":  //The case of choosing order (English version)
                        parameterString += eatingOrder(parameterString,result,1);
                        break;
                    case "get_today_food":
                        parameterString += eatingTodayFood(parameterString,result,1);
                        if(food_name.isEmpty() == false) {
                            textMessage.setText("What's my eating order " + food_name);
                            senMessage_test();
                        }
                        break;
                }
            } else {
                switch (result.getAction()) {
                    case "get_today_food":
                        parameterString += eatingTodayFood(parameterString,result,0);
                        if(food_name.isEmpty() == false) {
                            textMessage.setText("我要先吃甚麼 " + food_name);
                            senMessage_test();
                        }
                        break;
                    case "get_pressure_info":
                        if(sys_pre == 0 || dia_pre == 0)
                            parameterString += ("您尚未輸入血壓數值");
                        else {
                            parameterString += "你的收縮壓/舒張壓為 " + String.valueOf(sys_pre) + "(mmHg)"+
                                    "/" + String.valueOf(dia_pre)+"(mmHg)"+"\n";
                             if (sys_pre < 90.0 || dia_pre < 60.0)
                                parameterString += "正常收縮壓/舒張壓為90~140/60~90(mmHg)。您可能有低血壓，請休息幾分鐘再次測量。";
                            else if (sys_pre >= 140.0 || dia_pre >= 90.0)
                                parameterString += "正常收縮壓/舒張壓為90~140/60~90(mmHg)。您可能有高血壓，請休息幾分鐘再次測量。";
                            else parameterString += "您的血壓正常，請繼續保持";
                        }
                        break;
                    case "get_systolic_pressure_info":
                        if(sys_pre == 0)
                            parameterString += ("您尚未輸入血壓數值");
                        else {
                            parameterString += "你的收縮壓為 " + String.valueOf(sys_pre)+ "(mmHg)"+"\n";
                            if (sys_pre < 90.0)
                                parameterString += "正常收縮壓為90~140(mmHg)。您可能有低血壓，請休息幾分鐘再次測量。";
                            else if (sys_pre >= 140.0)
                                parameterString += "正常收縮壓為90~140(mmHg)。您可能有高血壓，請休息幾分鐘再次測量。";
                            else parameterString += "您的收縮壓正常，請繼續保持";
                        }
                        break;
                    case "get_diastolic_pressure_info":
                        if(dia_pre == 0)
                            parameterString += ("您尚未輸入血壓數值");
                        else {
                            parameterString += "你的舒張壓為 " + String.valueOf(dia_pre)+"(mmHg)"+"\n";
                            if (dia_pre < 60.0)
                                parameterString += "正常舒張壓為60~90(mmHg)。您可能有低血壓，請休息幾分鐘再次測量。";
                            else if (dia_pre >= 90.0)
                                parameterString += "正常舒張壓為60~90(mmHg)。您可能有高血壓，請休息幾分鐘再次測量。";
                            else parameterString += "您的舒張壓正常，請繼續保持";
                        }
                        break;
                    case "get_pressure_high_or_low":
                        if(sys_pre == 0 || dia_pre == 0)
                            parameterString += ("您尚未輸入血壓數值");
                        else if (sys_pre < 90.0 || dia_pre < 60.0) parameterString += "您有低血壓，請前往醫院了解詳情";
                        else if (sys_pre >= 140.0 || dia_pre >= 90.0)
                            parameterString += "您有高血壓，請前往醫院了解詳情";
                        else parameterString += "您的血壓正常，請繼續保持";
                        break;
                    case "set_pressure":
                        if(result.getFloatParameter(pressure_num1)!=0 && result.getFloatParameter(pressure_num2)!=0){
                            if(result.getStringParameter(pressure_parameter).equals("收縮壓")) {
                                parameterString += ("恭喜您成功更新血壓數值 " + "\n");
                                curHealth.setSystolicBloodPressure(result.getFloatParameter(pressure_num1));
                                curHealth.setDiastolicBloodPressure(result.getFloatParameter(pressure_num2));
                                //curHealth.setDatetime(calendar.getTimeInMillis());
                                curHealth.setLastModify(new Date().getTime());
                            }
                            else if(result.getStringParameter(pressure_parameter).equals("舒張壓")) {
                                parameterString += ("恭喜您成功更新血壓數值 " + "\n");
                                curHealth.setSystolicBloodPressure(result.getFloatParameter(pressure_num2));
                                curHealth.setDiastolicBloodPressure(result.getFloatParameter(pressure_num1));
                            }
                            else{
                                parameterString += ("請明確說出你想更新收縮壓還是舒張壓");
                            }
                            healthDAO.update(curHealth); //update database
                            sys_pre = curHealth.getSystolicBloodPressure();
                            dia_pre = curHealth.getDiastolicBloodPressure();
                            parameterString += ("您現在的收縮壓為" + String.valueOf(sys_pre) + "(mmHg)"+"\n");
                            parameterString += ("您現在的舒張壓為" + String.valueOf(dia_pre) + "(mmHg)");
                        }
                        else if(result.getFloatParameter(pressure_num1)!=0 && result.getFloatParameter(pressure_num2)==0){
                            if(result.getStringParameter(pressure_parameter).equals("收縮壓")){
                                parameterString += ("恭喜您成功更新血壓數值 " + "\n");
                                curHealth.setSystolicBloodPressure(result.getFloatParameter(pressure_num1));
                                sys_pre = curHealth.getSystolicBloodPressure();
                                parameterString += ("您現在的收縮壓為" + String.valueOf(sys_pre) + "(mmHg)");
                                healthDAO.update(curHealth);
                            }else if(result.getStringParameter(pressure_parameter).equals("舒張壓")){
                                parameterString += ("恭喜您成功更新血壓數值 " + "\n");
                                curHealth.setDiastolicBloodPressure(result.getFloatParameter(pressure_num1));
                                dia_pre = curHealth.getDiastolicBloodPressure();
                                parameterString += ("您現在的舒張壓為" + String.valueOf(dia_pre) + "(mmHg)");
                                healthDAO.update(curHealth);
                            }else{
                                parameterString += ("請明確說出你想更新收縮壓還是舒張壓");
                            }
                        }
                        else{
                            parameterString +=("麻煩您輸入血壓數值，謝謝");
                        }
                        break;
                    case "get_pulse":
                        if(pulse == 0)
                            parameterString += ("您尚未輸入您的脈搏");
                        else
                            parameterString += ("您的脈搏為" +pulse);
                        break;
                    case "get_bmi_info":
                        if(pro_height == 0 || pro_weight == 0 )
                            parameterString += ("您尚未輸入身高、體重相關數值");
                        else {
                            parameterString += ("您的BMI為" + String.valueOf(pro_BMI));
                            if (pro_BMI >= 24) {
                                parameterString += " 正常範圍是18.5~24。您過重了，請適量運動並控制飲食，並定期檢查BMI";
                            } else if (pro_BMI < 18.5) {
                                parameterString += " 正常範圍是18.5~24。您過輕了 均衡飲食有助身體健康";
                            } else parameterString += " BMI正常，請繼續保持";
                        }
                        break;
                    case "get_height_info":
                        if(pro_height == 0)
                            parameterString += ("您尚未輸入身高數值");
                        else
                            parameterString += ("您的身高為" + String.valueOf(pro_height)+"cm");
                        break;
                    case "set_height":
                        if(result.getFloatParameter(sys_height)!=0) {
                            parameterString += ("恭喜您成功更新身高數值 " + "\n");
                            curProfile.setHeight(result.getFloatParameter(sys_height));
                            myProfileDAO.update(curProfile); // update database
                            pro_height = curProfile.getHeight();
                            parameterString += ("您現在的身高為" + String.valueOf(pro_height) + "cm");
                        }
                        break;
                    case "get_weight_info":
                        if(pro_weight == 0)
                            parameterString += ("您尚未輸入體重數值");
                        else
                            parameterString += ("您的體重為" + String.valueOf(pro_weight)+"kg");
                        break;
                    case "set_weight":
                        if(result.getFloatParameter(sys_weight)!=0) {
                            parameterString += ("恭喜您成功更新體重數值 " + "\n");
                            curProfile.setWeight(result.getFloatParameter(sys_weight));
                            myProfileDAO.update(curProfile); // update database
                            pro_weight = curProfile.getWeight();
                            parameterString += ("您現在的體重為" + String.valueOf(pro_weight) + "kg");
                        }
                        break;
                    case "get_bmi_high_or_low":
                        if (pro_BMI >= 24) {
                            parameterString += " 您的體重過高，請多運動並控制飲食，並定期檢查BMI";
                        } else if (pro_BMI < 18.5) {
                            parameterString += " 您的體重太輕了 均衡飲食有助身體健康";
                        } else parameterString += " 您的BMI正常，請繼續保持";
                        break;
                    case "choose_food_action":  //The case of choosing order
                        parameterString += eatingOrder(parameterString,result,0);
                        break;
                    case "get_absorb_calorie":
                        int i;
                        //get today's total calories
                        for( i=0;i<todayFoods.size();i++) {
                            total_absorb_calories += todayFoods.get(i).getCalorie();
                        }
                        idel_absorb_cal = sharedPreferences.getFloat("absorb",0);

                        if(idel_absorb_cal == 0 || total_absorb_calories == 0){
                            if(idel_absorb_cal == 0){
                                parameterString += "請前往“設定”填寫您的減重目標！";
                            }
                            else{
                                parameterString += "您今天尚未吃任何食物！";
                            }
                        }
                        else {
                            if (total_absorb_calories < idel_absorb_cal) {
                                parameterString += "您今天吸收的熱量為" + total_absorb_calories + "大卡\n";
                                float carolie_need = idel_absorb_cal - total_absorb_calories;
                                parameterString += "您距離每日理想熱量還有" + carolie_need + "" + "大卡\n";
                                parameterString += "建議補足每日需求熱量";

                            } else if (total_absorb_calories > idel_absorb_cal) {
                                parameterString += "您今天吸收的熱量為" + total_absorb_calories + "大卡\n";
                                parameterString += "您超過每日理想需求熱量" + ((total_absorb_calories - (double) idel_absorb_cal) + "")
                                        + "大卡\n";
                                parameterString += "建議多運動或減少每日進食量";
                            } else {
                                parameterString += "您今天攝取的熱量已足夠！ 建議多休息";
                            }
                        }
                        break;
                    case "get_consume_calorie":
                        //get today's total calories
                        for(i=0;i<todaySports.size();i++){
                            total_consume_calories += todaySports.get(i).getCalorie();
                        }

                        idel_consume_cal = sharedPreferences.getFloat("consume",0);

                        if(idel_consume_cal == 0 || total_consume_calories == 0){
                            if(idel_consume_cal == 0){
                                parameterString += "請前往“設定”填寫您的減重目標！";
                            }
                            else{
                                parameterString += "您今天尚未做任何運動！";
                            }
                        }
                        else{
                            if(total_consume_calories > idel_consume_cal){
                                parameterString += "您今天消耗" + total_consume_calories + "大卡\n";
                                parameterString += "建議補充膳食纖維高的食品以及多休息";
                            }
                            else if(total_consume_calories < idel_consume_cal){
                                parameterString += "您今天消耗" + total_consume_calories + "大卡\n";
                                parameterString += "距離每日理想消耗熱量還有" + (idel_consume_cal-total_consume_calories)
                                        + "大卡\n";
                                parameterString += "建議多運動以保持理想熱量消耗量";
                            }
                            else{
                                parameterString += "您今天達成每日理想每日消耗熱量！ 請繼續保持";
                            }
                        }
                        break;
                    case "get_tem":
                        parameterString += ("您的體溫為"+tem);
                        break;
                    case "get_water":
                        parameterString += ("您喝了"+water_drunk);
                    break;
                    case "declare_disease":
                        if(result.getStringParameter(disease).equals("心臟病")){
                            curHealth.setHeartDiseasePos();
                            heart_disease = true;
                            //healthDAO.update(curHealth);
                            //parameterString += ("\nDebug "+curHealth.getHeartDisease());
                        }
                        else if(result.getStringParameter(disease).equals("糖尿病")){
                            curHealth.setDiabetesDiseasePos();
                            diabetes_disease = true;
                        }
                        Toast.makeText(ChatBotActivity.this, "系統已成功更新資料", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

        todayFoods.clear();
        todaySports.clear();
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
            Intent intent_main = new Intent();
            intent_main.setClass(ChatBotActivity.this, MainActivity.class);
            startActivity(intent_main);
            finish();
            //Toast.makeText(this, "Open food list", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.Import) {
            selectImage();
            //Toast.makeText(this, "Import food", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.chat) {
            Intent intent_chat_bot = new Intent();
            intent_chat_bot.setClass(ChatBotActivity.this, ChatBotActivity.class);
            startActivity(intent_chat_bot);
            finish();
        } else if (id == R.id.new_calendar){
            Intent intent_new_calendar = new Intent();
            intent_new_calendar.setClass(ChatBotActivity.this, NewCalendarActivity.class);
            startActivity(intent_new_calendar);
            finish();
        } else if (id == R.id.blood_pressure){
            Intent intent_blood_pressure = new Intent();
            intent_blood_pressure.setClass(ChatBotActivity.this, MyBloodPressure.class);
            startActivity(intent_blood_pressure);
            finish();
        } else if (id == R.id.temp_record){
            Intent intent_temp_record = new Intent();
            intent_temp_record.setClass(ChatBotActivity.this, MyTemperatureRecord.class);
            startActivity(intent_temp_record);
            finish();
        } else if (id == R.id.water_record){
            Intent intent_water_record = new Intent();
            intent_water_record.setClass(ChatBotActivity.this, DrinkWaterDiary.class);
            startActivity(intent_water_record);
            finish();
        } else if (id == R.id.message) {
            Intent intent_message = new Intent();
            intent_message.setClass(ChatBotActivity.this, MessageActivity.class);
            startActivity(intent_message);
            finish();
            //Toast.makeText(this, "Send message", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.mail){
            Intent intent_mail = new Intent();
            intent_mail.setClass(ChatBotActivity.this, MailActivity.class);
            startActivity(intent_mail);
            finish();
        } else if (id == R.id.setting_list) {
            Intent intent_setting = new Intent();
            intent_setting.setClass(ChatBotActivity.this, SettingsActivity.class);
            startActivity(intent_setting);
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
        btnRecord.setImageResource(R.drawable.ic_mic_black_48dp);
    }

    @Override
    public void onListeningFinished() {
        btnRecord.setImageResource(R.drawable.ic_mic_black_48dp);
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
                        Intent result = new Intent();
                        result.putExtra(FROM_CAMERA, SCAN_FOOD);
                        result.setClass(activity, MainActivity.class);
                        activity.startActivity(result);
                        activity.finish();
                    }
                } else if (items[index].equals("從相簿中選取")) {
                    if (activityIndex == ChATBOT_ACTIVITY) {
                        Intent result = new Intent();
                        result.putExtra(FROM_GALLERY, TAKE_PHOTO);
                        result.setClass(activity, MainActivity.class);
                        activity.startActivity(result);
                        activity.finish();
                    }
                } else if (items[index].equals("取消")) {
                    dialog.dismiss();

                }
            }
        });
        builder.show();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        //savedInstanceState.putString();
        super.onSaveInstanceState(savedInstanceState);

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
        MenuItem item_sound = findViewById(R.id.action_settings);

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) { //sound open or not
            if(sound_open == false) {
                sound_open = true;
                if(flag == 0) {
                    Toast.makeText(ChatBotActivity.this, "開啟聲音", Toast.LENGTH_SHORT).show();
                    item.setTitle("關閉聲音");
                }
                else {
                    Toast.makeText(ChatBotActivity.this, "Turn on the sound", Toast.LENGTH_SHORT).show();
                    item.setTitle("Turn off the sound");
                }
            }
            else{
                sound_open = false;
                if(flag == 0) {
                    Toast.makeText(ChatBotActivity.this, "關閉聲音", Toast.LENGTH_SHORT).show();
                    item.setTitle("開啟聲音");
                }
                else {
                    Toast.makeText(ChatBotActivity.this, "Turn off the sound", Toast.LENGTH_SHORT).show();
                    item.setTitle("Turn on the sound");
                }
            }
        }
        if (id == R.id.language_change){ // change language
            if(flag == 0){
                setTitle("CHAT BOT");
                config = new AIConfiguration("3f5da70a97c44731b8d7ac44b6acb7ef",
                        AIConfiguration.SupportedLanguages.English,
                        AIConfiguration.RecognitionEngine.System);
                aiService = AIService.getService(this, config);
                aiService.setListener(this);
                flag = 1;
                //Instruction (chatbot使用說明 => 讓user清楚知道目前聊天機器人有甚麼功能)
                Message eng_message = new Message();
                eng_message.setId("4");
                eng_message.setMessage("Welcome "+LoginActivity.facebookName + "\n"+
                        "Now chatbot can support\n" +
                        "1.Ask/Set info about height、weight、bmi\n" +
                        "2.Ask info about eating order\n" +
                        "3.Ask/Set info about pressure\n"+
                        "4.Ask info about calories\n"+
                        "5.Check the food you eat today");
                messageArrayList.clear();
                messageArrayList.add(eng_message);
                mAdapter.notifyDataSetChanged();
                text_to_audio.setLanguage(Locale.UK);
                Toast.makeText(ChatBotActivity.this, "English Chat Bot", Toast.LENGTH_SHORT).show();

            }
            else{
                setTitle("健康管家");
                config = new AIConfiguration("a772958d63a149b39bf9f11cfad29889",
                        AIConfiguration.SupportedLanguages.ChineseTaiwan,
                        AIConfiguration.RecognitionEngine.System);
                aiService = AIService.getService(this, config);
                aiService.setListener(this);
                flag = 0;
                //Instruction (chatbot使用說明 => 讓user清楚知道目前聊天機器人有甚麼功能)
                Message ini_message = new Message();
                ini_message.setId("3");
                ini_message.setMessage("您好"+LoginActivity.facebookName +"\n"+
                        "本聊天機器人目前支援功能有\n" +
                        "1.詢問/設定 身高、體重、bmi\n" +
                        "2.詢問飲食順序\n" +
                        "3.詢問/設定 血壓、脈搏狀況\n"+
                        "4.查詢目前消耗/吸收熱量\n"+
                        "5.查詢今日所吃的食物");
                messageArrayList.clear();
                messageArrayList.add(ini_message);
                mAdapter.notifyDataSetChanged();
                text_to_audio.setLanguage(Locale.TAIWAN);
                Toast.makeText(ChatBotActivity.this, "中文管家", Toast.LENGTH_SHORT).show();
            }
        }


        return super.onOptionsItemSelected(item);

    }

    //Here is the function to deal with today's eating food
    /*Function Description:  parameterString: The string parameter that chatbot answers
                                               result : api.ai's result
                                               label : 0 denotes Chinese version, 1 denotes English version
         */

    private String eatingTodayFood(String parameterString,Result result,int label){

        float sys_pre = curHealth.getSystolicBloodPressure();
        float dia_pre = curHealth.getDiastolicBloodPressure();
        int i,j,k;
        int total_calorie=0;
        String [] cato = new String[20];
        boolean [] cato_bool = new boolean[20];
        for(i=0;i<10;i++){
            cato_bool[i] = false;
        }
        cato[0] = "穀物類";cato[1] = "澱粉類";cato[2] = "堅果及種子類";
        cato[3] = "水果類";cato[4] = "蔬菜類";cato[5] = "藻類";cato[6] = "菇類";
        cato[7] = "豆類";cato[8] = "肉類";cato[9] = "魚貝類";cato[10] = "蛋類";
        cato[11] = "乳品類";cato[12] = "油脂類";cato[13] = "糖類"; cato[14] = "嗜好性飲料類";
        cato[15] = "調味料及香辛料類"; cato[16] ="糕餅點心類"; cato[17] = "加工調理食品類";

        if(todayFoods.isEmpty() == true) {
            if(label == 0)
                parameterString += ("您今日尚未攝取食物");
            else
                parameterString += ("You haven't eaten any food today. ");
        }
        else {
            if(label == 0)
                parameterString += ("您今天吃了");
            else
                parameterString += ("You've eaten");

            food_name = new String("");
            for (i = 0; i < todayFoods.size(); i++) {
                if (i != 0)
                    parameterString += ("、");
                food_name += (" "+todayFoods.get(i).getTitle());
                parameterString += (todayFoods.get(i).getTitle());
                parameterString += (todayFoods.get(i).getGrams());
                parameterString += ("g");
                total_calorie += (todayFoods.get(i).getCalorie());
                if(todayFoods.get(i).getTitle().contains("(") == true) {//把字串去括號 => 方便蒐尋食物類別
                    String token_new = new String("");
                    String title = todayFoods.get(i).getTitle();
                    for(j=0;j<title.length();j++) {
                        if(title.charAt(j) == '(')
                            break;
                    }
                    token_new = title.substring(0,j);
                    todayFoods.get(i).setTitle(token_new);
                }
                for (j = 0; j < foodCalList.size(); j++) {
                    if (foodCalList.get(j).getChineseName().contains(todayFoods.get(i).getTitle()) ) {
                        for (k = 0; k < cato_bool.length; k++) {
                            if (foodCalList.get(j).getCategory().equals(cato[k])) {
                                //parameterString += ("它為" + cato[k] + "類食物\n"); //Only for debugging
                                cato_bool[k] = true;
                            }
                        }
                        break;
                    }
                }
            }
        }

        if(label == 0) { // Chinese Version
            parameterString += ("\n您今天一共吃了" + total_calorie + "大卡" + "\n");
            parameterString += ("離每日所需熱量尚有" + Integer.toString(2200 - total_calorie) + "大卡" + "\n");

            parameterString += ("您今天尚未攝取");
            if (cato_bool[0] == false && cato_bool[1] == false && cato_bool[2] == false)
                parameterString += ("五穀根莖類食物\n");
            if (cato_bool[3] == false && cato_bool[4] == false && cato_bool[5] == false && cato_bool[6] == false) {
                parameterString += ("蔬果類食物");
                if (diabetes_disease == true) {
                    parameterString += ("(建議您可以吃洋蔥、苦瓜等食物，這些食物功能類似於胰島素)");
                }
                if (heart_disease == true) {
                    parameterString += ("(建議您可以多吃芹菜，芹菜富含豐芹菜鹼，具有保護心血管的功能)");
                }
                if (sys_pre > 140 || dia_pre > 90) { // High blood pressure
                    parameterString += ("(建議您多吃芹菜、木耳、洋蔥等蔬菜類食物，這些食物有利於降低血壓)");
                }
                parameterString += ("\n");
            }
            if (cato_bool[7] == false && cato_bool[8] == false && cato_bool[9] == false && cato_bool[10] == false) {
                parameterString += ("蛋豆魚肉類食物");
                if (diabetes_disease == true) {
                    parameterString += ("(建議您可以吃鱔魚，該食物功能類似於胰島素)");
                }
                if (sys_pre > 140 || dia_pre > 90) {
                    parameterString += ("(建議您以魚類代替肉類的攝取)");
                }
            }

            if (diabetes_disease == true) {
                parameterString += ("\n！！提醒您每餐須正常時間進食，少量多餐有助於控制穩定的血壓");
            }
            if (heart_disease == true) {

                parameterString += ("\n！！提醒您少吃肉類食物");
            }
        }

        //parameterString += ("Debugging: "+food_name);
        return parameterString;
    }

    //Here is the function to deal with eating order
    /*Function Description:  parameterString: The string parameter that chatbot answers
                                               result : api.ai's result
                                               label : 0 denotes Chinese version, 1 denotes English version
         */
    private String eatingOrder(String parameterString,Result result,int label){

        int number = 0;
        int i;
        float sys_pre = curHealth.getSystolicBloodPressure();
        float dia_pre = curHealth.getDiastolicBloodPressure();

        if(result.getStringParameter(J_Food).isEmpty() == false){ //priority 1
            if(label == 0)
                parameterString+=("建議您先吃");
            else
                parameterString+=("You should eat ");

            parameterString += (result.getStringParameter(J_Food));
            number =1;

            if (result.getStringParameter(J_Food1).isEmpty() == false) {
                parameterString +=(", ");
                parameterString += (result.getStringParameter(J_Food1));
            }
            if (result.getStringParameter(J_Food2).isEmpty() == false) {
                parameterString +=(", ");
                parameterString += (result.getStringParameter(J_Food2));
            }

            /*Special Case => High Blood Pressure*/
            if(label == 0) {
                if (result.getStringParameter(J_Food).contains("人參") && (sys_pre >= 140 || dia_pre >= 90)) {
                    parameterString += ("\n(提醒您，您有高血壓，應減少人參的攝取)");
                }

                if (heart_disease == true) {
                    parameterString += ("\n建議多攝取纖維素，減少膽固醇生成，對心臟的健康有所助益");
                }
            }

            else{
                parameterString+=(" first.");
            }

        }
        if(result.getStringParameter(D_Food).isEmpty() == false || result.getStringParameter(E_Food).isEmpty() == false || result.getStringParameter(G_Food).isEmpty() == false || result.getStringParameter(F_Food).isEmpty() == false) { //priority 2

            if (label == 0){
                if (number == 1) {
                    // deal with "drink"
                    if (result.getStringParameter(D_Food).isEmpty() == true && result.getStringParameter(E_Food).isEmpty() == true && result.getStringParameter(G_Food).isEmpty() == false) {
                        if (result.getStringParameter(G_Food).contains("乳") || result.getStringParameter(G_Food).contains("奶") || result.getStringParameter(G_Food).contains("阿華田"))
                            parameterString += ("\n接著再喝");
                    } else {
                        parameterString += ("\n接著再吃");
                    }
                } else {
                    number = 1; //set flag
                    parameterString += ("建議您先享用");
                }
            }else{
                if(number == 1)
                    parameterString += ("\nand then we recommend you eat ");
                else {
                    parameterString+=("You should eat ");
                }
            }

            if (result.getStringParameter(D_Food).isEmpty() == false)
                parameterString += (result.getStringParameter(D_Food));

            if (result.getStringParameter(D_Food1).isEmpty() == false) {
                parameterString +=(", ");
                parameterString += (result.getStringParameter(D_Food1));
            }
            if (result.getStringParameter(D_Food2).isEmpty() == false) {
                parameterString +=(", ");
                parameterString += (result.getStringParameter(D_Food2));
            }

            /*Special Case => High Blood Pressure*/
            if(label == 0) { // Chinese Version
                if (result.getStringParameter(D_Food).contains("雞") && (sys_pre >= 140 || dia_pre >= 90)) {
                    parameterString += ("\n(提醒您，您有高血壓，應減少雞肉的攝取)");
                }
            }

            if (result.getStringParameter(E_Food).isEmpty() == false) {
                if(result.getStringParameter(D_Food).isEmpty() == false)
                    parameterString +=(", ");
                parameterString += (result.getStringParameter(E_Food));
            }
            if (result.getStringParameter(E_Food1).isEmpty() == false) {
                parameterString +=(", ");
                parameterString += (result.getStringParameter(E_Food1));
            }
            if (result.getStringParameter(E_Food2).isEmpty() == false) {
                parameterString +=(", ");
                parameterString += (result.getStringParameter(E_Food2));
            }

            /*Special Case => High Blood Pressure*/
            if(label == 0) {
                if ((result.getStringParameter(E_Food).contains("髓") || result.getStringParameter(E_Food).contains("豬肝") || result.getStringParameter(E_Food).contains("豬腰")) && (sys_pre >= 140 || dia_pre >= 90)) {
                    parameterString += ("\n(提醒您，您有高血壓，應減少" + result.getStringParameter(E_Food) + "的攝取)");
                }
            }

            if (result.getStringParameter(G_Food).isEmpty() == false) {

                if(label == 0) { //Chinese Version
                    if (result.getStringParameter(D_Food).isEmpty() == false || result.getStringParameter(E_Food).isEmpty() == false) {
                        if (result.getStringParameter(G_Food).contains("乳") || result.getStringParameter(G_Food).contains("奶") || result.getStringParameter(G_Food).contains("阿華田"))
                            parameterString += (",喝");
                        else
                            parameterString += (",");
                    }
                }
                else{ //English Version
                    if (result.getStringParameter(D_Food).isEmpty() == false || result.getStringParameter(E_Food).isEmpty() == false) {
                        parameterString += (",");
                    }
                }

                parameterString += (result.getStringParameter(G_Food));
            }

            if (result.getStringParameter(G_Food1).isEmpty() == false) {
                parameterString +=(", ");
                parameterString += (result.getStringParameter(G_Food1));
            }
            if (result.getStringParameter(G_Food2).isEmpty() == false) {
                parameterString +=(", ");
                parameterString += (result.getStringParameter(G_Food2));
            }

            if (result.getStringParameter(F_Food).isEmpty() == false) {
                if(result.getStringParameter(D_Food).isEmpty() == false || result.getStringParameter(E_Food).isEmpty() == false || result.getStringParameter(G_Food).isEmpty() == false)
                    parameterString +=(",");
                parameterString += (result.getStringParameter(F_Food));
            }

            if (result.getStringParameter(F_Food1).isEmpty() == false) {
                parameterString +=(",");
                parameterString += (result.getStringParameter(F_Food1));
            }

            /*Special Disease */
            if(label == 0) {
                if (heart_disease == true) {
                    parameterString += ("\n(但不建議您攝取肉類食物)");
                }
            }

            if(label == 1){
                if(number == 0) {
                    parameterString +=(" first.");
                    number = 1;
                }
            }
        }
        if(result.getStringParameter(B_Food).isEmpty() == false) { //priority 3
            if(label == 0) { //Chinese Version
                if (number == 1)
                    parameterString += ("\n接著再吃");
                else {
                    number = 1; //set flag
                    parameterString += ("建議您先享用");
                }
            }else{ //English Version
                if(number == 1)
                    parameterString += ("\nand then we recommend you eat ");
                else {
                    parameterString+=("You should eat ");
                }
            }
            parameterString += ( result.getStringParameter(B_Food));

            if (result.getStringParameter(B_Food1).isEmpty() == false) {
                parameterString +=(", ");
                parameterString += (result.getStringParameter(B_Food1));
            }
            if (result.getStringParameter(B_Food2).isEmpty() == false) {
                parameterString +=(", ");
                parameterString += (result.getStringParameter(B_Food2));
            }

            if(label == 1){ // English Version
                if(number == 0) {
                    parameterString +=(" first.");
                    number = 1;
                }
            }
        }
        if(result.getStringParameter(A_Food).isEmpty() == false || result.getStringParameter(O_Food).isEmpty() == false) { //priority 4
            if(label == 0) { //Chinese Version
                if (number == 1)
                    parameterString += ("\n接著再吃");
                else {
                    number = 1; //set flag
                    parameterString += ("建議您先吃");
                }
            }else{ //English Version
                if(number == 1)
                    parameterString += ("\nand then we recommend you eat ");
                else {
                    parameterString+=("You should eat ");
                }
            }
            if(result.getStringParameter(A_Food).isEmpty() == false)
                parameterString += (result.getStringParameter(A_Food));

            if (result.getStringParameter(A_Food1).isEmpty() == false) {
                parameterString +=(", ");
                parameterString += (result.getStringParameter(A_Food1));
            }
            if (result.getStringParameter(A_Food2).isEmpty() == false) {
                parameterString +=(", ");
                parameterString += (result.getStringParameter(A_Food2));
            }

            if(label == 0) { //Chinese Version => Special Disease
                if (result.getStringParameter(A_Food).isEmpty() == false && diabetes_disease == true) { //Special Case (diabetes disease)
                    parameterString += ("\n(請小心控制碳水化合物的攝取，過量的碳水化合物可能導致血糖過高)");
                }
            }

            if(result.getStringParameter(O_Food).isEmpty() == false) {
                if(result.getStringParameter(A_Food).isEmpty() == false)
                    parameterString += (", ");
                parameterString += (result.getStringParameter(O_Food));
            }

            if (result.getStringParameter(O_Food1).isEmpty() == false) {
                parameterString +=(", ");
                parameterString += (result.getStringParameter(O_Food1));
            }
            if (result.getStringParameter(O_Food2).isEmpty() == false) {
                parameterString +=(", ");
                parameterString += (result.getStringParameter(O_Food2));
            }
            if (result.getStringParameter(O_Food3).isEmpty() == false) {
                parameterString +=(", ");
                parameterString += (result.getStringParameter(O_Food3));
            }

            if(label == 1){ // English Version
                if(number == 0) {
                    parameterString +=(" first.");
                    number = 1;
                }
            }
        }
        if(result.getStringParameter(I_Food).isEmpty() == false) { //priority 5
            if(label == 0) { // Chinese Version
                if (number == 1)
                    parameterString += ("\n接著再吃");
                else {
                    number = 1; //set flag
                    parameterString += ("建議您先吃");
                }
            }else{ //English Version
                if(number == 1)
                    parameterString += ("\nand then we recommend you eat ");
                else {
                    parameterString+=("You should eat ");
                }
            }
            parameterString += ( result.getStringParameter(I_Food));

            if (result.getStringParameter(I_Food1).isEmpty() == false) {
                parameterString +=(", ");
                parameterString += (result.getStringParameter(I_Food1));
            }
            if (result.getStringParameter(I_Food2).isEmpty() == false) {
                parameterString +=(", ");
                parameterString += (result.getStringParameter(I_Food2));
            }

            if(label == 1){ // English Version
                if(number == 0) {
                    parameterString +=(" first.");
                    number = 1;
                }
            }
        }
        if(result.getStringParameter(L_Food).isEmpty() == false || result.getStringParameter(K_Food).isEmpty() == false) { //priority 6
            if(label == 0) {
                if (number == 1) {
                    if (result.getStringParameter(L_Food).isEmpty() == false)
                        parameterString += ("\n接著再喝");
                    else
                        parameterString += ("\n接著再吃");
                } else {
                    parameterString += ("建議您先享用");
                }
            }else{
                if(number == 1)
                    parameterString += ("\nand then we recommend you eat ");
                else {
                    parameterString+=("You should eat ");
                }
            }
            if (result.getStringParameter(L_Food).isEmpty() == false)
                parameterString += (result.getStringParameter(L_Food));

            if (result.getStringParameter(L_Food1).isEmpty() == false) {
                parameterString += (", ");
                parameterString += (result.getStringParameter(L_Food1));
            }
            if (result.getStringParameter(L_Food2).isEmpty() == false) {
                parameterString += (", ");
                parameterString += (result.getStringParameter(L_Food2));
            }

            if (result.getStringParameter(K_Food).isEmpty() == false) {

                if (result.getStringParameter(L_Food).isEmpty() == false) {
                    if(label == 0)
                        parameterString += (",吃");
                    else{
                        parameterString += (",");
                    }
                }

                parameterString += (result.getStringParameter(K_Food));
            }

            if (result.getStringParameter(K_Food1).isEmpty() == false) {
                parameterString += (", ");
                parameterString += (result.getStringParameter(K_Food1));
            }
            if (result.getStringParameter(K_Food2).isEmpty() == false) {
                parameterString += (", ");
                parameterString += (result.getStringParameter(K_Food2));
            }

            if(label == 0) {
                if (result.getStringParameter(K_Food).isEmpty() == false && diabetes_disease == true) { //Special Case(diabetes disease)
                    parameterString += ("(不建議您攝取此類食物)");
                }
            }

            if(label == 1){ // English Version
                if(number == 0)
                    parameterString +=(" first.");
            }
        }
        return  parameterString;

    }

}
