package uk.ac.brookes.bourgein.reversiand;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.widget.Toast;

public class PrefsActivity extends PreferenceActivity {
	protected static final int PLAYER_ONE_PICK = 1;
	protected static final int PLAYER_TWO_PICK = 2;

	protected void onCreate(Bundle savedInstanceState) {
		   super.onCreate(savedInstanceState);
		   setUpActionBar();
		   addPreferencesFromResource(R.xml.prefs_layout);
		   
		   Preference customContact = (Preference)findPreference("contactPref");
		    customContact.setOnPreferenceClickListener(new OnPreferenceClickListener() {

		        @Override
		        public boolean onPreferenceClick(Preference preference) {
		            Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
		            startActivityForResult(i, PLAYER_ONE_PICK);
		            return true;
		        }
		    });
		    
		    Preference customContactTwo = (Preference)findPreference("contactPrefTwo");
		    customContactTwo.setOnPreferenceClickListener(new OnPreferenceClickListener() {

		        @Override
		        public boolean onPreferenceClick(Preference preference) {
		            Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
		            startActivityForResult(i, PLAYER_TWO_PICK);
		            return true;
		        }
		    });		    
		}
	
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
    	if(resultCode==RESULT_OK){
    		switch(requestCode){
    		case PLAYER_ONE_PICK:
    			Uri contactOneUri = data.getData();
    			addContactsToPrefs("playerOne", contactOneUri);
    		case PLAYER_TWO_PICK:
    			Uri contactTwoUri = data.getData();
    			addContactsToPrefs("playerTwo", contactTwoUri);
    		}
    	}
    }
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setUpActionBar() {
	    // Make sure we're running on Honeycomb or higher to use ActionBar APIs
	    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
	        ActionBar actionBar = getActionBar();
	        actionBar.setDisplayHomeAsUpEnabled(true);
	    }
	}
	
	private void addContactsToPrefs(String player, Uri contactUri){

		Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);
		
		if(cursor.moveToFirst()){
			int idx = cursor.getColumnIndex(ContactsContract.Contacts._ID);
		    long id = cursor.getLong(idx);
		    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		    SharedPreferences.Editor editor = settings.edit();
		    editor.putLong(player+"Id", id);
		    Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id);
		    Uri pic = Uri.withAppendedPath(person,ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
		    
		    Cursor nameCursor = getContentResolver().query(
					ContactsContract.Contacts.CONTENT_URI,
					null,
					ContactsContract.Contacts._ID+"="+id,
					null,
					null);
	        int nameIdx= nameCursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME);
	        String playerName=player;
	        if (nameCursor.moveToFirst()){
	        	playerName = nameCursor.getString(nameIdx); 
	        }
		    
		    editor.putString(player+"Name", playerName);
		    editor.putString(player+"Pic",pic.toString());
		    Toast endToast = Toast.makeText(getApplicationContext(), pic.toString(), Toast.LENGTH_SHORT);
			endToast.show();
		    editor.commit();
		}
	
	}

}
