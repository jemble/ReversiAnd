package uk.ac.brookes.bourgein.reversiand;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;

public class PlayerSelectActivity extends BaseActivity {
	
	protected static final int PLAYER_ONE_PICK = 1;
	protected static final int PLAYER_TWO_PICK = 2;
	protected static final int MAIN_ACTIVITY_QUIT = 3;
	
	boolean cpu;
	//layout
	ImageView playerOneImg;
	ImageView playerTwoImg;
	TextView playerOneNameTxtVw;
	TextView playerTwoNameTxtVw;
	Button playerOneSelectContactBtn;
	Button playerTwoSelectContactBtn;
	Button playerOneEditTextBtn;
	Button playerTwoEditTextBtn;
	Button playBtn;
	//strings
	String playerOneName;
	String playerTwoName;
	String playerOneUriString;
	String playerTwoUriString;
	
	
	SharedPreferences settings;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player_select_layout);
		 
		//initialise everything
		//playerOne
		playerOneImg = (ImageView)findViewById(R.id.playerOneImgVw);
		playerOneNameTxtVw = (TextView)findViewById(R.id.playerOneName);
		playerOneSelectContactBtn = (Button)findViewById(R.id.playerOneSelectContact);
		playerOneEditTextBtn = (Button)findViewById(R.id.playerOneEditDialog);
		 
		//playerTwo
		playerTwoImg = (ImageView)findViewById(R.id.playerTwoImgVw);
		playerTwoNameTxtVw = (TextView)findViewById(R.id.playerTwoName);
		playerTwoSelectContactBtn = (Button)findViewById(R.id.playerTwoSelectContact);
		playerTwoEditTextBtn = (Button)findViewById(R.id.playerTwoEditDialog);
		 
		//other
		playBtn = (Button)findViewById(R.id.playButton);
		settings = PreferenceManager.getDefaultSharedPreferences(this);
		
		//is CPU playing
		Intent intent = getIntent();
		cpu = intent.getBooleanExtra("Cpu",false);
 		
		getSettings();
		
		//Clicks
		playerOneSelectContactBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
	            startActivityForResult(i, PLAYER_ONE_PICK);	
			}
		});
		
		playerOneEditTextBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getUserNameFromDialog(1);
			}
		});
		
		playerTwoSelectContactBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
	            startActivityForResult(i, PLAYER_TWO_PICK);	
			}
		});
		
		playerTwoEditTextBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getUserNameFromDialog(2);
			}
		});
		
		playBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				putSettings();
				Intent cpuIntent = new Intent(getApplicationContext(),MainActivity.class);
				cpuIntent.putExtra("Cpu", cpu);
				//startActivity(cpuIntent);
				startActivityForResult(cpuIntent,MAIN_ACTIVITY_QUIT);
			}
		});
		
		//set layout
		setLayouts(playerOneNameTxtVw,playerOneUriString,playerOneImg,playerOneName);
		if (cpu){
			hidePlayerTwo();
		}
		else{
			setLayouts(playerTwoNameTxtVw,playerTwoUriString,playerTwoImg,playerTwoName);	
		}
		
	}
	
	public void getSettings(){
		playerOneName = settings.getString("playerOneName", "Player One");
		playerOneUriString = settings.getString("playerOnePic", null);
		
		if(!cpu){
			playerTwoName = settings.getString("playerTwoName", "Player Two");
			playerTwoUriString = settings.getString("playerTwoPic",null);
		}	
	}
	
	public void putSettings(){
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("playerOneName", playerOneName);
		editor.putString("playerOnePic", playerOneUriString);
		
		if (!cpu){
			editor.putString("playerTwoName", playerTwoName);
			editor.putString("playerTwoPic", playerTwoUriString);
		}
		editor.commit();
	}
	
	public InputStream openPhoto(String photoUriString) {
	    
	     Uri photoUri = Uri.parse(photoUriString);
	     Cursor cursor = getContentResolver().query(photoUri,
	          new String[] {Contacts.Photo.PHOTO}, null, null, null);
	     if (cursor == null) {
	         return null;
	     }
	     try {
	         if (cursor.moveToFirst()) {
	             byte[] data = cursor.getBlob(0);
	             if (data != null) {
	                 return new ByteArrayInputStream(data);
	             }
	         }
	     } finally {
	         cursor.close();
	     }
	     return null;
	 }
	
	public void onActivityResult(int requestCode, int resultCode, Intent data){
    	if(resultCode==RESULT_OK){
    		switch(requestCode){
    		case PLAYER_ONE_PICK:
    			Uri contactOneUri = data.getData();
    			addContactsToPrefs(1, contactOneUri);
    			setLayouts(playerOneNameTxtVw,playerOneUriString,playerOneImg,playerOneName);
    			break;
    		case PLAYER_TWO_PICK:
    			Uri contactTwoUri = data.getData();
    			addContactsToPrefs(2, contactTwoUri);
    			setLayouts(playerTwoNameTxtVw,playerTwoUriString,playerTwoImg,playerTwoName);
    			break;
    		}
    	}
    	else if(resultCode == MAIN_ACTIVITY_QUIT){ //this is needed to handle back press from main activity
    		PlayerSelectActivity.this.finish();
    	}
    }
	
	private void addContactsToPrefs(int player, Uri contactUri){

		Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);
		String playerName = "Player " + player;
		if(cursor.moveToFirst()){
			int idx = cursor.getColumnIndex(ContactsContract.Contacts._ID);
		    long id = cursor.getLong(idx);
		    Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id);
		    Uri pic = Uri.withAppendedPath(person,ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
		    
		    Cursor nameCursor = getContentResolver().query(
					ContactsContract.Contacts.CONTENT_URI,
					null,
					ContactsContract.Contacts._ID+"="+id,
					null,
					null);
	        int nameIdx= nameCursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME);
	        
	        if (nameCursor.moveToFirst()){
	        	playerName = nameCursor.getString(nameIdx); 
	        }
		    
	        if (player == 1){
	        	playerOneUriString = pic.toString();
	        	playerOneName = playerName;
	        }
	        else {
	        	playerTwoUriString = pic.toString();
	        	playerTwoName = playerName;
	        }
		}
	}
	
	private void setLayouts(TextView playerText, String uriString, ImageView imgView, String name){		
		playerText.setText(name);
		if (uriString != null){
			InputStream stream  = openPhoto(uriString);
			if (stream != null){
				Bitmap bitmap = BitmapFactory.decodeStream(stream);
				imgView.setImageBitmap(bitmap);
			}
			else {
				imgView.setImageResource(R.drawable.stub);
			}
		}
		else {
			imgView.setImageResource(R.drawable.stub);
		}
	}
	
	private void hidePlayerTwo(){
		TextView playerTwoTextView = (TextView)findViewById(R.id.playerTwoTitle);
		playerTwoTextView.setVisibility(View.INVISIBLE);
		playerTwoImg.setVisibility(View.INVISIBLE);
		playerTwoName = "Computer";
		playerTwoNameTxtVw.setVisibility(View.INVISIBLE);
		playerTwoSelectContactBtn.setVisibility(View.INVISIBLE);
		playerTwoEditTextBtn.setVisibility(View.INVISIBLE);
		playerTwoUriString = null;		
	}
	
	void getUserNameFromDialog(int player){
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		final int playerNum = player;
		alert.setTitle("Player Name");
		alert.setMessage("Enter the player name");

		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		String value;
		public void onClick(DialogInterface dialog, int whichButton) {
		  value = input.getText().toString();
		  
		  if (playerNum == 1){
			  playerOneName = value;
			  playerOneUriString = null;
			  Toast playOne = Toast.makeText(getApplicationContext(), playerOneName, Toast.LENGTH_SHORT);
				playOne.show();
				setLayouts(playerOneNameTxtVw,playerOneUriString,playerOneImg,playerOneName);
		  }
		  else {
			  playerTwoName = value;
			  playerTwoUriString = null;
			  Toast playOne = Toast.makeText(getApplicationContext(), playerTwoName, Toast.LENGTH_SHORT);
				playOne.show();
				setLayouts(playerTwoNameTxtVw,playerTwoUriString,playerTwoImg,playerTwoName);
		  }
		  } 
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		    // Canceled.
		  }
		});

		alert.show();
	}

}
