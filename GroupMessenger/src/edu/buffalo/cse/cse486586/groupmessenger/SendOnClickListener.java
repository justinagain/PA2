package edu.buffalo.cse.cse486586.groupmessenger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class SendOnClickListener implements OnClickListener {

	private static final String TAG = SendOnClickListener.class.getName();
    public static final String KEY_FIELD = "key";
    public static final String VALUE_FIELD = "value";

    private final TextView mTextView;
    private final TextView mEditTextView;
    private final ContentResolver mContentResolver;
    private final Uri mUri;
    private final Activity mActivity;
//    private final ContentValues[] mContentValues;

    public SendOnClickListener(Activity _a, TextView _tv, TextView _metv, ContentResolver _cr){
        mEditTextView = _metv;
    	mTextView = _tv;
        mContentResolver = _cr;
        mActivity = _a;
        mUri = buildUri("content", "edu.buffalo.cse.cse486586.groupmessenger.provider");
        Log.v(TAG, "Build URI: " + mUri.toString());
    }
    
    private Uri buildUri(String scheme, String authority) {
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.authority(authority);
        uriBuilder.scheme(scheme);
        return uriBuilder.build();
    }
    
	@Override
	public void onClick(View arg0) {
		String messageText = mEditTextView.getText().toString();
		Log.v(TAG, "Registered the 'send' click with text: " + messageText);
        ContentValues cv = new ContentValues();
        cv.put(KEY_FIELD, "key" + GroupMessengerContentProvider.URI_ID);
        cv.put(VALUE_FIELD, messageText);
		try {
			Log.v(TAG, "About to add new ContentValue with: " + cv.get(KEY_FIELD) + " AND " + cv.get(VALUE_FIELD));
			mContentResolver.acquireContentProviderClient("edu.buffalo.cse.cse486586.groupmessenger.provider").insert(mUri, cv);
			sendToClients(mUri, cv);
			GroupMessengerContentProvider.URI_ID++;
		} catch (RemoteException e) {
			Log.v(TAG, "Failed to add to ContentProvide: " + e.getMessage());
			e.printStackTrace();
		}

	}

	private void sendToClients(Uri mUri2, ContentValues cv) {
		String[] pushPorts = Constants.AVD_REMOTE_CLIENTS;
		for(int i = 0; i < pushPorts.length; i++){
			Log.v(TAG, "ContentValues string is (lets hope it is parsable): " + cv.toString());
			String key = (String)cv.get(KEY_FIELD);
			String value = (String)cv.get(VALUE_FIELD);
			String message =  Util.getPortNumber(mActivity) + ":" + key + ":" + value; 
			new ClientTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, pushPorts[i], message);
		}
	}

	private class ClientTask extends AsyncTask<String, Void, Void>{
		protected Void doInBackground(String... msgs){
			try {
				Log.v(TAG, "About to push to socket: " + msgs[0]);
				Socket writeSocket = new Socket(Constants.IP_ADDRESS, Integer.parseInt(msgs[0]));
				writeSocket.getOutputStream().write(msgs[1].getBytes());
				writeSocket.getOutputStream().flush();
				writeSocket.close();
				Log.v(TAG, "Pushed!");
			} catch (UnknownHostException e) {
				e.printStackTrace();
				Log.v(TAG, "Error creating Inet Address");
			} catch (IOException e) {
				Log.v(TAG, "Error creating Socket");
				e.printStackTrace();
			}
			return null;
		}
	}

	
	
}
