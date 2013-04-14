package com.example.coursework;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;

import com.ubhave.dataformatter.csv.CSVFormatter;
import com.ubhave.sensormanager.ESException;
import com.ubhave.sensormanager.ESSensorManager;
import com.ubhave.sensormanager.data.SensorData;
import com.ubhave.sensormanager.sensors.SensorUtils;

public class Listen extends Activity {
	
	double latitude, longitude, soundPressureLevel;
	View currentView = null;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        soundPressureLevel = -1;
        latitude = -1;
        longitude = -1;
        setContentView(R.layout.activity_listen);
    	findViewById(R.id.listen_button).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				currentView = v;
				String location = getLocation();
				soundPressureLevel = getSoundPressureLevel();
				try {
					publishResult(soundPressureLevel);
					alert("Results", "Your locaion is " + location + " and the sound pressure level in your locaion is " + soundPressureLevel + "db");
				} catch (Exception e) {
					alert("Result sending failed", "Run to the hills... \n" + e.getMessage());
				}
			}
		});
    }
	
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
	private String getLocation()
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
	public int getSoundPressureLevel() {
		ESSensorManager esSensorManager = null; //create a ESSensorManager object
		SensorData noise = null;
		try { 
			esSensorManager = ESSensorManager.getSensorManager(Listen.this.getApplicationContext());  
			noise = esSensorManager.getDataFromSensor(SensorUtils.SENSOR_TYPE_MICROPHONE);
		} catch (ESException e) {
			e.printStackTrace();
			alert("Audio recording failed", "Run to the hills... \n" + e.getMessage());
		}
		CSVFormatter formatter = CSVFormatter.getCSVFormatter(SensorUtils.SENSOR_TYPE_MICROPHONE);
		String csv = formatter.toString(noise);
		
	    String[] myData=csv.split(",");  //convert csv data to an array	    
	    int soundPressureLevelint=0, Prms=0, squareSums=0, Pref=20;
	   
	    //calculate sound pressure level	    
	    for (int i=4;i<myData.length;i++){
	    	int x = Integer.parseInt(myData[i]);
	    	double y = x*x;
	    	squareSums=(int) (squareSums + y) ;
	    }
	    Prms = (int) Math.sqrt(squareSums/(myData.length-4));	
	    soundPressureLevelint = (int) (20*Math.log10(Prms/Pref));
	    return soundPressureLevelint;
	}

	/* ############################## RESULT POSTING PART ############################## */
	private void publishResult(double result) throws Exception
	{
		TelephonyManager tManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE); //get the phone id 
		String phoneID = tManager.getDeviceId();
		
		String url = "http://api.cosm.com/v2/feeds/125086?key=_IlYBldHLBC-ogLz2OMwXRCURmOSAKxIVXRsMi9xZ1ZUZz0g";
		 
		String req = "{";
		req += "\"title\":\"MUC\",";
		req += "\"version\":\"1.0.0\",";
		req += "\"tags\":[ \"Faz\", \"Tiago\", \"MUC\" ],";
		req += "\"location\":{";
		req += "\"disposition\":\"fixed\",";
		req += "\"lat\":"+latitude+",";
		req += "\"lon\":"+longitude+",";
		req += "\"domain\":\"physical\"";
		req += "},";
		req += "\"datastreams\" : [{ \"current_value\" : \""+result+"\", \"id\" : \"" + phoneID + "\" }]";
		req += "}";
		
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpPut httput = new HttpPut(url);
		StringEntity se = new StringEntity(req);
		httput.setEntity(se);
		httput.setHeader("Content-type", "text/json");

		ResponseHandler responseHandler = new BasicResponseHandler();
		String response = httpclient.execute(httput, responseHandler);
	}
}