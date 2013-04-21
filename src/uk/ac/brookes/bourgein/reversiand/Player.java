package uk.ac.brookes.bourgein.reversiand;

import uk.ac.brookes.bourgein.reversiand.Direction;

import java.util.ArrayList;
/**
 * All fields and methods relating to a player in the game.
 * 
 * @author bourgein
 *
 */
public class Player {
	/**Used to hold what squares are currently playable. 0 for unplayavble, 1 for playable*/
	private int[] playableSquares;
	/**Player's number. Either 1 or 2*/
	public int playerNum;
	/**Stores the player's display name*/
	public String playerName;
	/**Stores a URI to the player's picture as a String*/
	public String playerUriString;
	/**Stores the player's current score*/
	public int score;
	/**Stores the player's highest scoring row. Used with bestCol to determine best square*/
	public int bestRow;
	/**Stores the player's highest scoring column. Used with bestRow to determine best square*/
	public int bestCol;
	/**Stores if the player has a valid move*/
	public boolean canGo;
	/**Stores if the player is being controlled by the device*/
	public boolean isCpu;
	
	public Player(int player){
		playableSquares = new int[64];
		this.playerNum = player;
		score = 0;
	}
		
	public String getScoreAsString(){
		return Integer.toString(score);
	}
	
	/**
	 * Used to determine what squares are playable on the board.
	 * Works in exactly the same was as GameActivity.flip() but increments a counter every time a valid
	 * move is found. If no moves are found it sets canGo field to false, else canGo =  true.
	 * @param currentGameBoard the current state of the game board
	 * @param dirs the list of directions to move in
	 */
	public void calcPlayableMoves(int[] currentGameBoard, ArrayList<Direction> dirs){
		resetMoves();
		int moveCount=0;
		int otherPlayer = (this.playerNum == 1) ? 2 : 1;
		for (int row = 0;row<8;row++){
			for (int col = 0; col<8;col++){
				
				if (currentGameBoard[row*8+col] == 0){
					for (Direction direction : dirs){
						
						int dirCol = direction.getCol();
						int dirRow = direction.getRow();
						
						if ((row+dirRow) < 8 
								&& (row+dirRow) >= 0
								&& (col+dirCol) < 8
								&& (col+dirCol) >= 0){
							
							if (currentGameBoard[(row+dirRow)*8+(col+dirCol)] == otherPlayer){ //check +1 in the direction we're using is the other player
								int move = 2;
								
								//while we're still on the board. N.b we multiply by the dirRow and dirCol.
								while ((row+(move*dirRow)) < 8
										&& (row+(move*dirRow)) >=0
										&& (col+(move*dirCol)) < 8
										&& (col+(move*dirCol))>=0)
								{
									//if we end up on one of our own pieces
									if (currentGameBoard[(row+(move*dirRow))*8+(col+(move*dirCol))]==this.playerNum){
										setSquareAsPlayable(row, col);
										moveCount++;
									}
									move++;
								}
							}
						}
					}
				}	
			}
		}
		if (moveCount==0){
			this.canGo = false;
		}
		else {
			this.canGo = true;
		}
	}
	
	/**
	 * Used to determine which square on the board is the highest scoring one.
	 * Works in exactly the same way as GameActivity.flip() but every direction for every
	 * playable square it stores the potential points and stores it in a temporary variable.
	 * The highest scoring move is then stored in the bestSquare field.
	 * @param currentGameBoard state of the current game board
	 * @param dirs list of directions to move in
	 */
	public void calcBestMove(int[] currentGameBoard, ArrayList<Direction> dirs){
		int otherPlayer = (this.playerNum == 1) ? 2 : 1;
		int bestScore = 0;
		for (int row=0;row<8;row++){
			for (int col=0;col<8;col++){
				int tempScore = 0;
				if (playableSquares[row*8+col]==1){
					for (Direction dir : dirs){
						int dirCol = dir.getCol();
						int dirRow = dir.getRow();
						
						if ((row + dirRow) < 8 && (row + dirRow) >= 0 && (col + dirCol) < 8
								&& (col + dirCol) >= 0) {
							if (currentGameBoard[(row+dirRow)*8+(col+dirCol)] == otherPlayer) {
								int move = 2;

								// while we're still on the board. N.b we multiply by the
								// dirRow and dirCol.
								while ((row + (move * dirRow)) < 8
										&& (row + (move * dirRow)) >= 0
										&& (col + (move * dirCol)) < 8
										&& (col + (move * dirCol)) >= 0) {
									// if we end up on one of our own pieces
									if (currentGameBoard[(row+(move*dirRow))*8+(col+(move*dirCol))] == this.playerNum) {
										tempScore += move;
									}
									move++;
								}		
							}
						}
					}
				}
				if (tempScore>bestScore){
					bestScore = tempScore;
					this.bestCol = col;
					this.bestRow = row;
				}
			}
		}
	}
	
	/**
	 * Checks if the given square is a valid move or not. 
	 * Looks in the playableSquares array and if a 1 is found returns true else false
	 * @param row the row of the square to check
	 * @param col the column of the square to check
	 * @return true if valid move, else false
	 */
	public boolean isValidMove(int row, int col){
		if(this.playableSquares[row*8+col] == 1){
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * Sets the given square as playable.
	 * Updates the given squares index in the playableSquares array with a 1 
	 * @param row the row of the square
	 * @param col the column of the square
	 */
	private void setSquareAsPlayable(int row, int col){
		this.playableSquares[row*8+col] = 1;
	}
	
	/**
	 * Resets all playable squares to unplayable.
	 * Loops through the playableSQuares array and sets each value to 0.
	 * Called by calcPlayableMoves() to ensure only the current correct squares are set as playable
	 */
	private void resetMoves(){
		for(int i=0;i<64;i++){
			this.playableSquares[i] = 0;
		}
	}
}
