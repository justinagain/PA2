package edu.buffalo.cse.cse486586.groupmessenger;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.AsyncTask;
import android.util.Log;

public class BroadcastRequestClientTask extends AsyncTask<BroadcastMessage, Void, Void> {
	
	private static final String TAG = BroadcastRequestClientTask.class.getName();
	
	protected Void doInBackground(BroadcastMessage... msgs){
		try {
			int [] clients = Constants.AVD_REMOTE_CLIENTS;
			for (int i = 0; i < clients.length; i++) {
				Log.v(TAG, "About to push to socket: " + clients[i]);
				Socket writeSocket = new Socket(Constants.IP_ADDRESS, clients[i]);
				writeSocket.getOutputStream().write(msgs[0].getPayload());
				writeSocket.getOutputStream().flush();
				writeSocket.close();
				Log.v(TAG, "Pushed!");				
			}
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
