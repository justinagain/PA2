package edu.buffalo.cse.cse486586.groupmessenger;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class GroupMessengerContentProvider extends ContentProvider {

	public final static String _ID = "_ID";
	public final static String MESSAGE_COLUMN = "MESSAGE";
	public final static String MESSAGES_TABLE = "MESSAGES";
	
	public static Uri CONTENT_URI = Uri.parse("content://edu.buffalo.cse.cse486586.groupmessenger.provider");

	
    GroupMessengerSQLiteOpenHelper groupMessengerSQLiteOpenHelper;
    
	public GroupMessengerContentProvider(){
		//groupMessengerDatabase = SQLiteDatabase.openOrCreateDatabase(GROUP_MESSENGER_DATABASE_NAME, CursorFactory.this)
	}
	
	@Override
	public Cursor query(Uri arg0, String[] arg1, String arg2, String[] arg3,
			String arg4) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri groupMessengerUri, ContentValues contentValues) {
		Log.v(GroupMessengerActivity.INFO_TAG, "MADE IT TO THE CONTENT PROVIDER!!!");
		// Opens the database object in "write" mode.
        SQLiteDatabase db = groupMessengerSQLiteOpenHelper.getWritableDatabase();

        // Performs the insert and returns the ID of the new note.
        long rowId = db.insert(
            MESSAGES_TABLE, 
            MESSAGE_COLUMN,  
            contentValues
        );

        Uri newURI = null;
        if (rowId > 0) {
            newURI = ContentUris.withAppendedId(CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(newURI, null);
        }		
		return newURI;
	}
	
	/**
	 *  Do not need to implement any of the following per assignment directions.
	 */
	
	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCreate() {
        groupMessengerSQLiteOpenHelper = new GroupMessengerSQLiteOpenHelper(getContext());
		return false;
	}

	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return 0;
	}

}
