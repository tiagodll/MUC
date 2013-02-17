package com.example.coursework;

import com.example.coursework.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

public class Listen extends Activity {

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_listen);
    	findViewById(R.id.listen_button).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
		        builder.setTitle("Title");
		        builder.setMessage("yay, it works!");
		        builder.setNeutralButton("ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						dialog.dismiss();
					}
				});
		        builder.setCancelable(false);
		        final AlertDialog myAlertDialog = builder.create();
		        myAlertDialog.show();
			}
		});
    }
}
