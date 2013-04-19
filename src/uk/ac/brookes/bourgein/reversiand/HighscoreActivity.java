package uk.ac.brookes.bourgein.reversiand;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

public class HighscoreActivity extends BaseActivity {
	private ArrayList<String> names = new ArrayList<String>();
	private ArrayList<Integer> scores = new ArrayList<Integer>();
	private ArrayList<String> uris = new ArrayList<String>();
	private ContentResolver cr;
	private int numOfHighscores;
	private HighscoreAdapter scoresAdapter;
	private ListView scoresList;
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.highscore_layout);
		
		cr = getContentResolver();
		Cursor c = cr.query(HighscoreProvider.CONTENT_URI, null, null, null, HighscoreProvider.KEY_SCORE+" DESC");
		numOfHighscores = c.getCount();
		if (numOfHighscores == 0){
			populateFakeScores();
			c = cr.query(HighscoreProvider.CONTENT_URI, null, null, null, null);
			numOfHighscores = c.getCount();
		}
		
		String name;
		String picUri;
		int score;
		if(c.moveToFirst()){
			do{
				name = c.getString(HighscoreProvider.NAME_COLUMN);
				picUri = c.getString(HighscoreProvider.PICURI_COLUMN);
				score = c.getInt(HighscoreProvider.SCORE_COLUMN);
				names.add(name);
				scores.add(score);
				uris.add(picUri);
			}
			while(c.moveToNext());
		}
		
		scoresAdapter = new HighscoreAdapter(this, names, scores, uris);
		scoresList = (ListView)findViewById(R.id.hghscore_list);
		scoresList.setAdapter(scoresAdapter);
		
	}
	
	public void populateFakeScores(){
		String[] fakeNames = {"Jeremy","Mike","Tim","Phil","Sandra"};
		String[] fakeUris = {"blah.blah.blah/blah","blah.blah.blah/blah","blah.blah.blah/blah","blah.blah.blah/blah","blah.blah.blah/blah"};
		int[] fakeScores = {23,50,32,66,12};
		
		ContentResolver cr = getContentResolver();

		ContentValues values = new ContentValues();
		for (int i = 0; i < fakeNames.length; i++) {
			values.put(HighscoreProvider.KEY_NAME, fakeNames[i]);
			values.put(HighscoreProvider.KEY_PICURI, fakeUris[i]);
			values.put(HighscoreProvider.KEY_SCORE,fakeScores[i]);
			cr.insert(HighscoreProvider.CONTENT_URI, values);
		}
	}
	
}
