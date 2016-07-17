package com.example.nthucs.prototype.MessageList;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nthucs.prototype.R;

import java.util.ArrayList;

/**
 * Created by admin on 2016/7/17.
 */
public class MessageAdapter extends ArrayAdapter<Commit> {

    private ArrayList<Commit> objects;

    public MessageAdapter(Context context, ArrayList<Commit> objects){
        super(context,0,objects);
        this.objects = objects;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        Commit commit = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_list,parent,false);
        }
        ImageView personImg = (ImageView) convertView.findViewById(R.id.imgCommitPerson);
        TextView personName = (TextView) convertView.findViewById(R.id.personName);
        TextView personCommit = (TextView) convertView.findViewById(R.id.personCommit);

        personImg.setImageBitmap(Bitmap.createScaledBitmap(commit.getBitmap(), 120, 120, false));
        personName.setText(commit.getName());
        personCommit.setText(commit.getCommit());

        // Return the completed view to render on screen
        return convertView;
    }
}
