package uk.ac.brookes.bourgein.reversiand;
/**
 * Encapsulates a direction on a compass
 * @author bourgein
 *
 */
public class Direction {
	/**Column direction e.g. 1 for east*/
	private int col;
	/**Row direction e.g. 1 for south*/
	private int row;
	
	public Direction(int col, int row){
		this.col = col;
		this.row = row;
	}
	
	/**
	 * Returns the column direction
	 * @return the column direction
	 */
	public int getCol(){
		return col;
	}
	/**
	 * Returns a row direction
	 * @return the row direction
	 */
	public int getRow(){
		return row;
	}
}
