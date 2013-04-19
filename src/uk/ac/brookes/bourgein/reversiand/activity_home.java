package uk.ac.brookes.bourgein.reversiand;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class activity_home extends Activity {
	
	public static final int PICK_CONTACT = 0;
	private Button onePlayerButton;
	private Button twoPlayerButton;
	private Button settingsButton;
	private Button highscoreButton;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.home_screen);
         
         onePlayerButton = (Button)findViewById(R.id.btn_1player);
         twoPlayerButton = (Button)findViewById(R.id.btn_2player);
         settingsButton = (Button)findViewById(R.id.btn_settings);
         highscoreButton = (Button)findViewById(R.id.btn_highscore);
         
         onePlayerButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent onePlayerIntent = new Intent(getApplicationContext(),PlayerSelectActivity.class);
				onePlayerIntent.putExtra("Cpu", true);
				startActivity(onePlayerIntent);
			}
		});
         
         twoPlayerButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent twoPlayerIntent = new Intent( getApplicationContext(), PlayerSelectActivity.class);
				twoPlayerIntent.putExtra("Cpu", false);
		        startActivity(twoPlayerIntent);
			}
		});
         
         settingsButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent settingsIntent = new Intent(getApplicationContext(),CustomPrefs.class);
				startActivity(settingsIntent);
				
			}
		});
         
         highscoreButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent highscoreIntent = new Intent(getApplicationContext(),HighscoreActivity.class);
				startActivity(highscoreIntent);				
			}
		});
    }
}
