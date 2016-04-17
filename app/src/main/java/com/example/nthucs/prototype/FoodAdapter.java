package com.example.nthucs.prototype;

import android.content.Context;
import android.widget.ArrayAdapter;
import java.util.List;

public class FoodAdapter extends ArrayAdapter<Food> {

    private int resource;

    private List<Food> foods;

    public FoodAdapter(Context context, int resource, List<Food> foods) {
        super(context, resource, foods);
        this.resource = resource;
        this.foods = foods;
    }
}
