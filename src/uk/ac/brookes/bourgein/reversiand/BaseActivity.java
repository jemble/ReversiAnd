package uk.ac.brookes.bourgein.reversiand;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public abstract class BaseActivity extends Activity {

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()) {
		case R.id.home:
//			Toast setToast = Toast.makeText(getApplicationContext(), "setting", Toast.LENGTH_SHORT);
//			setToast.show();
			
			Intent homeIntent = new Intent(getApplicationContext(),activity_home.class);
			startActivity(homeIntent);
			finish();
			return true;
		case R.id.settings:
			Intent settingsIntent = new Intent(getApplicationContext(),CustomPrefs.class);
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
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setUpActionBar() {
	    // Make sure we're running on Honeycomb or higher to use ActionBar APIs
	    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
	        ActionBar actionBar = getActionBar();
	        actionBar.setDisplayHomeAsUpEnabled(true);
	    }
	}

}
