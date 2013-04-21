package uk.ac.brookes.bourgein.reversiand;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
/**
 * Preferences activity where users can view and change preferences
 * @author bourgein
 *
 */
public class CustomPrefsActivity extends BaseActivity {
	
	/**Used to choose the black and white tiles*/ 
	ImageButton blackWhiteImgBtn;
	/**Used to choose the red and yellow tiles*/
	ImageButton redYellowImgBtn;
	/**Used to choose timed game or not*/
	CheckBox timedCheck;
	/**Used to choose sounds on or off*/
	CheckBox soundCheck;
	/**Used to set the number of seconds for each turn if timed game on*/
	EditText secondsEditText;
	/**Used to save preferences and finish the Activity*/
	Button saveBtn;
	
	/**Type of tile chosen. 1 for black.white, 2 for red/yellow*/
	int tileChoice;
	/**Time game on or off*/
	boolean timed;
	/**Sound on or off*/
	boolean sound;
	/**Number of seconds for each turn*/
	int seconds;
	/**Used to access and modify shared preferences*/
	SharedPreferences settings;
	
	/**
	 * Called when the activity starts.
	 * Initialises class fields and sets onClicks for each tile choice ImageButton and the save Button
	 */
	@Override
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
				Intent goHome = new Intent(getApplicationContext(), HomeActivity.class);
				startActivity(goHome);
				
			}
		});
	}
	
	/**
	 * Gets the settings from shared preferences and sets appropriate class variables
	 */
	private void getSettings(){
		tileChoice = settings.getInt("tileChoice", 1);
		timed = settings.getBoolean("timed", false);
		sound = settings.getBoolean("sounds", false);
		seconds = settings.getInt("seconds", 60);
	}
	
	/**
	 * Sets the Views with appropriate text or images
	 */
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
	
	/**
	 * Commits the user choices to shared preferences
	 */
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
