package uk.ac.brookes.bourgein.reversiand;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;

/**
 * Highscores Activity where users can view the current highscores table
 * @author bourgein
 *
 */
public class HighscoreActivity extends BaseActivity {
	/**List of names to display*/
	private ArrayList<String> names = new ArrayList<String>();
	/**List of scores to display*/
	private ArrayList<Integer> scores = new ArrayList<Integer>();
	/**List of URIs as Strings used to display highscore entry picture*/
	private ArrayList<String> uris = new ArrayList<String>();
	/**Used to query the database*/
	private ContentResolver cr;
	/**A custom adapter to display each row of the highscore ListView*/
	private HighscoreAdapter scoresAdapter;
	/**Used to display the rows of highscores*/
	private ListView scoresList;
	
	/**
	 * Called when the Activity is created.
	 * Inflates the layout from highscore_layout.xml, queries the database and loops through the results displaying each 
	 * entry as a row in the ListView using the custom HighscoreAdapter
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.highscore_layout);
		
		cr = getContentResolver();
		Cursor c = cr.query(HighscoreProvider.CONTENT_URI, null, null, null, HighscoreProvider.KEY_SCORE+" DESC");
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
	
}
