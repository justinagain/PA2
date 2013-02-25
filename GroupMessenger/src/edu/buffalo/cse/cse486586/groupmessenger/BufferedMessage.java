package edu.buffalo.cse.cse486586.groupmessenger;

import android.content.ContentValues;

public class BufferedMessage {

	private ContentValues contentValues;
	private int key;
	
	public BufferedMessage(int key, ContentValues contentValues){
		this.contentValues = contentValues;
		this.key = key;
	}

	public int getKey(){return key;}
	public ContentValues getContentValues(){return contentValues;}
	
}
