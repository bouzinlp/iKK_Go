package com.example.nthucs.prototype;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    private ListView food_list;

    private FoodAdapter foodAdapter;

    private List<Food> foods;

    private MenuItem add_food, search_food, revert_food, delete_food;

    private int selectedCount = 0;

    private FoodDAO foodDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        food_list = (ListView)findViewById(R.id.food_list);
        processControllers();

        foodDAO = new FoodDAO(getApplicationContext());

        foods = foodDAO.getAll();

        foodAdapter = new FoodAdapter(this, R.layout.single_food, foods);
        food_list.setAdapter(foodAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Food food = (Food)data.getExtras().getSerializable(
                    "com.example.nthucs.prototype.Food");

            if (requestCode==0) {
                food = foodDAO.insert(food);

                foods.add(food);
                foodAdapter.notifyDataSetChanged();
            }
            else if (resultCode==1) {
                int position = data.getIntExtra("position", -1);

                if (position != -1) {
                    foodDAO.update(food);

                    foods.set(position, food);
                    foodAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);

        add_food = menu.findItem(R.id.add_food);
        search_food = menu.findItem(R.id.search_food);
        revert_food = menu.findItem(R.id.revert_food);
        delete_food = menu.findItem(R.id.delete_food);

        processMenu(null);

        return true;
    }

    private void processControllers() {
        // 建立選單食物點擊監聽物件
        AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Food food = foodAdapter.getItem(position);

                if (selectedCount > 0) {
                    processMenu(food);
                    foodAdapter.set(position, food);
                } else {
                    Intent intent = new Intent(
                            "com.example.nthucs.prototype.EDIT_FOOD");

                    intent.putExtra("position", position);
                    intent.putExtra("com.example.nthucs.prototype.Food", food);

                    startActivityForResult(intent, 1);
                }
            }
        };

        // 註冊選單食物點擊監聽物件
        food_list.setOnItemClickListener(itemListener);

        // 建立選單食物長按監聽物件
        AdapterView.OnItemLongClickListener itemLongListener = new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                Food food = foodAdapter.getItem(position);

                processMenu(food);
                foodAdapter.set(position, food);
                return true;
            }
        };

        // 註冊選單食物長按監聽物件
        food_list.setOnItemLongClickListener(itemLongListener);
    }

    private void processMenu(Food food) {
        if (food != null) {
            food.setSelected(!food.isSelected());

            if (food.isSelected())
                selectedCount++;
            else
                selectedCount--;
        }

        add_food.setVisible(selectedCount==0);
        search_food.setVisible(selectedCount==0);
        revert_food.setVisible(selectedCount>0);
        delete_food.setVisible(selectedCount>0);
    }

    public void clickMenuItem(MenuItem item) {
        int foodId = item.getItemId();

        switch (foodId) {
            case R.id.add_food:
                Intent intent = new Intent("com.example.nthucs.prototype.ADD_FOOD");
                startActivityForResult(intent, 0);
                break;
            case R.id.search_food:

                break;
            case R.id.revert_food:
                for (int i = 0 ; i < foodAdapter.getCount() ; i++) {
                    Food food = foodAdapter.getItem(i);

                    if (food.isSelected()) {
                        food.setSelected(false);
                        foodAdapter.set(i, food);
                    }
                }
                selectedCount = 0;
                processMenu(null);
                break;
            case R.id.delete_food:
                if (selectedCount == 0) break;

                AlertDialog.Builder d = new AlertDialog.Builder(this);
                String message = getString(R.string.delete_food);
                d.setTitle(R.string.delete).setMessage(String.format(message, selectedCount));
                d.setPositiveButton(android.R.string.yes,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int index = foodAdapter.getCount() - 1;

                                while (index > -1) {
                                    Food food = foodAdapter.get(index);

                                    if (food.isSelected()) {
                                        foodAdapter.remove(food);
                                        foodDAO.delete(food.getId());
                                    }
                                    index--;
                                }
                                foodAdapter.notifyDataSetChanged();
                            }
                        });
                d.setNegativeButton(android.R.string.no, null);
                d.show();

                break;
        }
    }
}
