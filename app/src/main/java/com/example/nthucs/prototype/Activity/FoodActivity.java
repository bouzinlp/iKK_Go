package com.example.nthucs.prototype.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.nthucs.prototype.FoodList.CalorieDAO;
import com.example.nthucs.prototype.FoodList.FoodCal;
import com.example.nthucs.prototype.Jsouptest.JsoupUse;
import com.example.nthucs.prototype.Utility.FileUtil;
import com.example.nthucs.prototype.FoodList.Food;
import com.example.nthucs.prototype.R;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

import com.facebook.FacebookSdk;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import java.util.ArrayList;
import java.util.List;

import android.content.ActivityNotFoundException;
import android.speech.RecognizerIntent;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class FoodActivity extends AppCompatActivity {


    private EditText dialogTitleEditText;

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

    // distinguish add or edit food
    private boolean isAddFood;

    //facebook share dialog
    private ShareDialog shareDialog;

    //voice
    protected static final int RESULT_SPEECH = 1;
    private Button btnSpeak;
    private TextView txtText;
    private ShareActionProvider mShareActionProvider;
    private String[] info;
    String inputText;
    public ProgressDialog searchDialog;
    private Spinner mealTypespinner;
    private int mealTypeIndex = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);

        //spinner
        mealTypespinner = (Spinner)findViewById(R.id.select_meal_spinner);
        ArrayAdapter<CharSequence> mealList = ArrayAdapter.createFromResource(this,
                R.array.meal_type,
                android.R.layout.simple_spinner_dropdown_item);
        mealTypespinner.setAdapter(mealList);
        mealTypespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                mealTypeIndex = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

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

        dialogTitleEditText = (EditText)findViewById(R.id.food_edittext);
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

            dialogTitleEditText.setText(food.getTitle());

            //title_text.setText(food.getTitle());
            content_text.setText(food.getContent());
            calorie_text.setText(Float.toString(food.getCalorie()));
            portions_text.setText(Float.toString(food.getPortions()));
            grams_text.setText(Float.toString(food.getGrams()));
            mealTypespinner.setSelection(food.getMealTypeIndex());

            isAddFood = false;

        } else if (action.equals("com.example.nthucs.prototype.ADD_FOOD")) {
            food = new Food();

            isAddFood = true;
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

        System.out.println("date time: " + food.getLocaleDatetime());

        if (food.getFileName() != null && food.getFileName().length() > 0) {
            // camera can access this statement
            if (food.isTakeFromCamera()) {
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
        switch(view.getId()){
            case R.id.ok_item:
                String titleText = dialogTitleEditText.getText().toString();
                String contentText = content_text.getText().toString();
                String calorieText = calorie_text.getText().toString();
                String portionsText = portions_text.getText().toString();
                String gramsText = grams_text.getText().toString();

                if (titleText.length() == 0) Toast.makeText(this, "食物名稱不可為空", Toast.LENGTH_SHORT).show();
                else if (calorieText.length() == 0) Toast.makeText(this, "熱量不可為空", Toast.LENGTH_SHORT).show();
                else if (portionsText.length() == 0) Toast.makeText(this, "食物份數不可為空", Toast.LENGTH_SHORT).show();
                else if (gramsText.length() == 0) Toast.makeText(this, "食物重量不可為空", Toast.LENGTH_SHORT).show();

                else {
                    // related calorie, portion and grams
                    float finalCalorie = Float.parseFloat(calorieText);
                    float originPortions = food.getPortions();
                    float originGrams = food.getGrams();
                    float modifyPortions = Float.parseFloat(portionsText);
                    float modifyGrams = Float.parseFloat(gramsText);
                    if (originPortions == 0) originPortions = 1;
                    /*if (originGrams != modifyGrams) {
                        modifyPortions = modifyGrams / 100;
                        finalCalorie = (modifyPortions/originPortions)*Float.parseFloat(calorieText);
                    } else if (originPortions != modifyPortions) {
                        modifyGrams = modifyPortions * 100;
                        finalCalorie = (modifyPortions/originPortions)*Float.parseFloat(calorieText);
                    }*/

                    // set to food, back to main activity will be updated
                    food.setTitle(titleText);
                    food.setContent(contentText);
                    food.setCalorie(finalCalorie);
                    food.setPortions(modifyPortions);
                    food.setGrams(modifyGrams);
                    food.setMealTypeIndex(mealTypeIndex);

                    // if add food with photo, then also record establish time
                    if (getIntent().getAction().equals("com.example.nthucs.prototype.ADD_FOOD")) {
                        food.setDatetime(new Date().getTime());
                    }

                    Intent result = getIntent();
                    result.putExtra("com.example.nthucs.prototype.FoodList.Food", food);
                    setResult(Activity.RESULT_OK, result);
                    finish();
                }
                break;
            case R.id.go_searchFood:
                if (dialogTitleEditText.getText().toString().length() == 0) Toast.makeText(this, "食物名稱不可為空", Toast.LENGTH_SHORT).show();
                else {
                    searchDialog = ProgressDialog.show(this, "搜尋中", "請稍後...");
                    inputText = dialogTitleEditText.getText().toString();
                    new searchFood().execute();
                }
                break;
            case R.id.cancel_item:
                finish();
                break;
        }


    }

    private File configFileName(String prefix, String extension) {
        if (fileName == null) {
            fileName = FileUtil.getUniqueFileName();
        }
        return new File(FileUtil.getExternalStorageDir(FileUtil.APP_DIR),
                prefix + fileName + extension);
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
            //File file = configFileName("P", ".jpg");
            File file2 = new File(picUri.getPath());
            Bitmap image = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(file2));
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

    private void initShareIntent(Menu menu){

        MenuItem item = menu.findItem(R.id.share_food);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
         /*NOTE   NEED TO DISTINGISH WHERE THE IMAGE PATH COME FROM*/
        File file;
        if (food.getPicUriString()!=null) {
            // photo taken from gallery display with parsing uri
            picUriString = food.getPicUriString();
            picUri = Uri.parse(picUriString);
            file = new File(picUri.getPath());
        }
        else{
            // photo taken from camera display with config way
            file = configFileName("P", ".jpg");
        }
        //File file = configFileName("P", ".jpg");
        //File file = new File(picUri.getPath());
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
    /*private void processDialogButtonControllers() {
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
    }*/

    // get dialog title button public
    /*public Button getDialogTitleButton() {
        return this.dialogTitleButton;
    }*/

    // get calorie edit text public
    public EditText getCalorieText() {
        return this.calorie_text;
    }

    // get portions edit text public
    public EditText getPortionsText() {
        return this.portions_text;
    }

    // get grams edit text public
    public EditText getGramsText() {
        return this.grams_text;
    }

    // get boolean value for custom dialog
    public boolean isAddFood() {
        return this.isAddFood;
    }

    private class searchFood extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                String catchString = JsoupUse.getMyFitnessPalDateBase(inputText ,1);
                info = JsoupUse.splitEveryInfor(catchString);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused)
        {
            super.onPostExecute(unused);
            searchDialog.dismiss();
            selectfood(info);
        }
    }

    private void  selectfood(String[] foodarray   ) {

        final ArrayList<String> items = new ArrayList<>();
        for(int i=0;i<foodarray.length;++i){
            if(foodarray[i].contains("Generic"))
                items.add(foodarray[i]);
        }
        for(int i=1;i<foodarray.length;++i){
            if(!foodarray[i].contains("Generic"))
                items.add(foodarray[i]);
        }
        for(int i=0;i<foodarray.length;++i){
            if(foodarray[i].contains(","))
                System.out.println(foodarray[i]);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(FoodActivity.this);
        builder.setTitle("Select food");
        builder.setItems(items.toArray(new String[items.size()]), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int index) {
                String nowtext= items.get(index);
                handleInfoStr(nowtext);
            }
        });
        AlertDialog alertDialog = builder.create();
        final ListView listview = alertDialog.getListView();
        listview.setDivider(new ColorDrawable(Color.GRAY));
        listview.setDividerHeight(2);
        alertDialog.show();
    }

    private void handleInfoStr(String nowText){
        String[] split = nowText.split(",");
        dialogTitleEditText.setText(split[0].split(" ")[0]);
        calorie_text.setText(nowText.split(":")[2].split(",")[0]);
        portions_text.setText("1.0");
        grams_text.setText("100");
    }

}
