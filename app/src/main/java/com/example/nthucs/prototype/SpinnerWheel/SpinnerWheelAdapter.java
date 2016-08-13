package com.example.nthucs.prototype.SpinnerWheel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.nthucs.prototype.R;
import com.example.nthucs.prototype.antistatic.spinnerwheel.adapters.AbstractWheelTextAdapter;

/**
 * Created by user on 2016/8/13.
 */
public class SpinnerWheelAdapter extends AbstractWheelTextAdapter {

    private int resource;

    // Compared result ready to select
    private String compare_result[] = new String[]{};

    /**
     * Constructor
     */
    public SpinnerWheelAdapter(Context context, int resource, String[] compare_result) {
        super(context, resource, NO_RESOURCE);

        this.resource = resource;
        setItemTextResource(resource);
        this.compare_result = compare_result;
    }

    @Override
    public View getItem(int position, View convertView, ViewGroup parent) {
        View view = super.getItem(position, convertView, parent);

        // temporary
        //TextView titleView = (TextView)view.findViewById(R.id.spinner_wheel_item);
        //titleView.setText(compare_result[position]);

        return view;
    }

    @Override
    public int getItemsCount() {
        return compare_result.length;
    }

    @Override
    protected CharSequence getItemText(int index) {
        return compare_result[index];
    }

}
