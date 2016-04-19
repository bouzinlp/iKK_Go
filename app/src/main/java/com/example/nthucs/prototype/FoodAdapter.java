package com.example.nthucs.prototype;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class FoodAdapter extends ArrayAdapter<Food> {

    private int resource;

    private List<Food> foods;

    public FoodAdapter(Context context, int resource, List<Food> foods) {
        super(context, resource, foods);
        this.resource = resource;
        this.foods = foods;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout foodView;

        final Food food = getItem(position);

        if (convertView == null) {
            foodView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater li = (LayoutInflater)
                    getContext().getSystemService(inflater);
            li.inflate(resource, foodView, true);
        }
        else {
            foodView = (LinearLayout) convertView;
        }

        // 讀取記事顏色、已選擇、標題
        RelativeLayout typeColor = (RelativeLayout) foodView.findViewById(R.id.type_color);
        ImageView selectedItem = (ImageView) foodView.findViewById(R.id.selected_item);
        TextView titleView = (TextView) foodView.findViewById(R.id.title_text);

        // 設定單一顏色
        GradientDrawable background = (GradientDrawable)typeColor.getBackground();
        Color color = new Color();
        background.setColor(color.parseColor("#3F51B5"));

        // 設定是否已選擇
        selectedItem.setVisibility(food.isSelected() ? View.VISIBLE : View.INVISIBLE);

        // 設定標題
        titleView.setText(food.getTitle());

        return foodView;
    }

    public void set(int index, Food food) {
        if (index >= 0 && index < foods.size()) {
            foods.set(index, food);
            notifyDataSetChanged();
        }
    }

    public Food get(int index) {
        return foods.get(index);
    }
}
