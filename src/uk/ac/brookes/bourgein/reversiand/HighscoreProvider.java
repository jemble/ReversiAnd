package uk.ac.brookes.bourgein.reversiand;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
/**
 * Defines a content provider to provide access to the highscores table.
 * @author bourgein
 *
 */
public class HighscoreProvider extends ContentProvider {
	public static final String AUTHORITY = "uk.ac.brookes.bourgein.highscores";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/highscores");
	
	private SQLiteDatabase highscoreDB;
	private static final String HIGHSCORES_TABLE = "highscores";
	public static final String KEY_ID = "_id";
	public static final String KEY_NAME = "name";
	public static final String KEY_PICURI = "picuri";
	public static final String KEY_SCORE = "score";
	public static final int NAME_COLUMN = 1;
	public static final int PICURI_COLUMN = 2;
	public static final int SCORE_COLUMN = 3;
	
	private static final int HIGHSCORES = 1;
	private static final int PERSON_ID = 2;
	private static final UriMatcher uriMatcher;

	private static final String DATABASE_NAME = "highscores.db";
	private static final int DATABASE_VERSION = 1;
	
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(AUTHORITY, "highscores", HIGHSCORES);
		uriMatcher.addURI(AUTHORITY, "highscores/#", PERSON_ID);
	}
	
	@Override
	public int delete(Uri uri, String where, String[] selectionArgs) {
		int matchResult = uriMatcher.match(uri);
		String newWhere = makeNewWhere(where, uri, matchResult);
		
		if (matchResult == PERSON_ID || matchResult == HIGHSCORES){
			int count = highscoreDB.delete(HIGHSCORES_TABLE, newWhere, selectionArgs);
			getContext().getContentResolver().notifyChange(uri, null);
			return count;
		}
		else{
			throw new IllegalArgumentException("Unknown URI: "+uri);
		}
	}

	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)){
		case HIGHSCORES:
			return "vnd.android.cursor.dir/vnd.brookes.highscores";
		case PERSON_ID:
			return "vnd.android.cursor.item/vnd.brookes.highscores";
		default:
			throw new IllegalArgumentException("Unsupported URI: "+uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		long rowID = highscoreDB.insert(HIGHSCORES_TABLE,KEY_NAME,values);
		if (rowID > 0){
			Uri newuri = ContentUris.withAppendedId(CONTENT_URI, rowID);
			getContext().getContentResolver().notifyChange(newuri, null);
			return newuri;
		}
		else{
			throw new SQLException("Failed to insert row: " +rowID);
		}
	}

	@Override
	public boolean onCreate() {
		HighscoresDatabaseHelper helper = new HighscoresDatabaseHelper(
				this.getContext(), 
				DATABASE_NAME, 
				null, DATABASE_VERSION);
		this.highscoreDB = helper.getWritableDatabase();
		return (highscoreDB != null);
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(HIGHSCORES_TABLE);
		
		if(uriMatcher.match(uri)==PERSON_ID){
			qb.appendWhere(KEY_ID+"="+uri.getPathSegments().get(1));
		}
		
		//note the "10" at the end to limit the query to 10 rows
		Cursor c = qb.query(highscoreDB, projection, selection, selectionArgs, null, null, sortOrder,"10");
		c.setNotificationUri(getContext().getContentResolver(), uri);
		
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String where,
			String[] selectionArgs) {
		int matchResult = uriMatcher.match(uri);
		String newWhere = makeNewWhere(where,uri,matchResult);
		if(matchResult == PERSON_ID || matchResult == HIGHSCORES){
			int count = highscoreDB.update(HIGHSCORES_TABLE, values, newWhere, selectionArgs);
			getContext().getContentResolver().notifyChange(uri, null);
			return count;
		}
		else{
			throw new IllegalArgumentException("Unknown URI: "+uri);
		}
	}
	
	private String makeNewWhere(String where, Uri uri, int matchResult) {

	    if (matchResult != PERSON_ID) {
		     return where;
		  } else {
		     String newWhereSoFar = KEY_ID + "=" + uri.getPathSegments().get(1);
		     if (TextUtils.isEmpty(where))
	              return newWhereSoFar;
		      else
		            return newWhereSoFar + " AND (" + where + ')';
		  }
	}

	
	private static class HighscoresDatabaseHelper extends SQLiteOpenHelper {
	     public HighscoresDatabaseHelper(Context context, String name,
			              CursorFactory factory, int version) {
	           super(context, name, factory, version);
	      }

	      @Override 
	       public void onCreate(SQLiteDatabase db) {
	            db.execSQL("CREATE TABLE " + HIGHSCORES_TABLE + " (" 
	            						  + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " 
	                                      + KEY_NAME + " TEXT," 
	                                      + KEY_PICURI + " TEXT,"
	                                      + KEY_SCORE + " INTEGER);");
	         }
	        @Override
	         public void onUpgrade(SQLiteDatabase db, int   oldVersion, int newVersion)     
	         {
	                db.execSQL("DROP TABLE IF EXISTS " + HIGHSCORES_TABLE);
		  onCreate(db);
	          }
	}


}
