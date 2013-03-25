package com.example.coursework;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.os.Handler;


public class Listen extends Activity {
	
	double latitude, longitude;
	private MediaRecorder mRecorder = null;
	private Handler mPeriodicEventHandler;
	View currentView = null;
	

	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        latitude = -1;
        longitude = -1;
        setContentView(R.layout.activity_listen);
    	findViewById(R.id.listen_button).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				currentView = v;
				startRecording();
				mPeriodicEventHandler = new Handler();
			    mPeriodicEventHandler.postDelayed(doPeriodicTask, 3000);			    
				//alert("cool", getLocation());
			}
		});
    }
	private Runnable doPeriodicTask = new Runnable()
    {
        public void run() 
        {
        	stopRecording();
        }
    };
	
	public void alert(String title, String message)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(currentView.getContext());
        builder.setTitle(title);
        builder.setMessage(message);
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
	
	/* ############################## GEOLOCATION PART ############################## */
	private String getLocation ()
	{
	    LocationManager locationManager = (LocationManager)getSystemService (LOCATION_SERVICE);
	    String bestProvider = locationManager.getBestProvider (new Criteria (), false);
	    locationManager.requestLocationUpdates(bestProvider,0 ,0, loc_listener);
		Location location = locationManager.getLastKnownLocation (bestProvider);
		try
		{
			latitude = location.getLatitude ();
			longitude = location.getLongitude ();
		}
		catch (NullPointerException e)
		{
			latitude = -1.0;
			longitude = -1.0;
		}
		return "lat: " + latitude + " lon: " + longitude;
	  }
	  LocationListener loc_listener = new LocationListener() {
		  public void onLocationChanged(Location l) { }
		  public void onProviderEnabled(String p) { }
		  public void onProviderDisabled(String p) { }
		  public void onStatusChanged(String p, int status, Bundle extras) { }      
	};

	/* ############################## SOUND RECORDING PART ############################## */
	private void startRecording() {
		String mFileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/testfile.3gp";
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (Exception e) {
            alert("Audio recording failed", "Run to the hills");
        }

        mRecorder.start();
    }
	private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }
	@Override
    public void onDestroy() {

        mPeriodicEventHandler.removeCallbacks(doPeriodicTask);      
        super.onDestroy();
    }
}