package uk.ac.brookes.bourgein.reversiand;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
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
import android.view.View.OnClickListener;
/**
 * Player selection Activity where users can set player names or choose a device contact.
 * 
 * <h3>Overview</h3>
 * The Activity comprises of two collections of Views (one for each player) showing a title, picture and name in addition to two buttons 
 * for each player: one to enter a name and one to select a contact. If the launching Activity (HomeActivity) has set
 * the Boolean extra "Cpu" to true then the player two buttons are made invisible via hidePlayerTwo().
 * <h3>Controls</h3>
 * <b>Enter Name button:</b> calls getUserNameFromDialog().
 * <br/>
 * <b>Choose Contact button:</b> launches the Intent.ACTION_PICK activity with contact.CONTENT_URI allowing a user to pick a contact and get the contact data on result.
 * <br/>
 * <b>Play button:</b> starts the GameActivity and sets up a result. If the GameActivity finishes with a result code of 
 * MAIN_ACTIVITY_QUIT then this Activity finishes and the user is taken to the HomeActivity. Also calls putSettings() to save the
 * chosen player name and photo URI (as a String) to the shared preferences, and sets the extra data of whether the device is playing or not to allow GameActivity
 * to know.
 * 
 * @author bourgein
 *
 */
public class PlayerSelectActivity extends BaseActivity {
	/**Used by onActivityResult() to determine what action to take*/
	protected static final int PLAYER_ONE_PICK = 1, PLAYER_TWO_PICK = 2, MAIN_ACTIVITY_QUIT = 3;
	/**Stores if the device is controlling player two*/
	boolean cpu;
	
	//layout
	/**Player One's picture*/
	private ImageView playerOneImg;
	/**Player Two's picture*/
	private ImageView playerTwoImg;
	/**View to display player One's name*/
	private TextView playerOneNameTxtVw;
	/**View to display player Two's name*/
	private TextView playerTwoNameTxtVw;
	/**Used to select player one contact*/
	private Button playerOneSelectContactBtn;
	/**Used to select player two contact*/
	private Button playerTwoSelectContactBtn;
	/**Used to launch dialog to enter player one's name*/
	private Button playerOneEditTextBtn;
	/**Used to launch dialog to enter player one's name*/
	private Button playerTwoEditTextBtn;
	/**Used to launch start the game*/
	private Button playBtn;
	
	//strings
	private String playerOneName;
	private String playerTwoName;
	/**URI as a string to store the URI of player one's picture*/
	private String playerOneUriString;
	/**URI as a string to store the URI of player two's picture*/
	private String playerTwoUriString;
	
	/**used to access and modify shared preferences*/
	private SharedPreferences settings;
	
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
				Intent cpuIntent = new Intent(getApplicationContext(),GameActivity.class);
				cpuIntent.putExtra("Cpu", cpu);
				startActivityForResult(cpuIntent,MAIN_ACTIVITY_QUIT);
			}
		});
		
		//set layout
		setLayouts(playerOneNameTxtVw,playerOneUriString,playerOneImg,playerOneName);
		if (cpu){
			hidePlayerTwo();
			setLayouts(playerTwoNameTxtVw,playerTwoUriString,playerTwoImg,"Computer");
		}
		else{
			setLayouts(playerTwoNameTxtVw,playerTwoUriString,playerTwoImg,playerTwoName);	
		}
		
	}
	
	/**
	 * Gets settings from shared preferences and stores them in Class fields.
	 * Only gets settings for player two if the CPU is not controlling that player.
	 */
	public void getSettings(){
		playerOneName = settings.getString("playerOneName", "Player One");
		playerOneUriString = settings.getString("playerOnePic", null);
		
		if(!cpu){
			playerTwoName = settings.getString("playerTwoName", "Player Two");
			playerTwoUriString = settings.getString("playerTwoPic",null);
		}	
	}
	
	/**
	 * Commits the Class fields to settings in shared preferences.
	 * Only commits player two's if CPU is not controlling that player.
	 */
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
	
	/**
	 * Finds the InputStream for a contact photo based on the given string.
	 * Uses a Cursor object to query the contacts content provider to get a byte array 
	 * which is then used to return a new ByteArrayInputStream 
	 * @param photoUriString represents a photo URI as a string 
	 * @return Stream to the contact photo if found or null 
	 */
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
	
	/**
	 * Used when activities called from this activity finish.
	 * <br />
	 * If the resultCode is RESULT_OK then depending if requestCode is PLAYER_ONE_PICK or PLAYER_TWO_PICK, will either
	 * call addContactsToPrefs and setLayout for player one or player two.
	 * <br />
	 * If resultCode is MAIN_ACTIVITY_QUIT then finishes this activity. 
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data){
    	if(resultCode==RESULT_OK){
    		switch(requestCode){
    		case PLAYER_ONE_PICK:
    			Uri contactOneUri = data.getData();
    			getContactsForPrefs(1, contactOneUri);
    			setLayouts(playerOneNameTxtVw,playerOneUriString,playerOneImg,playerOneName);
    			break;
    		case PLAYER_TWO_PICK:
    			Uri contactTwoUri = data.getData();
    			getContactsForPrefs(2, contactTwoUri);
    			setLayouts(playerTwoNameTxtVw,playerTwoUriString,playerTwoImg,playerTwoName);
    			break;
    		}
    	}
    	else if(resultCode == MAIN_ACTIVITY_QUIT){ //this is needed to handle back press from main activity
    		PlayerSelectActivity.this.finish();
    	}
    }
	
	/**
	 * Adds either player one or player two's name and photo URI (as a String) to the appropriate field.
	 * <br />
	 * Uses a Cursor object to query the contact content provider using the contactUri provided and if a record is found,
	 * gets the picture URI and display name and stores them in the appropriate field.  
	 * @param player the number of the player to search for. 1 or 2.
	 * @param contactUri the URI of the contact
	 */
	private void getContactsForPrefs(int player, Uri contactUri){

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
	
	/**
	 * Used to set the player's name and picture Views.
	 * <br />
	 * If the player's picture URI (passed as a String) is not null then a Bitmap object is created using BitmapFactory.decodeStream(). The stream is 
	 * provided by openPhoto(). If player's picture URI is null then the default drawable resource ("stub") is used. 
	 * @param playerText the player's name View
	 * @param uriString The URI to the player's picture as a String
	 * @param imgView the player's picture View
	 * @param name the player's name
	 */
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
	
	/**
	 * Used to hide player Two's input controls.
	 * It is only called when player is controlled by the device. The controls hidden are the Select contact button and the Enter Name button
	 */
	private void hidePlayerTwo(){
		playerTwoSelectContactBtn.setVisibility(View.INVISIBLE);
		playerTwoEditTextBtn.setVisibility(View.INVISIBLE);
		playerTwoUriString = null;		
	}
	
	/**
	 * Used to get the text entered in the Enter Name dialog and store the result the appropriate field.
	 * <br />
	 * Method creates an AlertDialog with an EditText input. On positive click the value entered is stored in the appropriate player name field.
	 * Player UriString is set to null to ensure that the default picture is used. Calls setLayout to update the UI as needed. 
	 * @param player the number of the player to get the details for. Either 1 or 2.
	 */
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
			  setLayouts(playerOneNameTxtVw,playerOneUriString,playerOneImg,playerOneName);
		  }
		  else {
			  playerTwoName = value;
			  playerTwoUriString = null;
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
