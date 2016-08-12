package com.example.nthucs.prototype.Utility;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.example.nthucs.prototype.Activity.GalleryActivity;
import com.example.nthucs.prototype.R;

/**
 * Created by USER12345678 on 2016/8/12.
 */
public class CustomDialog extends Dialog implements
        android.view.View.OnClickListener {

    public GalleryActivity galleryActivity;
    public Dialog dialog;

    public Button OK_button;

    public CustomDialog(GalleryActivity galleryActivity) {
        super(galleryActivity);
        this.galleryActivity = galleryActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);

        OK_button = (Button) findViewById(R.id.dialog_button);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_button:
                dismiss();
                break;
            default:
                break;
        }
        //dismiss();
    }
}
