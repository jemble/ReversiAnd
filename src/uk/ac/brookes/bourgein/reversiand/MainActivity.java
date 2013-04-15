	package uk.ac.brookes.bourgein.reversiand;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final int BACK_BUTTON_PRESS = 1;
	private static final int GAME_END = 2;
	private static int gameBoard[][];
	private int gameBoard1d[];
	private ImageAdapter gridAdapter;
	private ImageView imgViewOne;
	private ImageView imgViewTwo;
	private GridView gridV;
	private TextView player1Text;
	private TextView player2Text;
	private TextView playerOneNameTxt;
	private TextView playerTwoNameTxt;
	private TextView timerText;
	private boolean cpu;
	private String playerOneName;
	private String pOneUriString;
	private String playerTwoName;
	private String pTwoUriString;
	static Direction west = new Direction(-1, 0);
	static Direction east = new Direction(1, 0);
	static Direction north = new Direction(0, -1);
	static Direction south = new Direction(0, 1);
	static Direction northEast = new Direction(1, -1);
	static Direction southEast = new Direction(1, 1);
	static Direction southWest = new Direction(-1, 1);
	static Direction northWest = new Direction(-1, -1);

	static Player player1;
	static Player player2;
	static Player curPlayer;
	ArrayList<Direction> dirsArList = new ArrayList<Direction>();
	int moveCol, moveRow;
	private CountDownTimer countTimer;
	private int turnTime;
	private SoundPool soundPool;
	private int soundId;
	private int lowestScore;
	
	SharedPreferences settings;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		setupGame();
		 
		if (player1.getCanGo() || player2.getCanGo()){
			gridV.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					
					moveRow = arg2/8; //arg2 is the 1d array pos
					moveCol = arg2%8;
					doMove(moveRow,moveCol);	
				}
			});
			
			set1Darray();
			gridAdapter.notifyDataSetChanged();
		}
		else{
			Toast toast2 = Toast.makeText(getApplicationContext(), "game over", Toast.LENGTH_LONG);
			toast2.show();
		}
	}

	@Override
	public Dialog onCreateDialog(int id){
		switch(id){
		case BACK_BUTTON_PRESS:
			Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Are you sure you want to quit?");
			builder.setCancelable(true);
			builder.setPositiveButton("OK", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					MainActivity.this.finish();
					countTimer.cancel();
				}
			});
			
			builder.setNegativeButton("Cancel", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
			return builder.create();
			//dialog.show();
		case GAME_END:
			Builder restartBuilder = new AlertDialog.Builder(this);
			restartBuilder.setMessage("Start a new Game?");
			restartBuilder.setCancelable(true);
			restartBuilder.setPositiveButton("OK", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface restartDialog, int which) {
					countTimer.cancel();
					MainActivity.this.setupGame();
				}
			});
			
			restartBuilder.setNegativeButton("Quit", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface restartDialog, int which) {
					MainActivity.this.finish();
					countTimer.cancel();
				}
			});
			return restartBuilder.create();
			//restartDialog.show();
		}
		return null;
	}
	
	@Override
	public void onBackPressed(){
		showDialog(BACK_BUTTON_PRESS);
	}
	
	public boolean onKey(View v,int keyCode, KeyEvent event){
		switch(keyCode){
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			Toast keyRightToast = Toast.makeText(getApplicationContext(), "right key", Toast.LENGTH_SHORT);
			keyRightToast.show();
			return true;
		}
		return false;
	}
	
	/**
	 * setups the game
	 */
	public void setupGame(){
		gameBoard = new int[8][8];
		gameBoard1d = new int[64];
		dirsArList.add(east);
		dirsArList.add(west);
		dirsArList.add(north);
		dirsArList.add(south);
		dirsArList.add(northEast);
		dirsArList.add(southEast);
		dirsArList.add(southWest);
		dirsArList.add(northWest);
		player2 = new Player(2);
		player1 = new Player(1);
		
		imgViewOne = (ImageView)findViewById(R.id.imgPlayerOne);
		playerOneNameTxt = (TextView)findViewById(R.id.playerOneName);
		playerTwoNameTxt = (TextView)findViewById(R.id.playerTwoName);
		
		settings = PreferenceManager.getDefaultSharedPreferences(this);
		turnTime = Integer.parseInt(settings.getString("turnTime", "30"));
		
		playerOneName = settings.getString("playerOneName", "Player 1");     
        pOneUriString = settings.getString("playerOnePic", null);
        if (pOneUriString != null){
        	Uri photoOne = Uri.parse(pOneUriString);    
            imgViewOne.setImageURI(photoOne);
            player1.setPlayerUriString(pOneUriString);
        }
        player1.setPlayerName(playerOneName);
        
        playerOneNameTxt.setText(playerOneName);
        
        imgViewTwo = (ImageView)findViewById(R.id.imgPlayerTwo);
        playerTwoName = settings.getString("playerTwoName", "Player 2");
        pTwoUriString = settings.getString("playerTwoPic",null);
        
        if(pTwoUriString != null){
        	Uri photoTwo = Uri.parse(pTwoUriString);
            imgViewTwo.setImageURI(photoTwo);
            player2.setPlayerUriString(pTwoUriString);
        }
        player2.setPlayerName(playerTwoName);
        playerTwoNameTxt.setText(playerTwoName);
        
		
		
		curPlayer = player1;
		gameBoard[4][3] = player1.getPlayerNum();
		gameBoard[3][4] = player1.getPlayerNum();
		gameBoard[3][3] = player2.getPlayerNum();
		gameBoard[4][4] = player2.getPlayerNum();

		Intent intent = getIntent();
		cpu = intent.getBooleanExtra("Cpu",false);
		
		soundPool = new SoundPool(1,AudioManager.STREAM_MUSIC,100);
		soundId = soundPool.load(this,R.raw.alarm, 1);

		gridAdapter = new ImageAdapter(this,gameBoard1d);
		gridV = (GridView) findViewById(R.id.gameGrid);
		gridV.setAdapter(gridAdapter);
		
		set1Darray();
		gridAdapter.notifyDataSetChanged();

		player1.calcPlayableMoves(gameBoard, dirsArList);
		player2.calcPlayableMoves(gameBoard, dirsArList);
		
		player1Text = (TextView)findViewById(R.id.player1Text);
		player2Text = (TextView)findViewById(R.id.player2Text);
		timerText = (TextView)findViewById(R.id.timerText);
		
		calcScore(player1);
		calcScore(player2);
		player1Text.setText(player1.getScoreAsString());
		player2Text.setText(player2.getScoreAsString());
		
		player2.setIsCpu(cpu);
		
		if (curPlayer == player1){
			playerTwoNameTxt.setBackgroundColor(0);
			playerOneNameTxt.setBackgroundColor(getResources().getColor(R.color.background));
		}
		else{
			playerOneNameTxt.setBackgroundColor(0);
			playerTwoNameTxt.setBackgroundColor(getResources().getColor(R.color.background));
		}
		lowestScore = getLowestHighscore();
		countTimer = new CountDownTimer((turnTime*1000), 1000) {

		     public void onTick(long millisUntilFinished) {
		         timerText.setText(Long.toString(millisUntilFinished/1000));
		         if (millisUntilFinished/1000 == 30){
		        	 playSound();
		        	 timerText.setBackgroundColor(getResources().getColor(R.color.background));
		         }
		         if (millisUntilFinished/1000 == 5){
		        	 timerText.setTextSize(40);
		         }
		     }

		     public void onFinish() {
		    	 countTimer.cancel();
		    	 Toast countToast = Toast.makeText(getApplicationContext(), "Time's up!", Toast.LENGTH_LONG);
		    	 countToast.show();
		    	 if (curPlayer == player1){
		    		 endGame(player2);
		    	 }
		    	 else {
		    		 endGame(player1);
		    	 }
		    	 
		    	 
		     }
		  }.start();
	}
	
	/**
	 * makes a move by the current player if it is valid. Called when player touches a square or the computer
	 * is due to play
	 * @param rowToPlay the row to try and play
	 * @param colToPlay the column to try and play
	 */
	public void doMove(int rowToPlay, int colToPlay){
		if(curPlayer.isValidMove(rowToPlay, colToPlay)){
			flip(rowToPlay, colToPlay, curPlayer.getPlayerNum(), dirsArList);
			gridAdapter.notifyDataSetChanged();
			endMove();								
		}
	}
	
	/**
	 * called after doMove() to setup the next go i.e. swap players over, change colours, work out 
	 * each player's valid and best moves, restart timer
	 */
	public void endMove(){
		curPlayer = (curPlayer.getPlayerNum() == 1) ? player2 : player1;
		player1.calcPlayableMoves(gameBoard, dirsArList);
		player2.calcPlayableMoves(gameBoard, dirsArList);
		player1.calcBestMove(gameBoard, dirsArList);
		player2.calcBestMove(gameBoard, dirsArList);
		set1Darray();
		calcScore(player1);
		calcScore(player2);
		player1Text.setText(player1.getScoreAsString());
		player2Text.setText(player2.getScoreAsString());
		if (curPlayer == player1){
			playerTwoNameTxt.setBackgroundColor(0);
			playerOneNameTxt.setBackgroundColor(getResources().getColor(R.color.background));
		}
		else{
			playerOneNameTxt.setBackgroundColor(0);
			playerTwoNameTxt.setBackgroundColor(getResources().getColor(R.color.background));
		}
		countTimer.start();
		
		if (curPlayer.getIsCpu()){
			doMove(curPlayer.getBestRow(),curPlayer.getBestCol());
		}
		
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
	
	/**
	 * Called when the game is over
	 * @param winner the winning player
	 */
	public void endGame(Player winner){
		if (winner.getScore() > lowestScore){
			updateHighscore(winner.getPlayerName(),winner.getPlayerUriString(),winner.getScore());
		}
		Toast endToast = Toast.makeText(getApplicationContext(), winner.getPlayerName()+" wins!", Toast.LENGTH_SHORT);
		endToast.show();
		showDialog(GAME_END);
		
	}
	
	/**
	 * works out the score for the given player
	 * @param player the Player to calculate the current score for
	 */
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
	
	/**
	 * works out where what directions the player's pieces should be flipped in. The actual changing of the 
	 * grid data structure is done by doFlip()
	 * @param row starting board row
	 * @param col starting board column 
	 * @param player the current player
	 * @param dirs the direction arraylist
	 */
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

				if (gameBoard[row + dirRow][col + dirCol] == otherPlayer) { // check +1 in the direction we're using is the other player
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

	/**
	 * Does the actual changing of the board grid data structure when a valid move has occured
	 * @param dir the direction to flip in
	 * @param row the starting row
	 * @param col the starting column
	 * @param player the current player
	 */
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
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()) {
		case R.id.menu_settings:
			Toast setToast = Toast.makeText(getApplicationContext(), "setting", Toast.LENGTH_SHORT);
			setToast.show();
			break;
		case R.id.about:
			Toast aboutToast = Toast.makeText(getApplicationContext(), "about", Toast.LENGTH_SHORT);
			aboutToast.show();
			break;
		case R.id.help:
			Toast helpToast = Toast.makeText(getApplicationContext(), "help", Toast.LENGTH_SHORT);
			helpToast.show();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * plays the 30 second warning sound
	 */
	public void playSound(){
		AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		float volume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		soundPool.play(soundId, volume, volume, 1, 0, 1f);
	}
	
	/**
	 * Queries the highscore db to get the current lowest score as an int
	 * @return the current lowest score
	 */
	public int getLowestHighscore(){
		int lowScore=0;
		String[] proj = new String[] {"min("+HighscoreProvider.KEY_SCORE+")"};
		ContentResolver cr = getContentResolver();
		Cursor c = cr.query(HighscoreProvider.CONTENT_URI, proj, null, null, HighscoreProvider.KEY_SCORE+" DESC");
		if (c.moveToFirst()){
			lowScore = c.getInt(0);
			Toast scoreT = Toast.makeText(getApplicationContext(), Integer.toString(lowScore), Toast.LENGTH_SHORT);
			scoreT.show();
		}
		return lowScore;
	}
	
	/**
	 * updates highscore SQLite db with provided values
	 * @param name the player name
	 * @param uriString the uri of the player image as a string
	 * @param score the player score
	 */
	public void updateHighscore(String name, String uriString, int score){
		ContentResolver cr = getContentResolver();
		ContentValues values = new ContentValues();
		values.put(HighscoreProvider.KEY_NAME, name);
		values.put(HighscoreProvider.KEY_PICURI, uriString);
		values.put(HighscoreProvider.KEY_SCORE, score);
		cr.insert(HighscoreProvider.CONTENT_URI, values);
	}

}
