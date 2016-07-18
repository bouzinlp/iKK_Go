package com.example.nthucs.prototype.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Toast;

import com.example.nthucs.prototype.MessageList.Commit;
import com.example.nthucs.prototype.MessageList.MessageAdapter;
import com.example.nthucs.prototype.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by admin on 2016/7/18.
 */
public class ChatRoomActivity extends AppCompatActivity {

    CallbackManager callbackManager;
    public ListView listView;
    String httpUrl;
    ArrayList<Commit> chatRoom ;
    Bitmap img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        FacebookSdk.sdkInitialize(getApplicationContext());
        init();
        listView = (ListView) findViewById(R.id.messageList);
        chatRoom = new ArrayList<>();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void facebookChatRoom(AccessToken accessToken,String id){
        GraphRequest request = GraphRequest.newGraphPathRequest(
                accessToken,
                "/"+id+"/comments",
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        // Insert your code here
                        System.out.println("on complete");
                        try {
                            System.out.println(response.getJSONObject());
                            JSONArray responseData = response.getJSONObject().getJSONArray("data");
                            for(int i=0;i<responseData.length();i++){
                                JSONObject jsonTmp = responseData.getJSONObject(i);
                                //posted img
                                httpUrl ="https://graph.facebook.com/"+jsonTmp.optJSONObject("from").getString("id")+"/picture";
                                System.out.println(httpUrl);
                                Thread thread = new Thread(mutiThread);
                                thread.start();
                                try {
                                    thread.join();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                System.out.println(img);
                                //person's name
                                String personName =jsonTmp.optJSONObject("from").getString("name") ;
                                System.out.println("personName "+personName);
                                //person's comment
                                String personComment = jsonTmp.getString("message") ;
                                System.out.println("message "+personComment);
                                Commit commit = new Commit(img, personName, personComment,"0");
                                chatRoom.add(commit);
                                System.out.println("chatRoom size "+chatRoom.size());
                            }
                            MessageAdapter adapter = new MessageAdapter(getApplicationContext(), chatRoom);
                            listView.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        request.executeAsync();
    }

    public void getBitmapFromURL(String src) {
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

    private void init(){
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        System.out.println("SUCCESS");
                        try{
                            Intent intent = getIntent();
                            facebookChatRoom(AccessToken.getCurrentAccessToken(),intent.getStringExtra("postID"));
                        }catch (Exception e){

                            System.out.println(e.toString());
                        }

                    }

                    @Override
                    public void onCancel() {
                        // App code
                        // savedInstanceState
                        System.out.println("CANCEL");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        System.out.println("ERROR");

                    }
                });

        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends","user_posts"));
        System.out.println("LOGIN");
    }
}
