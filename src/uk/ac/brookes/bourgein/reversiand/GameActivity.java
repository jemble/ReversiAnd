	package uk.ac.brookes.bourgein.reversiand;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
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
import android.preference.PreferenceManager;
import android.provider.ContactsContract.Contacts;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * The actual game activity where the player(s) play a game.
 * <h3>Activity Flow</h3>
 * The main flow of the activity is shown below:
 * <img src="http://dl.dropboxusercontent.com/u/11100831/GameActivityFlow.png" />
 * 
 * @author bourgein
 *
 */
public class GameActivity extends BaseActivity {
	/**Use with onCreateDialog to show back button specific dialog*/
	private static final int BACK_BUTTON_PRESS = 1;
	/**Use with onCreateDialog to show end of game specific dialog*/
	private static final int GAME_END = 2;
	/**Use with onCreateDialog to show settings option specific dialog*/
	private static final int QUIT_TO_SETTINGS = 3;
	/**Use with onCreateDialog to show highscore option specific dialog*/
	private static final int QUIT_TO_HIGHSCORE = 4;
	/**Amount to delay computer move delay*/
	private static final int COMP_DELAY = 1500;
	/**Stores the state of each game square*/ 
	private static int gameBoard[];
	/**Used to map the gameBoard array to the appropriate square in the GridView*/
	private ImageAdapter gridAdapter;
	/**Player one picture view*/
	private ImageView imgViewOne;
	/**Player two picture view*/
	private ImageView imgViewTwo;
	/**Game board view*/
	private GridView gridV;
	/**Player one score view*/
	private TextView player1Text;
	/**Player two score view*/
	private TextView player2Text;
	/**Player one name view*/
	private TextView playerOneNameTxt;
	/**Player one name view*/
	private TextView playerTwoNameTxt;
	/**Countdown timer view*/
	private TextView timerText;
	/**if the cpu is playing*/
	private boolean cpu;
	/**if it is a timed game*/
	private boolean isTimed;
	/**is the sound on*/
	private boolean soundOn;
	/**Player one name*/
	private String playerOneName;
	/**String of URI to player one picture*/
	private String pOneUriString;
	/**Player two name*/
	private String playerTwoName;
	/**String of URI to player two picture*/
	private String pTwoUriString;
	/**Represents west direction*/
	private static Direction west = new Direction(-1, 0);
	/**Represents east direction*/
	private static Direction east = new Direction(1, 0);
	/**Represents north direction*/
	private static Direction north = new Direction(0, -1);
	/**Represents south direction*/
	private static Direction south = new Direction(0, 1);
	/**Represents north east direction*/
	private static Direction northEast = new Direction(1, -1);
	/**Represents south east direction*/
	private static Direction southEast = new Direction(1, 1);
	/**Represents south west direction*/
	private static Direction southWest = new Direction(-1, 1);
	/**Represents north west direction*/
	private static Direction northWest = new Direction(-1, -1);
	/**player one object*/
	private static Player player1;
	/**player two object*/
	private static Player player2;
	/**current player object*/
	private static Player curPlayer;
	/**Player object who has won the game*/
	private static Player winner;
	/**All Directions representing points on a compass*/
	private ArrayList<Direction> dirsArList = new ArrayList<Direction>();
	/**Current column*/
	private int moveCol;
	/**Current row*/
	private int moveRow;
	/**Turn time count down*/
	private CountDownTimer countTimer;
	/**Amount each turn in seconds*/
	private int turnTime;
	/**Used to play the 30 second warning sound*/ 
	private SoundPool soundPool;
	/**Resource ID of the 30 second warning sound*/ 
	private int soundId;
	/**Current lowest score in the highscore database*/
	private int lowestScore;
	/**Used to access the shared settings*/
	SharedPreferences settings;
	
	
	/**
	 * Called when the activity is created. 
	 * <br />
	 * Calls setContentView to inflate the xml defined
	 * layout and setupGame(). Sets onClickListener for each grid square that calls doMove with the square touched. 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		setupGame();
		
		gridV.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				if (!curPlayer.isCpu){
					moveRow = arg2/8; //arg2 is the 1d array pos
					moveCol = arg2%8;
					doMove(moveRow,moveCol);
				}
			}
		});
		
		gridAdapter.notifyDataSetChanged();
	}

	/**
	 * Overrides the super method to show different dialogs for different scenarios
	 * @param id The type of dialog to show
	 */
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
					GameActivity.this.setResult(PlayerSelectActivity.MAIN_ACTIVITY_QUIT);
					GameActivity.this.finish();
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
					Intent settingsIntent = new Intent(getApplicationContext(),CustomPrefsActivity.class);
					startActivity(settingsIntent);
					GameActivity.this.finish();
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
					GameActivity.this.finish();
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
			restartBuilder.setMessage(winner.playerName + " wins! \nStart a new Game?");
			restartBuilder.setCancelable(true);
			restartBuilder.setPositiveButton("OK", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface restartDialog, int which) {
					if (isTimed){
						countTimer.cancel();
					}
					GameActivity.this.setupGame();
				}
			});
			
			restartBuilder.setNegativeButton("Quit", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface restartDialog, int which) {
					if (isTimed){
						countTimer.cancel();
					}
					GameActivity.this.setResult(PlayerSelectActivity.MAIN_ACTIVITY_QUIT);
					GameActivity.this.finish();
					
				}
			});
			return restartBuilder.create();
			//restartDialog.show();
		}
		return null;
	}
	
	/**
	 * Overrides super method to call showDialog(BACK_BUTTON_PRESS) 
	 * when back button is pressed
	 *  
	 */
	@Override
	public void onBackPressed(){
		
		showDialog(BACK_BUTTON_PRESS);
	}
	
	/**
	 * Setups the game. 
	 * Works by calling getSettings(), initialising class fields, 
	 * checking the parent Activity intent for whether the CPU is playing,
	 * setting player names and pictures. 
	 * Also starts the countdown timer if a timed game is being played.  
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
		gameBoard[4*8+3] = player1.playerNum;
		gameBoard[3*8+4] = player1.playerNum;
		gameBoard[3*8+3] = player2.playerNum;
		gameBoard[4*8+4] = player2.playerNum;

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
		player1.playerName = playerOneName;
        
        playerOneNameTxt.setText(playerOneName);
		calcScore(player1);
		calcScore(player2);
		player1Text.setText(player1.getScoreAsString());
		player2Text.setText(player2.getScoreAsString());
		
		if (cpu){
			playerTwoName = "CPU";
			imgViewTwo.setImageResource(R.drawable.stub);
		}
		
		player2.playerName = playerTwoName;
        playerTwoNameTxt.setText(playerTwoName);
        playerTwoNameTxt.setBackgroundColor(getResources().getColor(R.color.playerTwoColor));
		playerOneNameTxt.setBackgroundColor(getResources().getColor(R.color.background));
		
		player2.isCpu = cpu;
			
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
	 * Makes a move by the current player if is a valid move. 
	 * Called when player touches a valid square or the computer is due to (and can) play
	 * Calls endMove()
	 * @param rowToPlay the row to try and play
	 * @param colToPlay the column to try and play
	 */
	public void doMove(int rowToPlay, int colToPlay){
		if(curPlayer.isValidMove(rowToPlay, colToPlay)){
			flip(rowToPlay, colToPlay, curPlayer.playerNum, dirsArList);
			gridAdapter.notifyDataSetChanged();
			endMove();								
		}
	}
	
	/**
	 * Called after doMove() to setup the next go.
	 * If either player can go it swaps players over, changes colours, works out 
	 * each player's valid and best moves, restart timer if timed game
	 * If neither player has a valid move the winner is found and endGame(winner) is called.
	 */
	public void endMove(){
		if (player1.canGo || player2.canGo){
			
			curPlayer = (curPlayer.playerNum == 1) ? player2 : player1;
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
			
			if (curPlayer.isCpu){
				if (curPlayer.canGo){
					final Handler mHandler = new Handler();
					mHandler.postDelayed(rMakeCompMove, COMP_DELAY);
				}
				else{
					endMove();
				}			
			}
		}
		else {		
			winner = (player1.score>player2.score) ? player1 : player2;
			endGame(winner);
		}
		
	}
	
	/**
	 * Used to delay the computer moves without delaying the main UI thread
	 */
	private final Runnable rMakeCompMove = new Runnable(){

		@Override
		public void run() {	
			doMove(curPlayer.bestRow,curPlayer.bestCol);
		}
		
	};
	
	/**
	 * Called when the game is over.
	 * <br />
	 * Shows the end of game dialog. If the winner has a highscore calls updateHighscore().
	 * @param winner the winning player
	 */
	public void endGame(Player winner){
		if (winner.score > lowestScore && !winner.isCpu){
			updateHighscore(winner.playerName,winner.playerUriString,winner.score);
		}
		showDialog(GAME_END);
		
	}
	
	/**
	 * Calculates the score for the given Player object. 
	 * <br />
	 * Loops through the gameBoard array and increments a counter if the current player is on the square. Sets 
	 * the player score to the counter.
	 * @param player the Player to calculate the current score for
	 */
	public void calcScore(Player player){
		int score=0;
		int playerNum = player.playerNum;
		for (int i=0;i<8;i++){
			if(gameBoard[i]==playerNum){
				score++;
			}
		}
		player.score = score;
	}
	
	/**
	 * Works out where what directions the player's pieces should be flipped in.
	 * <br />
	 * The algorithm used is as follows:
	 * <img src="https://dl.dropboxusercontent.com/u/11100831/howFlipWorks.png" />
	 * The actual changing of the grid data structure is done by doFlip()
	 * @param row starting board row
	 * @param col starting board column 
	 * @param player the current player
	 * @param dirs the direction arraylist
	 */
	public static void flip(int row, int col, int player,
			ArrayList<Direction> dirs) {

		int otherPlayer = (player == 1) ? player2.playerNum : player1.playerNum; // set enemy

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
	 * Does the actual changing of the board grid data structure.
	 * <br /> 
	 * Called by flip()
	 * @param dir the direction to flip in
	 * @param row the starting row
	 * @param col the starting column
	 * @param player the current player
	 */
	public static void doFlip(Direction dir, int row, int col, Player player) {
		int playerNum = player.playerNum;
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
	 * Plays the 30 second warning sound
	 * <br />
	 * Uses an AudioManager object to get the system volume and plays the sound resource mp3 using that volume
	 */
	public void playSound(){
		AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		float volume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		soundPool.play(soundId, volume, volume, 1, 0, 1f);
	}
	
	/**
	 * Queries the highscore db to get the current lowest score as an int
	 * <br />
	 * Uses a Cursor object to query the HighscoreProvider to find the lowest score by using the SQL command min().
	 * @return the current lowest score
	 */
	public int getLowestHighscore(){
		int lowScore=0;
		String[] proj = new String[] {"min("+HighscoreProvider.KEY_SCORE+")"};
		ContentResolver cr = getContentResolver();
		Cursor c = cr.query(HighscoreProvider.CONTENT_URI, proj, null, null, HighscoreProvider.KEY_SCORE+" DESC");
		if (c.moveToFirst()){
			lowScore = c.getInt(0);
		}
		return lowScore;
	}
	
	/**
	 * Finds the InputStream for a contact photo based on the given string.
	 * <br />
	 * Uses a Cursor object to query the contacts content provider to get a byte array 
	 * which is then used to return a new ByteArrayInputStream
	 * @param photoUriString represents a photo URI as a string 
	 * @return Stream to the contact photo if found or null 
	 */
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
	
	/**
	 * Sets the player picture. 
	 * Uses either the InputStream found using openPhoto() or the default 'stub' resource if the InputStream is null.
	 * @param uriString represents a photo URI as a string 
	 * @param imgVw the ImageView to set with the picture
	 */
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
	
	/**
	 * Gets the settings store in shared preferences.
	 * <br />
	 * Only gets player two settings if the device is not controlling that player. Stores found preferences in the class fields as appropriate.
	 */
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
	 * Updates highscore SQLite db with provided values.
	 * <br />
	 * Note it first deletes the lowest highscore in the table to ensure that only 10 rows are stored at a time
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
	
	/**
	 * Handles presses on option menus items.
	 * Overrides the BaseActivity version to allow for different in-game behaviour.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()) {
		case R.id.home:
			showDialog(BACK_BUTTON_PRESS);
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
