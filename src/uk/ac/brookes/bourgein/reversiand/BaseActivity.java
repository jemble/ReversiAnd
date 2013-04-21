package uk.ac.brookes.bourgein.reversiand;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.view.Menu;
import android.view.MenuItem;
/**
 * Used to define default menu for all activities except GameActivity
 * @author bourgein
 *
 */
public abstract class BaseActivity extends Activity {
	/**
	 * Inflates the menu.activity_main xml menu layout file
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	/**
	 * Handles clicks on the three menu options. 
	 * Each selection starts the relevant Activity  
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()) {
		case R.id.home:
			Intent homeIntent = new Intent(getApplicationContext(),HomeActivity.class);
			startActivity(homeIntent);
			finish();
			return true;
		case R.id.settings:
			Intent settingsIntent = new Intent(getApplicationContext(),CustomPrefsActivity.class);
			startActivity(settingsIntent);
			finish();
			return true;
		case R.id.highscores:
			Intent highscoresIntent = new Intent(getApplicationContext(),HighscoreActivity.class);
			startActivity(highscoresIntent);
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	/**
	 * Checks if API > 3.0 and if so enables the action bar
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setUpActionBar() {
	    // Make sure we're running on Honeycomb or higher to use ActionBar APIs
	    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
	        ActionBar actionBar = getActionBar();
	        actionBar.setDisplayHomeAsUpEnabled(true);
	    }
	}

}
