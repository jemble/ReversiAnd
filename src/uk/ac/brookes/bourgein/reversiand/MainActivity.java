package uk.ac.brookes.bourgein.reversiand;

import java.util.ArrayList;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import android.widget.GridView;

public class MainActivity extends Activity {
	private static int gameBoard[][] = new int[8][8];
	private int gameBoard1d[] = new int[64];
	private ImageAdapter gridAdapter;
	private GridView gridV;
	private TextView player1Text;
	private TextView player2Text;

	static Direction west = new Direction(-1, 0);
	static Direction east = new Direction(1, 0);
	static Direction north = new Direction(0, -1);
	static Direction south = new Direction(0, 1);
	static Direction northEast = new Direction(1, -1);
	static Direction southEast = new Direction(1, 1);
	static Direction southWest = new Direction(-1, 1);
	static Direction northWest = new Direction(-1, -1);

	static Player player1 = new Player(1);
	static Player player2 = new Player(2);

	static int gameCount = 0;
	ArrayList<Direction> dirsArList = new ArrayList<Direction>();
	int moveCol, moveRow;
	static Player curPlayer = player1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		dirsArList.add(east);
		dirsArList.add(west);
		dirsArList.add(north);
		dirsArList.add(south);
		dirsArList.add(northEast);
		dirsArList.add(southEast);
		dirsArList.add(southWest);
		dirsArList.add(northWest);

		gameBoard[4][3] = player1.getPlayerNum();
		gameBoard[3][4] = player1.getPlayerNum();
		gameBoard[3][3] = player2.getPlayerNum();
		gameBoard[4][4] = player2.getPlayerNum();

		player1.setPlayableSquare(4, 3);
		player1.setPlayableSquare(3, 4);
		player2.setPlayableSquare(3, 3);
		player2.setPlayableSquare(4, 4);

		gridAdapter = new ImageAdapter(this,gameBoard1d);
		gridV = (GridView) findViewById(R.id.gameGrid);
		gridV.setAdapter(gridAdapter);

		player1.calcPlayableMoves(gameBoard, dirsArList);
		player2.calcPlayableMoves(gameBoard, dirsArList);
		
		player1Text = (TextView)findViewById(R.id.player1Text);
		player2Text = (TextView)findViewById(R.id.player2Text);
		calcScore(player1);
		calcScore(player2);
		player1Text.setText(player1.getScoreAsString());
		player2Text.setText(player2.getScoreAsString());
		
		gridV.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				moveRow = arg2/8; //arg2 is the 1d array pos
				moveCol = arg2%8;
				
				if(curPlayer.isValidMove(moveRow, moveCol)){
					flip(moveRow, moveCol, curPlayer.getPlayerNum(), dirsArList);
					curPlayer = (curPlayer.getPlayerNum() == 1) ? player2 : player1;
					player1.calcPlayableMoves(gameBoard, dirsArList);
					player2.calcPlayableMoves(gameBoard, dirsArList);
					
					set1Darray();
					calcScore(player1);
					calcScore(player2);
					gridAdapter.notifyDataSetChanged();
					player1Text.setText(player1.getScoreAsString());
					player2Text.setText(player2.getScoreAsString());
				}
			}
		});
		
		set1Darray();
		gridAdapter.notifyDataSetChanged();

	}

	public int get2dInd(int i) {
		return gameBoard[i / 8][i % 8];
	}
	
	public void set1Darray(){
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				gameBoard1d[i * 8 + j] = gameBoard[i][j];
			}
		}
	}
	
	public void calcScore(Player player){
		int score=0;
		for (int i=0;i<8;i++){
			for (int j=0;j<8;j++){
				if(gameBoard[i][j]==player.getPlayerNum()){
					score++;
				}
			}
		}
		player.setScore(score);
	}
	public static void flip(int row, int col, int player,
			ArrayList<Direction> dirs) {

		int otherPlayer = (player == 1) ? player2.getPlayerNum() : player1
				.getPlayerNum(); // set enemy

		for (Direction direction : dirs) { // loop through all directions

			int dirCol = direction.getCol();
			int dirRow = direction.getRow();

			// check we're inside teh board
			if ((row + dirRow) < 8 && (row + dirRow) >= 0 && (col + dirCol) < 8
					&& (col + dirCol) >= 0) {

				if (gameBoard[row + dirRow][col + dirCol] == otherPlayer) { // check
																			// +1
																			// in
																			// the
																			// direction
																			// we're
																			// using
																			// is
																			// the
																			// other
																			// player
					int move = 2;

					// while we're still on the board. N.b we multiply by the
					// dirRow and dirCol.
					while ((row + (move * dirRow)) < 8
							&& (row + (move * dirRow)) >= 0
							&& (col + (move * dirCol)) < 8
							&& (col + (move * dirCol)) >= 0) {
						// if we end up on one of our own pieces
						if (gameBoard[(row + (move * dirRow))][(col + (move * dirCol))] == player) {
							doFlip(direction, row, col, curPlayer);
						}
						move++;
					}
				}
			}
		}
	}

	public static void doFlip(Direction dir, int row, int col, Player player) {
		int playerNum = player.getPlayerNum();
		int dirRow = dir.getRow();
		int dirCol = dir.getCol();
		gameBoard[row][col] = playerNum;
		
		row += dirRow;
		col += dirCol;
		while (gameBoard[row][col] != playerNum) {
			gameBoard[row][col] = playerNum;
			
			row += dirRow;
			col += dirCol;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
