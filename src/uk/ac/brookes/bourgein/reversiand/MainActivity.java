	package uk.ac.brookes.bourgein.reversiand;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.ContactsContract.Contacts;
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

public class MainActivity extends BaseActivity {
	private static final int BACK_BUTTON_PRESS = 1;
	private static final int GAME_END = 2;
	private static final int QUIT_TO_SETTINGS = 3;
	private static final int QUIT_TO_HIGHSCORE = 4;
	private static final int COMP_DELAY = 1500;
	private static int gameBoard[];
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
	private boolean isTimed;
	private boolean soundOn;
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
	private static Player winner;
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
					
					if (!curPlayer.getIsCpu()){
						moveRow = arg2/8; //arg2 is the 1d array pos
						moveCol = arg2%8;
						doMove(moveRow,moveCol);
					}
				}
			});
			
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
					if (isTimed){
						countTimer.cancel();
					}
					//this is needed to make sure we go back to the home screen
					MainActivity.this.setResult(PlayerSelectActivity.MAIN_ACTIVITY_QUIT);
					MainActivity.this.finish();
				}
			});
			
			builder.setNegativeButton("Cancel", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
			return builder.create();
			
		case QUIT_TO_SETTINGS:
			Builder settingsBuilder = new AlertDialog.Builder(this);
			settingsBuilder.setMessage("Are you sure you want to quit to go to Settings?");
			settingsBuilder.setCancelable(true);
			settingsBuilder.setPositiveButton("OK", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface settingsDialog, int which) {					
					if (isTimed){
						countTimer.cancel();
					}
					Intent settingsIntent = new Intent(getApplicationContext(),CustomPrefs.class);
					startActivity(settingsIntent);
					MainActivity.this.finish();
				}
			});
			
			settingsBuilder.setNegativeButton("Cancel", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface settingsDialog, int which) {
					settingsDialog.cancel();
				}
			});
			return settingsBuilder.create();
			
		case QUIT_TO_HIGHSCORE:
			Builder highscoreBuilder = new AlertDialog.Builder(this);
			highscoreBuilder.setMessage("Are you sure you want to quit to go to Highscores?");
			highscoreBuilder.setCancelable(true);
			highscoreBuilder.setPositiveButton("OK", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface highscoreDialog, int which) {					
					if (isTimed){
						countTimer.cancel();
					}
					Intent highscoreIntent = new Intent(getApplicationContext(),HighscoreActivity.class);
					startActivity(highscoreIntent);
					MainActivity.this.finish();
				}
			});
			
			highscoreBuilder.setNegativeButton("Cancel", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface highscoreDialog, int which) {
					highscoreDialog.cancel();
				}
			});
			return highscoreBuilder.create();
			
		case GAME_END:
			Builder restartBuilder = new AlertDialog.Builder(this);
			restartBuilder.setMessage(winner.getPlayerName() + " wins! \nStart a new Game?");
			restartBuilder.setCancelable(true);
			restartBuilder.setPositiveButton("OK", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface restartDialog, int which) {
					if (isTimed){
						countTimer.cancel();
					}
					MainActivity.this.setupGame();
				}
			});
			
			restartBuilder.setNegativeButton("Quit", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface restartDialog, int which) {
					if (isTimed){
						countTimer.cancel();
					}
					MainActivity.this.setResult(PlayerSelectActivity.MAIN_ACTIVITY_QUIT);
					MainActivity.this.finish();
					
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
	
	/**
	 * setups the game
	 */
	public void setupGame(){
		gameBoard = new int[64];
		
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
		imgViewTwo = (ImageView)findViewById(R.id.imgPlayerTwo);
		playerOneNameTxt = (TextView)findViewById(R.id.playerOneName);
		playerTwoNameTxt = (TextView)findViewById(R.id.playerTwoName);
		
		getSettings();
		
		
		curPlayer = player1;
		gameBoard[4*8+3] = player1.getPlayerNum();
		gameBoard[3*8+4] = player1.getPlayerNum();
		gameBoard[3*8+3] = player2.getPlayerNum();
		gameBoard[4*8+4] = player2.getPlayerNum();

		Intent intent = getIntent();
		cpu = intent.getBooleanExtra("Cpu",false);
		
		if (soundOn){
			soundPool = new SoundPool(1,AudioManager.STREAM_MUSIC,100);
			soundId = soundPool.load(this,R.raw.alarm, 1);
		}
		
		gridAdapter = new ImageAdapter(this,gameBoard);
		gridV = (GridView) findViewById(R.id.gameGrid);
		gridV.setAdapter(gridAdapter);
		
		gridAdapter.notifyDataSetChanged();

		player1.calcPlayableMoves(gameBoard, dirsArList);
		player2.calcPlayableMoves(gameBoard, dirsArList);
		
		player1Text = (TextView)findViewById(R.id.player1Text);
		player2Text = (TextView)findViewById(R.id.player2Text);
		timerText = (TextView)findViewById(R.id.timerText);
		player1.setPlayerName(playerOneName);
        
        playerOneNameTxt.setText(playerOneName);
		calcScore(player1);
		calcScore(player2);
		player1Text.setText(player1.getScoreAsString());
		player2Text.setText(player2.getScoreAsString());
		
		if (cpu){
			playerTwoName = "CPU";
			imgViewTwo.setImageResource(R.drawable.stub);
		}
		
		player2.setPlayerName(playerTwoName);
        playerTwoNameTxt.setText(playerTwoName);
		
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
		
		if (isTimed){
			countTimer = new CountDownTimer((turnTime*1000), 1000) {
			
		     public void onTick(long millisUntilFinished) {
		         timerText.setText(Long.toString(millisUntilFinished/1000));
		         if (millisUntilFinished/1000 == 30){
		        	 if(soundOn){
		        		 playSound();
		        	 }
		        	 timerText.setBackgroundColor(getResources().getColor(R.color.background));
		         }
		         if (millisUntilFinished/1000 == 5){
		        	 timerText.setTextSize(40);
		         }
		     }

		     public void onFinish() {
		    	 countTimer.cancel();
		    	 if (curPlayer == player1){
		    		 winner = player2;
		    	 }
		    	 else {
		    		 winner = player1;
		    	 }
		    	 endGame(winner);
		     }
		  }.start();
		}
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
		if (player1.getCanGo() || player2.getCanGo()){
			curPlayer = (curPlayer.getPlayerNum() == 1) ? player2 : player1;
			player1.calcPlayableMoves(gameBoard, dirsArList);
			player2.calcPlayableMoves(gameBoard, dirsArList);
			player1.calcBestMove(gameBoard, dirsArList);
			player2.calcBestMove(gameBoard, dirsArList);
	
			calcScore(player1);
			calcScore(player2);
			player1Text.setText(player1.getScoreAsString());
			player2Text.setText(player2.getScoreAsString());
			if (curPlayer == player1){
				playerTwoNameTxt.setBackgroundColor(getResources().getColor(R.color.playerTwoColor));
				playerOneNameTxt.setBackgroundColor(getResources().getColor(R.color.background));
			}
			else{
				playerOneNameTxt.setBackgroundColor(getResources().getColor(R.color.playerOneColor));
				playerOneNameTxt.setTextColor(getResources().getColor(R.color.playerOneTextColor));
				playerTwoNameTxt.setBackgroundColor(getResources().getColor(R.color.background));
			}
			if (isTimed){
				timerText.setTextSize(24);
				countTimer.start();
			}
			
			if (curPlayer.getIsCpu()){
				final Handler mHandler = new Handler();
				mHandler.postDelayed(rMakeCompMove, COMP_DELAY);
			}
		}
		else {		
			winner = (player1.getScore()>player2.getScore()) ? player1 : player2;
			endGame(winner);
		}
		
	}
	
	/**
	 * Used to delay the computer moves without freezing program
	 */
	private final Runnable rMakeCompMove = new Runnable(){

		@Override
		public void run() {	
			doMove(curPlayer.getBestRow(),curPlayer.getBestCol());
		}
		
	};
	
	/**
	 * Called when the game is over
	 * @param winner the winning player
	 */
	public void endGame(Player winner){
		if (winner.getScore() > lowestScore && !winner.getIsCpu()){
			updateHighscore(winner.getPlayerName(),winner.getPlayerUriString(),winner.getScore());
		}
//		Toast endToast = Toast.makeText(getApplicationContext(), winner.getPlayerName()+" wins!", Toast.LENGTH_SHORT);
//		endToast.show();
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
				if(gameBoard[i*8+j]==player.getPlayerNum()){
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

				if (gameBoard[(row + dirRow) *8 + (col + dirCol)] == otherPlayer) { // check +1 in the direction we're using is the other player
					int move = 2;

					// while we're still on the board. N.b we multiply by the
					// dirRow and dirCol.
					while ((row + (move * dirRow)) < 8
							&& (row + (move * dirRow)) >= 0
							&& (col + (move * dirCol)) < 8
							&& (col + (move * dirCol)) >= 0) {
						// if we end up on one of our own pieces
						if (gameBoard[(row + (move * dirRow))* 8 +(col + (move * dirCol))] == player) {
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
		gameBoard[row*8+col] = playerNum;
		
		row += dirRow;
		col += dirCol;
		while (gameBoard[row*8+col] != playerNum) {
			gameBoard[row*8+col] = playerNum;
			row += dirRow;
			col += dirCol;
		}
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
//			Toast scoreT = Toast.makeText(getApplicationContext(), Integer.toString(lowScore), Toast.LENGTH_SHORT);
//			scoreT.show();
		}
		return lowScore;
	}
	
	public InputStream openPhoto(String photoUriString) {
	    
	     Uri photoUri = Uri.parse(photoUriString);
	     Cursor cursor = getContentResolver().query(photoUri,
	          new String[] {Contacts.Photo.PHOTO}, null, null, null);
	     if (cursor == null) {
	         return null;
	     }
	     try {
	         if (cursor.moveToFirst()) {
	             byte[] data = cursor.getBlob(0);
	             if (data != null) {
	                 return new ByteArrayInputStream(data);
	             }
	         }
	     } finally {
	         cursor.close();
	     }
	     return null;
	 }
	
	public void setLayout(String uriString, ImageView imgVw){
		if (uriString != null){
			InputStream stream  = openPhoto(uriString);
			if (stream != null){
				Bitmap bitmap = BitmapFactory.decodeStream(stream);
				imgVw.setImageBitmap(bitmap);
			}
			else {
				imgVw.setImageResource(R.drawable.stub);
			}
		}
		else {
			imgVw.setImageResource(R.drawable.stub);
		}
	}
	
	public void getSettings(){
		settings = PreferenceManager.getDefaultSharedPreferences(this);
		turnTime = settings.getInt("seconds", 60);
		
		playerOneName = settings.getString("playerOneName", "Player 1");     
        pOneUriString = settings.getString("playerOnePic", null);
        setLayout(pOneUriString,imgViewOne);
        
        if (!cpu){
        	playerTwoName = settings.getString("playerTwoName", "Player 2");
        	pTwoUriString = settings.getString("playerTwoPic",null);
            setLayout(pTwoUriString,imgViewTwo);
        }
        
        isTimed = settings.getBoolean("timed", false);
        soundOn = settings.getBoolean("sounds", false);
	}
	
	/**
	 * updates highscore SQLite db with provided values
	 * @param name the player name
	 * @param uriString the uri of the player image as a string
	 * @param score the player score
	 */
	public void updateHighscore(String name, String uriString, int score){
		ContentResolver cr = getContentResolver();
		
		//Delete the lowest score
		//Perhaps change this to use query to get the rowID???
		cr.delete(HighscoreProvider.CONTENT_URI, HighscoreProvider.KEY_ID + "= (SELECT " + HighscoreProvider.KEY_ID + " FROM highscores ORDER BY " + HighscoreProvider.KEY_SCORE + " ASC LIMIT 1)", null);
		
		ContentValues values = new ContentValues();
		values.put(HighscoreProvider.KEY_NAME, name);
		values.put(HighscoreProvider.KEY_PICURI, uriString);
		values.put(HighscoreProvider.KEY_SCORE, score);
		cr.insert(HighscoreProvider.CONTENT_URI, values);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()) {
		case R.id.home:
			showDialog(GAME_END);
			return true;
		case R.id.settings:
			showDialog(QUIT_TO_SETTINGS);
			return true;
		case R.id.highscores:
			showDialog(QUIT_TO_HIGHSCORE);
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
