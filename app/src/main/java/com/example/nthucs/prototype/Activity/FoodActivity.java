package com.example.nthucs.prototype.Activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.nthucs.prototype.FoodList.CalorieDAO;
import com.example.nthucs.prototype.FoodList.FoodCal;
import com.example.nthucs.prototype.SpinnerWheel.CustomDialogForFood;
import com.example.nthucs.prototype.Utility.FileUtil;
import com.example.nthucs.prototype.FoodList.Food;
import com.example.nthucs.prototype.R;
import java.io.File;
import java.util.Date;

import com.example.nthucs.prototype.antistatic.spinnerwheel.AbstractWheel;
import com.example.nthucs.prototype.antistatic.spinnerwheel.OnWheelChangedListener;
import com.example.nthucs.prototype.antistatic.spinnerwheel.OnWheelClickedListener;
import com.example.nthucs.prototype.antistatic.spinnerwheel.adapters.ArrayWheelAdapter;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.ShareActionProvider;


public class FoodActivity extends AppCompatActivity {

    // dialog for choosing food within spinner wheel
    private Button dialogTitleButton;

    // text input
    //private EditText title_text;
    private EditText content_text, calorie_text, portions_text, grams_text;

    // food information
    private Food food;

    // picture information
    private String fileName;
    private ImageView picture;

    // pass Uri's toString if take photo from library
    private String picUriString;
    private Uri picUri;

    // data base for storing calorie data
    private CalorieDAO calorieDAO;

    // food cal list
    private List<FoodCal> foodCalList = new ArrayList<>();

    //facebook share dialog
    private ShareDialog shareDialog;

