package com.example.nthucs.prototype.Activity;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import com.example.nthucs.prototype.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import android.support.v4.app.ActivityCompat;
import android.Manifest;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by user on 2016/11/6.
 */

public class LoginActivity extends Activity {


    public static String facebookUserID,facebookName;
    private LoginButton loginButton;
    private CallbackManager mCallbackManager;
    private ProfileTracker mProfileTracker;
    private AccessToken accessToken;
    private TextView title;
    private static final int REQUEST_MUTIPLE_PERMISSION = 0;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("mhealth","CREATE");
        setContentView(R.layout.activity_login);
        checkAndAskPermission();

        if(AccessToken.getCurrentAccessToken()!=null) {
            facebookUserID = Profile.getCurrentProfile().getId();
            facebookName = Profile.getCurrentProfile().getName();
            System.out.println("profile id = "+facebookUserID);
            Intent intent_main = new Intent(getApplicationContext(), HomeActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("BACK", 0);
            intent_main.putExtras(bundle);
            startActivity(intent_main);
            finish();

        }
        initFBManager();
        Log.v("mhealth","init");
        title = (TextView) findViewById(R.id.APPTitle);
        title.setTypeface(Typeface.createFromAsset(getAssets(),"Righteous-Regular.ttf"));
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
                                    Bundle bundle = new Bundle();
                                    bundle.putInt("BACK", 0);
                                    intent_main.putExtras(bundle);
                                    startActivity(intent_main);
                                }
                            };
                        }
                        else {
                            facebookUserID = Profile.getCurrentProfile().getId();
                            facebookName = Profile.getCurrentProfile().getName();
                            System.out.println("profile id = "+facebookUserID);
                            Intent intent_main = new Intent(getApplicationContext(), HomeActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putInt("BACK", 0);
                            intent_main.putExtras(bundle);
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
                        Log.v("mhealth", exception.getMessage());
                        Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void checkAndAskPermission(){
        int writePermission = ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int locationPermission = ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION);
        int readPhonePermission = ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_PHONE_STATE);
        int recordAudioPermission = ActivityCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO);
        List<String> permissionList = new ArrayList<>();
        if (writePermission != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if(locationPermission != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if(readPhonePermission != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if(recordAudioPermission != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.RECORD_AUDIO);
        }
        ActivityCompat.requestPermissions(
                this,permissionList.toArray(new String[permissionList.size()])
                ,REQUEST_MUTIPLE_PERMISSION
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch(requestCode) {
            case REQUEST_MUTIPLE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //取得權限
                } else {
                    //使用者拒絕權限，停用檔案存取功能
                    Toast.makeText(getApplicationContext(), "請開啟所有權限", Toast.LENGTH_LONG).show();
                }
                break;

        }
    }

}
