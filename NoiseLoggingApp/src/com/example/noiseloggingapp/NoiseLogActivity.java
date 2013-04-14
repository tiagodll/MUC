package com.example.noiseloggingapp;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ubhave.dataformatter.DataFormatter;
import com.ubhave.dataformatter.csv.CSVFormatter;
import com.ubhave.dataformatter.json.JSONFormatter;
import com.ubhave.sensormanager.ESException;
import com.ubhave.sensormanager.ESSensorManager;
import com.ubhave.sensormanager.data.SensorData;
import com.ubhave.sensormanager.sensors.SensorUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import android.graphics.LightingColorFilter;
import android.graphics.PorterDuff;
 

public class NoiseLogActivity extends Activity {
	
	View currentView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_noise_log); 
		findViewById(R.id.button1).getBackground().setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0xFFAA0000)); //CHeneg button colour code from: stackoverflow.com/questions/1521640/standard-android-button-with-a-different-color
	   	findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() { //create a listener for button1 
				public void onClick(View v) {
					currentView = v;
					
					ESSensorManager esSensorManager = null; //create a ESSensorManager object
					SensorData noise = null;
					try { 
						esSensorManager = ESSensorManager.getSensorManager(NoiseLogActivity.this.getApplicationContext());  
						noise = esSensorManager.getDataFromSensor(SensorUtils.SENSOR_TYPE_MICROPHONE); //get one sample sound ???S
					} catch (ESException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					CSVFormatter formatter = CSVFormatter.getCSVFormatter(SensorUtils.SENSOR_TYPE_MICROPHONE);
					String csv = formatter.toString(noise);
				    //alert("cool", csv);
					
				    String[] myData=csv.split(",");  //convert csv data to an array
				    
				    int soundPressureLevelint=0, Prms=0, squareSums=0, Pref=20  ;
				   
				    //calculate sound pressure level
				    
				    for (int i=4;i<myData.length;i++){  
				    	
				    	int x = Integer.parseInt(myData[i]);
				    	double y = x*x;
				    	squareSums=(int) (squareSums + y) ;
				    }
				    Prms=(int) Math.sqrt(squareSums/(myData.length-4));	
				    soundPressureLevelint=(int) (20*Math.log10(Prms/Pref));
				    String soundPressureLevelstr = Integer.toString(soundPressureLevelint);
				    //alert("Sound Pressure Level",soundPressureLevelstr);

				    String myLocation=getLocation ();
				    alert("Location & Sound Level", myLocation + "\r \n Sound Pressure Levl:" + soundPressureLevelstr);
					 
				    TelephonyManager tManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE); //get the phone id 
					 String phoneID = tManager.getDeviceId();
					 postResult(soundPressureLevelstr, myLocation, phoneID);

				}
			});
	}
	
	 
	
	/* ############################## GEOLOCATION PART ############################## */
	
	private String getLocation ()
	{
	    LocationManager locationManager = (LocationManager)getSystemService (LOCATION_SERVICE);
	    String bestProvider = locationManager.getBestProvider (new Criteria (), false);
	    locationManager.requestLocationUpdates(bestProvider,0 ,0, loc_listener);
		Location location = locationManager.getLastKnownLocation (bestProvider);
		
		double latitude;
		double longitude;
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
		return "Latitude: " + latitude + " Longitude: " + longitude;
	  }
	  LocationListener loc_listener = new LocationListener() {
		  public void onLocationChanged(Location l) { }
		  public void onProviderEnabled(String p) { }
		  public void onProviderDisabled(String p) { }
		  public void onStatusChanged(String p, int status, Bundle extras) { }      
	};
	
	
	 private void postResult(String soundLevel, String myLocation, String phoneID)
	{
		//alert("Amplitude", "So this are the values: " + amplitude);
		 
	
		try{
		URL url = new URL("http://api.cosm.com/v2/feeds/124820");
		HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
		httpCon.setDoOutput(true);
		httpCon.setRequestMethod("PUT");
		OutputStreamWriter out = new OutputStreamWriter(
		    httpCon.getOutputStream());
		//out.write("{\"datastreams\":[{\"id\":\" "+ phoneID +"\", \"Sound Level\": "+soundLevel+", \"Location\":"+myLocation+"}]}");
		out.write("{\"datastreams\":[{\"phoneID\":\" "+ phoneID +"\", \"soundLevel\": "+soundLevel+", \"myLocation\":"+myLocation+"}]}");
		out.close();
		}catch (Exception e) {
            alert("Result sending failed", "Run to the hills... \n" + e.getMessage());
        }
	}
	
	private void alert(String title, String message)
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.noise_log, menu);
		return true;
	}
}
