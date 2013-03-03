package uk.ac.brookes.bourgein.reversiand;

public class Direction {
	private int col;
	private int row;
	
	public Direction(int col, int row){
		this.col = col;
		this.row = row;
	}
	
	public int getCol(){
		return col;
	}
	public int getRow(){
		return row;
	}
}
