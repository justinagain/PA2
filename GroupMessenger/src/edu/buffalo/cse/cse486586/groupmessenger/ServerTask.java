package edu.buffalo.cse.cse486586.groupmessenger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

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
	private ArrayList<BroadcastMessage> bmList = new ArrayList<BroadcastMessage>();
	
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
				Log.v(INFO_TAG, "Socket awaits accept ... ");
				socket = serverSocket.accept();
				Log.v(INFO_TAG, "A message is coming in ... ");
				InputStream stream = socket.getInputStream();
				byte[] data = new byte[BroadcastMessage.MSG_SIZE];
				int count = stream.read(data);				
				Log.v(INFO_TAG, "Message recieved with bytes: " + count);
				//GroupMessengerActivity.RECEVIED_COUNTER++;
				BroadcastMessage bm = BroadcastMessage.createMessageFromByteArray(data);
				if(bm.isRequestBroadcast()){
					Log.v(INFO_TAG, "A broadcast request has been received.");
				}
				else{
					Log.v(INFO_TAG, "A broadcast has been received.");					
				}
				socket.close();
				publishProgress(bm.getAvd()+":"+bm.getAvdSequenceNumber()+":"+bm.getMessage());
			}
		}
		catch (IOException e){
			Log.v(INFO_TAG, "IOException creating ServerSocket");
		}
		return null;
	}
	
	protected void onProgressUpdate(String... strings){
		TextView textView = (TextView)mActivity.findViewById(R.id.textView1);
		textView.append(strings[0] + "\n");
		return;
	}
	
}