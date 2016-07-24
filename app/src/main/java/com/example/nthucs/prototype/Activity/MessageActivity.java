package com.example.nthucs.prototype.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.nthucs.prototype.MessageList.Commit;
import com.example.nthucs.prototype.MessageList.MessageAdapter;
import com.example.nthucs.prototype.R;
import com.example.nthucs.prototype.TabsBar.TabsController;
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
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by admin on 2016/7/16.
 */
public class MessageActivity extends AppCompatActivity {

    private CallbackManager mCallbackManager;
    private ProfileTracker mProfileTracker;
    private Menu menu;
    private AccessToken accessToken;
    public ArrayList<Commit> arrayOfCommit;
    public ListView listView;

    String httpUrl,postID;
    static Bitmap img;

    // element for the bottom of the tab content
    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Message");
        setContentView(R.layout.activity_message);

        Toast.makeText(getApplicationContext(), "Login to show messages", Toast.LENGTH_SHORT).show();
        FacebookSdk.sdkInitialize(getApplicationContext());
        initFBManager();

        //arrayOfCommit = new ArrayList<Commit>();
        // Create the adapter to convert the array to views
        //MessageAdapter adapter = new MessageAdapter(this, arrayOfCommit);
        // Attach the adapter to a ListView
        listView = (ListView) findViewById(R.id.messageList);
        //listView.setAdapter(adapter);


        // initialize tabLayout and viewPager
        viewPager = (ViewPager)findViewById(R.id.viewPager);
        tabLayout = (TabLayout)findViewById(R.id.tabLayout);
        initializeTabLayout();

        // call function to active tabs listener
        TabsController tabsController = new TabsController(3, MessageActivity.this, tabLayout, viewPager);
        tabsController.processTabLayout();

        selectTab(3);
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
                        System.out.println(response.getJSONObject());
                        arrayOfCommit = handleJSON(response.getJSONObject());
                        // Create the adapter to convert the array to views
                        MessageAdapter adapter = new MessageAdapter(getApplicationContext(), arrayOfCommit);
                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                System.out.println("arraylist "+arrayOfCommit.get(position).getPostID());
                                postID = arrayOfCommit.get(position).getPostID();
                                Intent intent_chat = new Intent(getApplicationContext(), ChatRoomActivity.class);
                                intent_chat.putExtra("postID",postID);
                                startActivity(intent_chat);
                            }
                        });
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
                        Thread thread = new Thread(mutiThread);
                        thread.start();
                        try {
                            thread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if(jsonTmp.optJSONObject("comments")!=null) {
                            //person 's comments
                            JSONArray comments = jsonTmp.getJSONObject("comments").getJSONArray("data");
                            int lastIndex = comments.length() - 1;
                            //person's name
                            String personName = comments.getJSONObject(lastIndex).getJSONObject("from").getString("name");
                            //person's comment
                            String personComment = comments.getJSONObject(lastIndex).getString("message");
                            //postID
                            String postID = jsonTmp.getString("id");
                            Commit commit = new Commit(img, personName, personComment,postID);
                            data.add(commit);
                        }
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
            img = BitmapFactory.decodeStream(url.openConnection().getInputStream());
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
