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

public class HighscoreAdapter extends BaseAdapter {

	private Activity activity;
    private ArrayList<String> names;
    private ArrayList<Integer> scores;
    private ArrayList<String> uris;
    private static LayoutInflater inflater=null;
    
    public HighscoreAdapter(Activity a, ArrayList<String> n, ArrayList<Integer> s, ArrayList<String> u){
    	activity = a;
    	names = n;
    	scores = s;
    	uris = u;
    	inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	
    }
    
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
		
		txtName.setText(names.get(position));
		txtScore.setText(String.valueOf(scores.get(position)));
		
		return vi;
	}

}
