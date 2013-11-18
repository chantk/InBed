package com.lazy.android.inbed;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
//import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
//import android.hardware.SensorListener;
//import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ToggleButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class InBed extends Activity {
    public static final String TAG = "InBed";

//    private static final int MODE_PORTRAIT = 0;
//    private static final int MODE_LANDSCAPE = 1;
//    private static final String ROTATION = "ACCELEROMETER_ROTATION";

    //Button startButton, stopButton;
    private TextView mStartTimeDisplay, mStopTimeDisplay;
    private Button mPickTime;

    private int mHour;
    private int mMinute;

    static final int TIME_DIALOG_ID = 0;


    /** Called when the activity is first created. */ 
   @Override 
    public void onCreate(Bundle savedInstanceState) { 
        Log.d(TAG, "started"); 
	Log.d(TAG, "v0.0.7");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

	updateDisplay();

    }

    public void onToggleClicked(View view) {
	// Is the toggle on?
	boolean on = ((ToggleButton) view).isChecked();

	if (on) {
	    // Enable the service
	    Intent intent = new Intent(this, InBedService.class);
	    startService(intent);
	    // pass intent to service here
	    
	} else {
	    // Disable the service
	    Intent intent = new Intent(this, InBedService.class);
	    stopService(intent);
	}
    }

    private void updateDisplay() {
	/*	mStartTimeDisplay.setText(
		new StringBuilder()
		.append(pad(mHour)).append(":")
		.append(pad(mMinute)));
	
	AlarmManager mgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
	*/
	Intent i = new Intent(this, InBedService.class);
	PendingIntent pi = PendingIntent.getService(this, 0, i, 0);

	/*	Time mStartTime = new Time();
	mStartTime.hour = mHour;
	mStartTime.minute = mMinute;
	mgr.setRepeating(AlarmManager.RTC, mStartTime.toMillis(true), AlarmManager.INTERVAL_DAY, pi);
	Log.d(TAG, "Next start time: " + mStartTime.toString());
	*/
    }

    /*    private static String pad(int c) {
	if (c >= 10)
	    return String.valueOf(c);
	else
	    return "0" + String.valueOf(c);
	    }*/

    /*    @Override
    protected Dialog onCreateDialog(int id) {
	switch (id) {
	case TIME_DIALOG_ID:
	    return new TimePickerDialog(this,
		    mTimeSetListener, mHour, mMinute, false);
	}
	return null;
    }

    private TimePickerDialog.OnTimeSetListener mTimeSetListener = 
	new TimePickerDialog.OnTimeSetListener() {
	    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		mHour = hourOfDay;
		mMinute = minute;
		updateDisplay();
	    }
	};
    */
}