    //voice
    protected static final int RESULT_SPEECH = 1;
    private Button btnSpeak;
    private TextView txtText;
    private ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);

        //voice
        txtText = (TextView) findViewById(R.id.txtText);
        btnSpeak = (Button) findViewById(R.id.voice_btn);
        btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_voice = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent_voice.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
                try {
                    startActivityForResult(intent_voice, RESULT_SPEECH);
                    txtText.setText("");
                } catch (ActivityNotFoundException a) {
                    Toast t = Toast.makeText(getApplicationContext(), "Opps! Your device doesn't support Speech to Text", Toast.LENGTH_SHORT);
                    t.show();
                }
            }
        });

        FacebookSdk.sdkInitialize(getApplicationContext());
        shareDialog = new ShareDialog(this);

        // process dialog button for title
        processDialogButtonControllers();

        //title_text = (EditText)findViewById(R.id.title_text);
        content_text = (EditText)findViewById(R.id.content_text);
        calorie_text = (EditText)findViewById(R.id.calorie_text);
        portions_text = (EditText)findViewById(R.id.portions_text);
        grams_text = (EditText)findViewById(R.id.grams_text);

        picture = (ImageView) findViewById(R.id.picture_food);

        Intent intent = getIntent();
        String action = intent.getAction();

        if (action.equals("com.example.nthucs.prototype.EDIT_FOOD")) {
            food = (Food)intent.getExtras().getSerializable(
                    "com.example.nthucs.prototype.FoodList.Food");

            dialogTitleButton.setText(food.getTitle());

            //title_text.setText(food.getTitle());
            content_text.setText(food.getContent());
            calorie_text.setText(Float.toString(food.getCalorie()));
            portions_text.setText(Float.toString(food.getPortions()));
            grams_text.setText(Float.toString(food.getGrams()));

        } else if (action.equals("com.example.nthucs.prototype.ADD_FOOD")) {
            food = new Food();
        }

        // calorie data base
        calorieDAO = new CalorieDAO(getApplicationContext());

        // get all data
        foodCalList = calorieDAO.getAll();
    }

    @Override
    protected void onResume() {
        super.onResume();

        fileName = food.getFileName();

        System.out.println("date time: "+food.getLocaleDatetime());

        if (food.getFileName() != null && food.getFileName().length() > 0) {
            // camera can access this statement
            if (food.isTakeFromCamera() == true) {
                // photo taken from camera display with config way
                File file = configFileName("P", ".jpg");
                if (file.exists()) {
                    // 顯示照片元件
                    picture.setVisibility(View.VISIBLE);
                    // 設定照片
                    FileUtil.fileToImageView(file.getAbsolutePath(), picture);
                }
            // gallery can access this statement
            } else {
                // photo taken from gallery display with parsing uri
                picUriString = food.getPicUriString();

                picUri = Uri.parse(picUriString);
                File file2 = new File(picUri.getPath());

                if (file2.exists()) {
                    // 顯示照片元件
                    picture.setVisibility(View.VISIBLE);
                    // 設定照片
                    FileUtil.fileToImageView(file2.getAbsolutePath(), picture);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
        }

        //voice
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_SPEECH: {
               if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txtText.setText(text.get(0));
               }
               break;
            }
        }

    }

    public void onSubmit(View view) {
        if (view.getId() == R.id.ok_item) {
            String titleText = dialogTitleButton.getText().toString();
            String contentText = content_text.getText().toString();
            String calorieText = calorie_text.getText().toString();
            String portionsText = portions_text.getText().toString();
            String gramsText = grams_text.getText().toString();

            food.setTitle(titleText);
            food.setContent(contentText);
            food.setCalorie(Float.parseFloat(calorieText));
            food.setPortions(Float.parseFloat(portionsText));
            food.setGrams(Float.parseFloat(gramsText));

            // if add food with photo, then also record establish time
            if (getIntent().getAction().equals("com.example.nthucs.prototype.ADD_FOOD")) {
                food.setDatetime(new Date().getTime());
            }

            Intent result = getIntent();
            result.putExtra("com.example.nthucs.prototype.FoodList.Food", food);
            setResult(Activity.RESULT_OK, result);
        }
        finish();
    }

    private File configFileName(String prefix, String extension) {
        if (fileName == null) {
            fileName = FileUtil.getUniqueFileName();
        }

        return new File(FileUtil.getExternalStorageDir(FileUtil.APP_DIR),
                prefix + fileName + extension);
    }

    public void clickFunction(View view) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.food_menu, menu);
        initShareIntent(menu);
        return super.onCreateOptionsMenu(menu);
    }

    /*
    @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        getMenuInflater().inflate(R.menu.activity_main, menu);
	        return true;
	    }
    */

    private void shareToFB(){
        try {
            File file = configFileName("P", ".jpg");
            Bitmap image = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(file));
            SharePhoto photo = new SharePhoto.Builder()
                    .setBitmap(image)
                    .build();
            SharePhotoContent content = new SharePhotoContent.Builder()
                    .addPhoto(photo)
                    .build();


            if(shareDialog.canShow(SharePhotoContent.class)){
                shareDialog.show(content);
                System.out.println("SET");
            }
            else{
                System.out.println("U CANT");
            }

        }
        catch (Exception e){
            System.out.println("EXCEPTION : "+e);
        }
    }

    // Call to update the share intent
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    private void initShareIntent(Menu menu){

        MenuItem item = menu.findItem(R.id.share_food);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        File file = configFileName("P", ".jpg");
        Uri uri = Uri.fromFile(file);
        shareIntent.putExtra(Intent.EXTRA_STREAM,uri);
        mShareActionProvider.setShareIntent(shareIntent);
        mShareActionProvider.setOnShareTargetSelectedListener(new ShareActionProvider.OnShareTargetSelectedListener(){

            @Override
            public boolean onShareTargetSelected(ShareActionProvider source, Intent intent) {
                String shareTarget = intent.getComponent().getPackageName();
                if(shareTarget.toLowerCase().startsWith("com.facebook.katana")){
                    shareToFB();
                }
                return false;
            }
        });
    }

    // process dialog button controllers
    private void processDialogButtonControllers() {
        // initialize dialog button
        dialogTitleButton = (Button)findViewById(R.id.dialog_button);

        // avoid all upper case
        dialogTitleButton.setTransformationMethod(null);

        // set button listener
        dialogTitleButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                // process custom dialog
                CustomDialogForFood customDialogForFood = new CustomDialogForFood(foodCalList, FoodActivity.this);
                customDialogForFood.processDialogControllers();
            }
        });
    }

    // get dialog title button public
    public Button getDialogTitleButton() {
        return this.dialogTitleButton;
    }

    // get calorie edit text public
    public EditText getCalorieText() {
        return this.calorie_text;
    }
}
