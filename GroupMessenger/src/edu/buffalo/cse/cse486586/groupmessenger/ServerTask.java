package edu.buffalo.cse.cse486586.groupmessenger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import android.app.Activity;
import android.content.ContentValues;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

public class ServerTask extends AsyncTask<ServerSocket, String, Void>{
	final static String INFO_TAG = ServerTask.class.getName();
	private Activity mActivity;
	private Uri mUri;

	private AtomicInteger sequencerNumber = new AtomicInteger(0);
	HashMap<String, ArrayList<BroadcastMessage>> bufferedSequencerMessages = new HashMap<String, ArrayList<BroadcastMessage>>();
	HashMap<String, AtomicInteger> globalSequencerNumbers = new HashMap<String, AtomicInteger>();
	
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
		initializeSequencer();
		
		try{
			while(true){					
				Log.v(INFO_TAG, "Socket awaits accept ... ");
				socket = serverSocket.accept();
				Log.v(INFO_TAG, "A message is coming in ... ");
				InputStream stream = socket.getInputStream();
				byte[] data = new byte[BroadcastMessage.MSG_SIZE];
				int count = stream.read(data);				
				Log.v(INFO_TAG, "Message recieved with bytes: " + count);
				BroadcastMessage bm = BroadcastMessage.createMessageFromByteArray(data);
				if(bm.isRequestBroadcast()){
					Log.v(INFO_TAG, "A broadcast request has been received.");
					processBroadcastRequest(bm);
				}
				else{
					Log.v(INFO_TAG, "A broadcast has been received.");					
					processBroadcastReceipt(bm);
				}
				socket.close();
			}
		}
		catch (IOException e){
			Log.v(INFO_TAG, "IOException creating ServerSocket");
		}
		return null;
	}

	private void processBroadcastRequest(BroadcastMessage bm) {
		//set bm as send message
		bm.setType(BroadcastMessage.BROADCAST);
		String avdName = bm.getAvd();
		int avdAwareSequenceId =  bm.getAvdSequenceNumber();
		AtomicInteger largestSentSequenceId = globalSequencerNumbers.get(avdName);
		if(avdAwareSequenceId - largestSentSequenceId.intValue() == 0 ){
			broadcastMessage(bm, avdName, largestSentSequenceId);
		} else {
			bufferMessage(bm, avdName);
		}
	}

	private void bufferMessage(BroadcastMessage bm, String avdName) {
		Log.v(INFO_TAG, "Message came in: Processed a broadcast request that must be buffered");
		publishProgress("Message came in: " + bm.getAvd() + ":" + bm.getAvdSequenceNumber());
		bufferedSequencerMessages.get(avdName).add(bm);
	}

	private void broadcastMessage(BroadcastMessage bm, String avdName,
			AtomicInteger largestSentSequenceId) {
		Log.v(INFO_TAG, "Processed a broadcast request that is ready to be shipped out to all: no need to buffer");
		int newValue = globalSequencerNumbers.get(avdName).incrementAndGet();
		Log.v(INFO_TAG, "Incremented to: " + newValue);
		if(bufferedSequencerMessages.get(avdName).size() == 0){
			sequencerNumber.incrementAndGet();
			Log.v(INFO_TAG, "Inrementing the global counter to: " + sequencerNumber.intValue());
			bm.setAvdSequenceNumber(sequencerNumber.intValue() + "");
			//push immediately
			new BroadcastRequestClientTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, bm);
		}
		else{
			// There is stuff on the queue to push - go get it
			ArrayList<BroadcastMessage> bmbroadcastMessages = bufferedSequencerMessages.get(avdName);
			// Add what you have just seen
			bmbroadcastMessages.add(bm);
			// Sort it
			Collections.sort(bmbroadcastMessages);
			// Iterate over it
			for (BroadcastMessage broadcastMessage : bmbroadcastMessages) {
				// If the largest sent sequenceID is one less than the current, sorted queue value, then send it.
				if(broadcastMessage.getAvdSequenceNumber() == largestSentSequenceId.intValue() + 1){
					// Send it!
					new BroadcastRequestClientTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, bm);
					// Incrment it 
					int incrementedValue = largestSentSequenceId.incrementAndGet();
					Log.v(INFO_TAG, "Inrementing to: " + incrementedValue);
					// Remove it
					bmbroadcastMessages.remove(broadcastMessage);
					broadcastMessage.setAvdSequenceNumber(sequencerNumber.intValue() + "");
				}
				else{
					Log.v(INFO_TAG, "We have hit a gap, we need to break where: " + largestSentSequenceId.intValue() + ":" + broadcastMessage.getAvdSequenceNumber());
					break;
				}
			}
		}
	}

	private void processBroadcastReceipt(BroadcastMessage bm) {
		Log.v(INFO_TAG, "Processing a BroadcastReceipt");
		publishProgress(bm.getAvd() + ":" + bm.getAvdSequenceNumber() + ":" + bm.getMessage());
	}

	private void initializeSequencer() {
		sequencerNumber.set(0);
		
		bufferedSequencerMessages.put(Constants.AVD0, new ArrayList<BroadcastMessage>());
		bufferedSequencerMessages.put(Constants.AVD1, new ArrayList<BroadcastMessage>());
		bufferedSequencerMessages.put(Constants.AVD2, new ArrayList<BroadcastMessage>());
		
		globalSequencerNumbers.put(Constants.AVD0, new AtomicInteger(0));
		globalSequencerNumbers.put(Constants.AVD1, new AtomicInteger(0));
		globalSequencerNumbers.put(Constants.AVD2, new AtomicInteger(0));
	}
	
	protected void onProgressUpdate(String... strings){
		TextView textView = (TextView)mActivity.findViewById(R.id.textView1);
		textView.append(strings[0] + "\n");
		return;
	}
	
}