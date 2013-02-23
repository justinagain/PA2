package edu.buffalo.cse.cse486586.groupmessenger;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class GroupMessengerContentProvider extends ContentProvider {

    private static final String TAG = OnPTestClickListener.class.getName();
	
	public final static String _ID = "_ID";	
	public static Uri CONTENT_URI = Uri.parse("content://edu.buffalo.cse.cse486586.groupmessenger.provider");
	private static int URI_ID = 0;

	public GroupMessengerContentProvider(){
	}
	
	@Override
	public Cursor query(Uri arg0, String[] arg1, String arg2, String[] arg3,
			String arg4) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri groupMessengerUri, ContentValues contentValues) {
		Log.v(GroupMessengerActivity.INFO_TAG, "About to insert into content provider with URI: " + groupMessengerUri.toString());
        writeToInternalStorage(groupMessengerUri, contentValues);
        getContext().getContentResolver().notifyChange(groupMessengerUri, null);
		return groupMessengerUri;
	}
	
	
	private boolean writeToInternalStorage(Uri uri, ContentValues contentValues){
		boolean success = false;
		FileOutputStream fos;
		try {
			Log.v(GroupMessengerActivity.INFO_TAG, "About to insert into a speicific file");
			String keyValue = contentValues.get(OnPTestClickListener.KEY_FIELD).toString();
			String contentValue = contentValues.get(OnPTestClickListener.VALUE_FIELD).toString();

			String fileName = uri.toString().replace("content://", "");
			fileName = fileName + "_" + keyValue;
			fos = this.getContext().openFileOutput(fileName, Context.MODE_PRIVATE);
			fos.write(contentValue.getBytes());				
			fos.close();
			success = true;
			Log.v(TAG, "Wrote ContentValues successfully.");
		} catch (FileNotFoundException e) {
			Log.v(TAG, "File not found when writing ContentValues");
			e.printStackTrace();
		} catch (IOException e) {
			Log.v(TAG, "Some IO Exception when writing ContentValues");
			e.printStackTrace();
		}
		return success;
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
		return false;
	}

	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return 0;
	}

}
