package com.example.nthucs.prototype.Setting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.nthucs.prototype.R;

import java.util.List;

/**
 * Created by user on 2016/7/23.
 */
public class SettingAdapter extends ArrayAdapter<String> {

    private int resource;

    private List<String> list_title;

    public SettingAdapter(Context context, int resource, List<String> list_title) {
        super(context, resource, list_title);
        this.resource = resource;
        this.list_title = list_title;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout settingView;

        final String setting_title = getItem(position);

        if (convertView == null) {
            settingView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater li = (LayoutInflater)getContext().getSystemService(inflater);
            li.inflate(resource, settingView, true);
        }
        else {
            settingView = (LinearLayout) convertView;
        }

        // read setting title
        TextView titleView = (TextView) settingView.findViewById(R.id.setting_title);

        // set string on text view
        titleView.setText(setting_title);

        return  settingView;
    }

    public String get(int index) {
        return this.list_title.get(index);
    }
}
