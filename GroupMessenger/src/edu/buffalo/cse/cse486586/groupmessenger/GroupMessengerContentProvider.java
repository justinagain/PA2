package edu.buffalo.cse.cse486586.groupmessenger;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public class GroupMessengerContentProvider extends ContentProvider {

	private HashMap<Uri, ContentValues> hashMap;
	
	public static Uri CONTENT_URI = Uri.parse("content://edu.buffalo.cse.cse486586.groupmessenger.provider");

	public GroupMessengerContentProvider(){
		hashMap = new HashMap<Uri, ContentValues>();
	}
	
	@Override
	public Cursor query(Uri arg0, String[] arg1, String arg2, String[] arg3,
			String arg4) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri groupMessengerUri, ContentValues contentValues) {
		String uriString = groupMessengerUri.toString();
		uriString += uriString + "/" + Constants.ID_VALUE_COUNTER;
		Constants.ID_VALUE_COUNTER++;
		Uri newURI = null;
		newURI = Uri.parse(uriString);
		hashMap.put(newURI, contentValues);			
		return newURI;
	}
	
	/**
	 *  Do not implement any of the following per assignment directions.
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return 0;
	}

}
