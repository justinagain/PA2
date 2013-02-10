package edu.buffalo.cse.cse486586.groupmessenger;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GroupMessengerSQLiteOpenHelper extends SQLiteOpenHelper {
	
	public static final String DATABASE_NAME = "GROUP_MESSENGER";
	public static final int DATABASE_VERSION = 2;
	
	
	GroupMessengerSQLiteOpenHelper(Context context) {
        // calls the super constructor, requesting the default cursor factory.
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    /**
    *
    * Creates the underlying database with table name and column names taken from the
    * NotePad class.
    */
   @Override
   public void onCreate(SQLiteDatabase db) {
       db.execSQL("CREATE TABLE " + GroupMessengerContentProvider.MESSAGES_TABLE + " ("
               + GroupMessengerContentProvider._ID + " INTEGER PRIMARY KEY,"
               + GroupMessengerContentProvider.MESSAGE_COLUMN + " TEXT"
               + ");");
   }


	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

}
