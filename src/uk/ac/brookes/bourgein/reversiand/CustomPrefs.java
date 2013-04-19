package uk.ac.brookes.bourgein.reversiand;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;

public class CustomPrefs extends BaseActivity {
	
	//layout
	ImageButton blackWhiteImgBtn;
	ImageButton redYellowImgBtn;
	CheckBox timedCheck;
	CheckBox soundCheck;
	EditText secondsEditText;
	Button saveBtn;
	
	//prefs
	int tileChoice;
	boolean timed;
	boolean sound;
	int seconds;
	
	//other
	SharedPreferences settings;
	
	
	protected void onCreate(Bundle savedInstanceState) {
		   super.onCreate(savedInstanceState);
		   setContentView(R.layout.custom_prefs);
		   
		   //initialise
		   blackWhiteImgBtn = (ImageButton)findViewById(R.id.blackWhiteImgBtn);
		   redYellowImgBtn = (ImageButton)findViewById(R.id.redYellowImgBtn);
		   timedCheck = (CheckBox)findViewById(R.id.timedCheck);
		   soundCheck = (CheckBox)findViewById(R.id.soundCheck);
		   secondsEditText = (EditText)findViewById(R.id.secondsEditText);
		   saveBtn = (Button)findViewById(R.id.prefsSaveBtn);
		   settings = PreferenceManager.getDefaultSharedPreferences(this);
		   
		   getSettings();
		   setLayout();
		   
		   //onclicks
		   blackWhiteImgBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				blackWhiteImgBtn.setImageDrawable(getResources().getDrawable(R.drawable.black_white_select));
				redYellowImgBtn.setImageDrawable(getResources().getDrawable(R.drawable.red_yellow_unselect));
				tileChoice = 1;
			}
		});
		   
		   redYellowImgBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				blackWhiteImgBtn.setImageDrawable(getResources().getDrawable(R.drawable.black_white_unselect));
				redYellowImgBtn.setImageDrawable(getResources().getDrawable(R.drawable.red_yellow_select));
				tileChoice = 2;
			}
		});
		   saveBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				saveSettings();
				Intent goHome = new Intent(getApplicationContext(), activity_home.class);
				startActivity(goHome);
				
			}
		});
	}
	
	private void getSettings(){
		tileChoice = settings.getInt("tileChoice", 1);
		timed = settings.getBoolean("timed", false);
		sound = settings.getBoolean("sounds", false);
		seconds = settings.getInt("seconds", 60);
	}
	
	private void setLayout(){
		timedCheck.setChecked(timed);
		soundCheck.setChecked(sound);
		secondsEditText.setText(String.valueOf(seconds));
		switch(tileChoice){
		case 1:
			blackWhiteImgBtn.setImageDrawable(getResources().getDrawable(R.drawable.black_white_select));
			redYellowImgBtn.setImageDrawable(getResources().getDrawable(R.drawable.red_yellow_unselect));
			break;
		case 2:
			blackWhiteImgBtn.setImageDrawable(getResources().getDrawable(R.drawable.black_white_unselect));
			redYellowImgBtn.setImageDrawable(getResources().getDrawable(R.drawable.red_yellow_select));
			break;
		}
	}
	
	private void saveSettings(){
		
		timed = timedCheck.isChecked();
		sound = soundCheck.isChecked();
		seconds = Integer.parseInt(secondsEditText.getText().toString());
		
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("timed", timed);
		editor.putBoolean("sounds", sound);
		editor.putInt("seconds", seconds);
		editor.putInt("tileChoice", tileChoice);
		
		editor.commit();
	}
}
