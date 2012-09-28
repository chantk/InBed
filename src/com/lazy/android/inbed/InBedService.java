package com.lazy.android.inbed;

import android.app.Service;
//import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
//import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContentResolver;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;
//import java.lang.System;
import java.util.List;

public class InBedService extends Service implements SensorEventListener {
        //extends BroadcastReceiver {
    private static final String TAG = "InBedService";
    private static final String ROTATION = Settings.System.ACCELEROMETER_ROTATION;
    private static final int ROTATION_ENABLED = 1;
    private static final int ROTATION_DISABLED = 0;
    
    private float pitch = 0;
    private float roll = 0;

    private static SensorManager sm;
    private static Sensor sensor;
    //private static int userPreference;
    NotificationManager mNM;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        //Toast.makeText(this, "InBed Service Created", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onCreate");

        sm = (SensorManager)getSystemService(SENSOR_SERVICE);
	
	// Enable auto-rotate initially. Otherwise there is no point in using InBed.
	Settings.System.putInt(getContentResolver(), ROTATION, ROTATION_ENABLED);
	/*
	try {
	    userPreference = Settings.System.getInt(getContentResolver(), ROTATION);
	}
	catch (Settings.SettingNotFoundException e) {
	    // setting doesn't exist
	}
	*/
	/**
        if (!sm.registerListener(this, SensorManager.SENSOR_ORIENTATION)) { 
            Log.e(TAG, "Could not register a listener for SENSOR_ORIENTATION");
            new AlertDialog.Builder(this) 
                .setTitle(R.string.alert_sensors_orientation_title)
                .setMessage(R.string.alert_sensors_orientation_not_available)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setNegativeButton(android.R.string.ok, 
                        new android.content.DialogInterface.OnClickListener() { 
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                InBed.this.finish();
                            }
                        })
                .setCancelable(false) 
                .show(); 
        }
	**/
    }

    @Override
    public void onDestroy() { 
        Toast.makeText(this, "InBed Service Stopped", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onDestroy");
        sm.unregisterListener(this);
	Settings.System.putInt(getContentResolver(), ROTATION, ROTATION_ENABLED);
	mNM.cancel(R.string.inbed_service_started);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "InBed Service Started", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onStartCommand");
	Log.i("InBedService", "Received start id " + startId + ": " + intent);

	List<Sensor> sensors = sm.getSensorList(Sensor.TYPE_ORIENTATION);
	if (sensors.size() > 0) {
	    sensor = sensors.get(0);
	    sm.registerListener(this, sensor, 
			SensorManager.SENSOR_DELAY_UI);
			//SensorManager.SENSOR_DELAY_NORMAL);
	}

	mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
	showNotification();
	return START_STICKY;
    }

    @Override 
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override 
    public void onSensorChanged(SensorEvent event) 
    {
	pitch = event.values[1];
	roll = event.values[2];
	
        Log.d(TAG, "onSensorChanged: " + ", y: " + pitch + ", z: " + roll);

	if (isFacingDown(pitch, roll)) {
	    Settings.System.putInt(getContentResolver(), ROTATION, 
	    		ROTATION_DISABLED);
	    Log.d(TAG, "Disabled auto-rotate");
	} else {
	    Settings.System.putInt(getContentResolver(), ROTATION, 
	    		ROTATION_ENABLED);
	    Log.d(TAG, "Enabled auto-rotate");
	}
	try {
		int ret = Settings.System.getInt(getContentResolver(), ROTATION);
		Log.d(TAG, "ROTATION = " + ret);
	}
	catch (Settings.SettingNotFoundException e) {}
    }

    private boolean isFacingDown(float pitch, float roll) {
        boolean isDownwards = false;
	// Aggressive orientation
	if (-15 <= pitch && 15 >= pitch) {
	    if ((-90 < roll && -70 >= roll) ||
	       (70 <= roll && 90 > roll)) {
		   isDownwards = true;
	       }
	} 
	else if (-180 <= pitch && -90 >= pitch) {
            if (-90 <= roll && 90 >= roll) {
		Log.d(TAG, "Screen is facing downwards, head up.");
                isDownwards = true;
	    }
        }
        else if (90 <= pitch && 180 >= pitch) {
            if (-90 <= roll && 90 >= roll) {
		Log.d(TAG, "Screen is facing downwards, head down.");
                isDownwards = true;
	    }
        }
        else isDownwards = false;
        return isDownwards;
    }

    /**
     * Show a notification while this service is running.
     */
    private void showNotification() {
	CharSequence text = getText(R.string.inbed_service_started);

	// Set the icon, scrolling text and timestamp
	Notification notification = new Notification(R.drawable.notification_icon, text,
		System.currentTimeMillis());

	// The PendingIntent to launch our activity if the user selects this notification
	PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
		new Intent(this, InBed.class), 0);

	// Set the info for the views that show in the notification panel.
	notification.setLatestEventInfo(this, getText(R.string.inbed_service_label),
		text, contentIntent);

	notification.flags |= Notification.FLAG_ONGOING_EVENT;

	// Send the notification.
	// We use a layout id because it is a unique number. We use it later to cancel.
	mNM.notify(R.string.inbed_service_started, notification);
    }
}

