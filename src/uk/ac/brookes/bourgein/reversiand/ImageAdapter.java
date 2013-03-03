package uk.ac.brookes.bourgein.reversiand;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
	private Context context;
	private int[] board;
	
	public ImageAdapter(Context c, int[] board){
		this.context = c;
		this.board = board;
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
			imageView.setLayoutParams(new GridView.LayoutParams(90,90));
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			imageView.setPadding(0,0,0,0);
		}
		else{
			imageView = (ImageView)convertView;
		}
		switch (board[position]){
			case 1: 
				imageView.setImageResource(R.drawable.white);
				break;
			case 2:
				imageView.setImageResource(R.drawable.black);
				break;
			default:
				imageView.setImageResource(R.drawable.blank);
				break;
		}
		return imageView;
	}

}
