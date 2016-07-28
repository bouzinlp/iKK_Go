package com.example.nthucs.prototype.SportList;

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

import com.example.nthucs.prototype.R;

import java.util.List;

/**
 * Created by user on 2016/7/27.
 */
public class SportAdapter extends ArrayAdapter<Sport> {

    private int resource;

    private List<Sport> sports;

    public SportAdapter(Context context, int resource, List<Sport> sports) {
        super(context, resource, sports);
        this.resource = resource;
        this.sports = sports;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout sportView;

        final Sport sport = getItem(position);

        if (convertView == null) {
            sportView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater li = (LayoutInflater)getContext().getSystemService(inflater);
            li.inflate(resource, sportView, true);
        } else {
            sportView = (LinearLayout) convertView;
        }

        // 讀取記事顏色、已選擇、標題
        RelativeLayout typeColor = (RelativeLayout) sportView.findViewById(R.id.type_color);
        ImageView selectedItem = (ImageView) sportView.findViewById(R.id.selected_item);
        TextView titleView = (TextView) sportView.findViewById(R.id.title_text);

        // 設定單一顏色
        GradientDrawable background = (GradientDrawable)typeColor.getBackground();
        Color color = new Color();
        background.setColor(color.parseColor("2E8B57"));

        // 設定是否已選擇
        selectedItem.setVisibility(sport.isSelected() ? View.VISIBLE : View.INVISIBLE);

        // 設定標題
        titleView.setText(sport.getTitle());

        return sportView;
    }

    public void set(int index, Sport sport) {
        if (index >= 0 && index < sports.size()) {
            sports.set(index, sport);
            notifyDataSetChanged();
        }
    }

    public Sport get(int index) {
        return sports.get(index);
    }

}
