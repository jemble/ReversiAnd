package uk.ac.brookes.bourgein.reversiand;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
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
		    
		    
		}
	
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
    	if(resultCode==RESULT_OK){
    		if(requestCode == PLAYER_ONE_PICK){
    			Uri contactUri = data.getData();
    			Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);
    			
    			if(cursor.moveToFirst()){
    				int idx = cursor.getColumnIndex(ContactsContract.Contacts._ID);
    			    String id = cursor.getString(idx);
    			    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
    			    SharedPreferences.Editor editor = settings.edit();
    			    editor.putString("playerOneId", id);
    			    editor.commit();
    			    
    			    
    			    Toast testToast = Toast.makeText(getApplicationContext(), id, Toast.LENGTH_LONG);
    			    testToast.show();
    			}
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

}
