package edu.buffalo.cse.cse486586.groupmessenger;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.AsyncTask;
import android.util.Log;

public class Test1Task extends AsyncTask<BroadcastMessage, Void, Void>{

	private static final String TAG = Test1Task.class.getName();
	
	protected Void doInBackground(BroadcastMessage... msgs){
		try {
			for (int i = 0; i < msgs.length; i++) {
				Log.v(TAG, "About to push to socket: " + Constants.SEQUENCER);
				Socket writeSocket = new Socket(Constants.IP_ADDRESS, Constants.SEQUENCER);
				writeSocket.getOutputStream().write(msgs[i].getPayload());
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