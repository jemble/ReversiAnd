package uk.ac.brookes.bourgein.reversiand;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * Custom adapter to show a row of the highscore table
 * @author bourgein
 *
 */
public class HighscoreAdapter extends BaseAdapter {
	
	/**Used to get the correct context. Typically the calling Activity*/
	private Activity activity;
	/**List of names in the highscore table*/
    private ArrayList<String> names;
    /**List of scores in the highscore table*/
    private ArrayList<Integer> scores;
    /**List of URIs as Strings in the highscore table*/
    private ArrayList<String> uris;
    /**Used to inflate the row from highscore_item xml layout file*/
    private static LayoutInflater inflater;
    
    public HighscoreAdapter(Activity a, ArrayList<String> n, ArrayList<Integer> s, ArrayList<String> u){
    	activity = a;
    	names = n;
    	scores = s;
    	uris = u;
    	inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	
    }
    
    /**
     * Returns number of items in highscore list
     */
	@Override
	public int getCount() {
		return names.size();
		
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	/**
	 * Creates a View based on position in list.
	 * If the position is odd the background will be black else the background will be white
	 * @param position the position in the list
	 * @param convertView the old view to reuse
	 * @param parent the parent ViewGroup
	 * @return the View created
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		
		if (convertView == null){
			vi = inflater.inflate(R.layout.highscore_item, null);
		}
		TextView txtName = (TextView)vi.findViewById(R.id.hs_name);
		ImageView imgPerson = (ImageView)vi.findViewById(R.id.hs_image);
		TextView txtScore = (TextView)vi.findViewById(R.id.hs_score);
		if (uris.get(position)!=null){
			imgPerson.setImageURI(Uri.parse(uris.get(position)));
		}
		if ((position+1)%2 != 0){
			
			vi.setBackgroundColor(activity.getApplicationContext().getResources().getColor(R.color.playerOneColor));
			txtName.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.playerOneTextColor));
			txtScore.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.playerOneTextColor));
		}
		else{
			
		}
		txtName.setText(names.get(position));
		txtScore.setText(String.valueOf(scores.get(position)));
		
		return vi;
	}

}
