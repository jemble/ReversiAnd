package uk.ac.brookes.bourgein.reversiand;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
	private Context context;
	private int[] board;
	private int whiteId;
	private int blackId;
	private SharedPreferences settings;
	
	public ImageAdapter(Context c, int[] board){
		this.context = c;
		this.board = board;
		settings = PreferenceManager.getDefaultSharedPreferences(context);
		if(settings.getInt("tileChoice", 1) == 1){
			whiteId = R.drawable.white;
			blackId = R.drawable.black;
		}
		else {
			whiteId = R.drawable.red;
			blackId = R.drawable.yellow;
		}
		
	}
	
	@Override
	public int getCount() {
		return board.length;
	}

	@Override
	public Object getItem(int position) {
		
		return null;
	}

	@Override
	public long getItemId(int position) {
		
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView;
		if(convertView==null){
			imageView = new ImageView(context);
			imageView.setLayoutParams(new GridView.LayoutParams(parent.getWidth()/8,parent.getWidth()/8));
			imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
			imageView.setPadding(0,0,0,0);
		}
		else{
			imageView = (ImageView)convertView;
		}
		switch (board[position]){
			case 1: 
				imageView.setImageResource(whiteId);
				break;
			case 2:
				imageView.setImageResource(blackId);
				break;
			default:
				imageView.setImageResource(R.drawable.blank);
				break;
		}
		return imageView;
	}

}
