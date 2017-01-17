package com.example.nthucs.prototype.Activity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.nthucs.prototype.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

/**
 * Created by user on 2016/11/6.
 */

public class LoginActivity extends Activity {

    public static String facebookUserID,facebookName;
    private LoginButton loginButton;
    private CallbackManager mCallbackManager;
    private ProfileTracker mProfileTracker;
    private AccessToken accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("mhealth","CREATE");
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        if(AccessToken.getCurrentAccessToken()!=null) {
            facebookUserID = Profile.getCurrentProfile().getId();
            facebookName = Profile.getCurrentProfile().getName();
            System.out.println("profile id = "+facebookUserID);
            Intent intent_main = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent_main);
            finish();

        }
        initFBManager();
        Log.v("mhealth","init");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }


    //init facebook manager
    private void initFBManager(){
        loginButton = (LoginButton)findViewById(R.id.login_button);
        loginButton.setReadPermissions("public_profile", "user_friends","user_posts");
        mCallbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.v("mhealth", "Login");
                        accessToken = loginResult.getAccessToken();
                        if(Profile.getCurrentProfile() == null) {
                            mProfileTracker = new ProfileTracker() {
                                @Override
                                protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                                    // profile2 is the new profile
                                    facebookUserID = profile2.getId();
                                    facebookName = profile2.getName();
                                    System.out.println("profile2 id = "+facebookUserID);
                                    mProfileTracker.stopTracking();
                                    Intent intent_main = new Intent(getApplicationContext(), HomeActivity.class);
                                    startActivity(intent_main);
                                }
                            };
                        }
                        else {
                            facebookUserID = Profile.getCurrentProfile().getId();
                            facebookName = Profile.getCurrentProfile().getName();
                            System.out.println("profile id = "+facebookUserID);
                            Intent intent_main = new Intent(getApplicationContext(), HomeActivity.class);
                            startActivity(intent_main);
                        }


                    }
                    @Override
                    public void onCancel() {
                        Log.v("mhealth", "oncancel");
                        Toast.makeText(getApplicationContext(), "Login Cancel", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onError(FacebookException exception) {
                        Log.v("mhealth", "onerror");
                        Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

}
