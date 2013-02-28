package edu.buffalo.cse.cse486586.groupmessenger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.content.ContentValues;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

public class ServerTask extends AsyncTask<ServerSocket, String, Void>{
	final static String INFO_TAG = "Project 2 Info: ";
	private Activity mActivity;
	private Uri mUri;
	
	ServerTask(Activity activity, Uri uri){
		mActivity = activity;
		mUri = uri;
	}
	
	@Override
	protected Void doInBackground(ServerSocket... sockets) {
		Log.v(INFO_TAG, "Create a socket");
		String msg = null;
		ServerSocket serverSocket = sockets[0];
		Socket socket;
		try{
			while(true){					
				Log.v(INFO_TAG, "About to have socket accept");
				socket = serverSocket.accept();
				Log.v(INFO_TAG, "A message is coming in ... ");
				InputStream stream = socket.getInputStream();
				byte[] data = new byte[BroadcastMessage.MSG_SIZE];
				int count = stream.read(data);				
				Log.v(INFO_TAG, "Message recieved with bytes: " + count);
				
//				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//				msg = in.readLine();
//				Log.v(INFO_TAG, "The message is " + msg);
//				String[] possibleKeys = msg.split(":");
//				String avd = possibleKeys[0];
//				String messageId = possibleKeys[1];
//				String message = "";
//				for(int i = 2; i < possibleKeys.length; i++){
//					message = message + possibleKeys[i];
//				}
//				Log.v(INFO_TAG, "Stash a message - hold it back!");
				GroupMessengerActivity.RECEVIED_COUNTER++;
				publishProgress("Received");
				socket.close();
			}
		}
		catch (IOException e){
			Log.v(INFO_TAG, "IOException creating ServerSocket");
		}
		return null;
	}
	
	private ContentValues createContentValues(String msg) {
		ContentValues contentValues = new ContentValues();
		String[] possibleKeys = msg.split(":");
		String avd = possibleKeys[0];
		String key1 = possibleKeys[1];
		contentValues.put(OnPTestClickListener.KEY_FIELD, key1);
		String message = "";
		for(int i = 2; i < possibleKeys.length; i++){
			message = message + possibleKeys[i];
		}
		contentValues.put(OnPTestClickListener.VALUE_FIELD, message);
		Log.v(INFO_TAG, "Received values are: " + avd + " " + key1 + " " + message);
		return contentValues;
	}

	protected void onProgressUpdate(String... strings){
		TextView textView = (TextView)mActivity.findViewById(R.id.textView1);
		textView.append(strings[0] + "\n");
		return;
	}
	
}