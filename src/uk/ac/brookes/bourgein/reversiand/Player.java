package uk.ac.brookes.bourgein.reversiand;

import uk.ac.brookes.bourgein.reversiand.Direction;

import java.util.ArrayList;

public class Player {
	private int[][] playableSquares;
	private int playerNum;
	private String playerName;
	private String playerUriString;
	private int score;
	private int bestRow;
	private int bestCol;
	private boolean canGo;
	private boolean isCpu;
	
	public Player(int player){
		playableSquares = new int[8][8];
		this.playerNum = player;
		score = 0;
	}
	
	public int getPlayerNum(){
		return playerNum;
	}
	
	public void setScore(int score){
		this.score = score;
	}
	
	public int getScore(){
		return score;
	}
	
	public String getScoreAsString(){
		return Integer.toString(score);
	}
	
	public void setPlayableSquare(int row,int col){
		this.playableSquares[row][col]=1;
	}
	
	public void setBestSquare(int row, int col){
		this.bestRow = row;
		this.bestCol = col; 
	}
	
	public void setIsCpu(boolean isCpu){
		this.isCpu = isCpu;
	}
	
	public boolean getIsCpu(){
		return this.isCpu;
	}
	
	public boolean getCanGo(){
		return this.canGo;
	}
	
	public int getBestRow(){
		return this.bestRow;
	}
	
	public int getBestCol(){
		return this.bestCol;
	}
	
	public void setPlayerName(String name){
		this.playerName = name;
	}
	
	public String getPlayerName(){
		return this.playerName;
	}
	
	public void setPlayerUriString(String uri){
		this.playerUriString = uri;
	}
	
	public String getPlayerUriString(){
		return this.playerUriString;
	}
	
	public String getBestSquare(){
		return "Player "+this.playerNum+" best square is row: "+this.bestRow + " col: "+this.bestCol;
	}
	
	public void calcPlayableMoves(int[][] currentGameBoard, ArrayList<Direction> dirs){
		resetMoves();
		int moveCount=0;
		int otherPlayer = (this.playerNum == 1) ? 2 : 1;
		for (int row = 0;row<8;row++){
			for (int col = 0; col<8;col++){
				
				if (currentGameBoard[row][col] == 0){
					for (Direction direction : dirs){
						
						int dirCol = direction.getCol();
						int dirRow = direction.getRow();
						
						if ((row+dirRow) < 8 
								&& (row+dirRow) >= 0
								&& (col+dirCol) < 8
								&& (col+dirCol) >= 0){
							
							if (currentGameBoard[row+dirRow][col+dirCol] == otherPlayer){ //check +1 in the direction we're using is the other player
								int move = 2;
								
								//while we're still on the board. N.b we multiply by the dirRow and dirCol.
								while ((row+(move*dirRow)) < 8
										&& (row+(move*dirRow)) >=0
										&& (col+(move*dirCol)) < 8
										&& (col+(move*dirCol))>=0)
								{
									//if we end up on one of our own pieces
									if (currentGameBoard[(row+(move*dirRow))][(col+(move*dirCol))]==this.playerNum){
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
	
	public void calcBestMove(int[][] currentGameBoard, ArrayList<Direction> dirs){
		int otherPlayer = (this.playerNum == 1) ? 2 : 1;
		int bestScore = 0;
		for (int row=0;row<8;row++){
			for (int col=0;col<8;col++){
				int tempScore = 0;
				if (playableSquares[row][col]==1){
					for (Direction dir : dirs){
						int dirCol = dir.getCol();
						int dirRow = dir.getRow();
						
						if ((row + dirRow) < 8 && (row + dirRow) >= 0 && (col + dirCol) < 8
								&& (col + dirCol) >= 0) {
							if (currentGameBoard[row + dirRow][col + dirCol] == otherPlayer) {
								int move = 2;

								// while we're still on the board. N.b we multiply by the
								// dirRow and dirCol.
								while ((row + (move * dirRow)) < 8
										&& (row + (move * dirRow)) >= 0
										&& (col + (move * dirCol)) < 8
										&& (col + (move * dirCol)) >= 0) {
									// if we end up on one of our own pieces
									if (currentGameBoard[(row + (move * dirRow))][(col + (move * dirCol))] == this.playerNum) {
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
					this.setBestSquare(row, col);
				}
			}
		}
	}
	
	public boolean isValidMove(int row, int col){
		if(this.playableSquares[row][col] == 1){
			return true;
		}
		else {
			return false;
		}
	}
	
	private void setSquareAsPlayable(int row, int col){
		playableSquares[row][col] = 1;
	}
	
	private void resetMoves(){
		for(int i=0;i<8;i++){
			for (int j=0;j<8;j++){
				this.playableSquares[i][j] = 0;
			}
		}
	}
}
