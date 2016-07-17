package com.example.nthucs.prototype.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.example.nthucs.prototype.MessageList.Commit;
import com.example.nthucs.prototype.MessageList.MessageAdapter;
import com.example.nthucs.prototype.R;
import com.example.nthucs.prototype.TabsBar.ViewPagerAdapter;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by admin on 2016/7/16.
 */
public class MessageActivity extends AppCompatActivity {

    private CallbackManager mCallbackManager;
    private ProfileTracker mProfileTracker;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Menu menu;
    private AccessToken accessToken;
    public ArrayList<Commit> arrayOfCommit;
    public ListView listView;
    String httpUrl;
    static Bitmap img;
    // action number for every activity
    private static final int SCAN_FOOD = 2;
    private static final int TAKE_PHOTO = 3;
    private static final int CALENDAR = 4;
    private static final int SETTINGS = 5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        FacebookSdk.sdkInitialize(getApplicationContext());
        initFBManager();

        //arrayOfCommit = new ArrayList<Commit>();
        // Create the adapter to convert the array to views
        //MessageAdapter adapter = new MessageAdapter(this, arrayOfCommit);
        // Attach the adapter to a ListView
        listView = (ListView) findViewById(R.id.messageList);
        //listView.setAdapter(adapter);

        processTabLayout();
        selectTab(5);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.message_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(mCallbackManager.onActivityResult(requestCode, resultCode, data)) {
            return;
        }
    }



    // Initialize tab layout and listener
    private void processTabLayout() {

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

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

                        // temporary added for return food list
                        if (tab.getPosition() == 0) {
                            Intent result = getIntent();

                            setResult(Activity.RESULT_OK, result);
                            finish();
                        } else if (tab.getPosition() == 1) {
                            Intent intent_gallery = new Intent("com.example.nthucs.prototype.TAKE_PHOTO");
                            startActivityForResult(intent_gallery, TAKE_PHOTO);
                        } else if (tab.getPosition() == 2) {
                            Intent intent_camera = new Intent("com.example.nthucs.prototype.TAKE_PICT");
                            startActivityForResult(intent_camera, SCAN_FOOD);
                        } else if (tab.getPosition() == 3) {
                            Intent intent_calendar = new Intent("com.example.nthucs.prototype.CALENDAR");
                            startActivityForResult(intent_calendar, CALENDAR);
                        } else if (tab.getPosition() == 4) {
                            Intent intent_settings = new Intent("com.example.nthucs.prototype.SETTINGS");
                            startActivityForResult(intent_settings, SETTINGS);
                        } else if (tab.getPosition() == 5) {
                        }
                        //System.out.println(tab.getPosition());
                    }
                }
        );
    }

    // select specific tab
    private void selectTab(int index) {
        TabLayout.Tab tab = tabLayout.getTabAt(index);
        tab.select();
    }

    //init facebook manager
    private void initFBManager(){
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d("Success", "Login");
                        //check whether user logged
                        accessToken = loginResult.getAccessToken();
                        if(isLoggedIn()){
                            if(Profile.getCurrentProfile()==null){
                                mProfileTracker = new ProfileTracker() {
                                    @Override
                                    protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                                        // profile2 is the new profile
                                        mProfileTracker.stopTracking();
                                        updateMenuTitles(profile2.getName());
                                    }
                                };
                            }
                            updateMenuTitles(Profile.getCurrentProfile().getName());
                            queryGraphAPI();
                        }
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(getApplicationContext(), "Login Cancel", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void FBLogin(MenuItem menuItem){
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends","user_posts"));
    }

    private boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    private void updateMenuTitles(String name) {
        MenuItem loginMenuItem = menu.findItem(R.id.login);
        loginMenuItem.setTitle(name);
    }

    private void queryGraphAPI(){
        GraphRequest request = GraphRequest.newGraphPathRequest(
                accessToken,
                "/me/feed",
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        // Insert your code here
                        arrayOfCommit = new ArrayList<>();
                        arrayOfCommit = handleJSON(response.getJSONObject());
                        // Create the adapter to convert the array to views
                        MessageAdapter adapter = new MessageAdapter(getApplicationContext(), arrayOfCommit);
                        listView.setAdapter(adapter);
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "application,comments,picture");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private ArrayList handleJSON(JSONObject jsonObject){
        ArrayList<Commit> data = new ArrayList<>();
        try{
            JSONArray responseData = jsonObject.getJSONArray("data");
            for(int i=0;i<responseData.length();i++){
                JSONObject jsonTmp = responseData.getJSONObject(i);
                if(jsonTmp.optJSONObject("application")!=null){
                    if(jsonTmp.optJSONObject("application").getString("name").equals("Prototype")){
                        //posted img
                        httpUrl = jsonTmp.getString("picture");
                        System.out.println("UUUUUUUUUUUUUUUUUUUUUUUUUUUUUU "+httpUrl);
                        Thread thread = new Thread(mutiThread);
                        thread.start();
                        try {
                            thread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //person 's comments
                        JSONArray comments = jsonTmp.getJSONObject("comments").getJSONArray("data");
                        int lastIndex = comments.length()-1;
                        //person's name
                        String personName = comments.getJSONObject(lastIndex).getJSONObject("from").getString("name");
                        //person's comment
                        String personComment = comments.getJSONObject(lastIndex).getString("message");
                        Commit commit = new Commit(img,personName,personComment);
                        System.out.println("YOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO "+img);
                        data.add(commit);
                    }
                }
            }
        }
        catch (JSONException e){
            System.out.println("JSONEXCEPTION "+e.toString());
            e.printStackTrace();
        }
        return data;
    }


    public static void getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            /*HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);*/
            img = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            //img =  myBitmap;
        } catch (IOException e) {
            // Log exception
            System.out.println(e.toString());
        }
    }

    private Runnable mutiThread = new Runnable(){
        public void run(){
            // 運行網路連線的程式
            getBitmapFromURL(httpUrl);
        }
    };
}
