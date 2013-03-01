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
import android.os.RemoteException;
import android.util.Log;
import android.widget.TextView;

public class ServerTask extends AsyncTask<ServerSocket, String, Void>{
	final static String INFO_TAG = ServerTask.class.getName();
	private Activity mActivity;
	private Uri mUri;

	private AtomicInteger sequencerNumber = new AtomicInteger(0);
	HashMap<String, ArrayList<BroadcastMessage>> bufferedSequencerMessages = new HashMap<String, ArrayList<BroadcastMessage>>();
	HashMap<String, AtomicInteger> globalSequencerNumbers = new HashMap<String, AtomicInteger>();
	AtomicInteger broadcastRecieptNumbers = new AtomicInteger(1);
	ArrayList<BroadcastMessage> broadcastRecieptBuffer = new ArrayList<BroadcastMessage>();
	
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
					processBroadcastRequest(bm, BroadcastMessage.BROADCAST);
				}
				else if(bm.isBroadcast()){
					Log.v(INFO_TAG, "A broadcast has been received.");					
					processBroadcastReceipt(bm);
				}
				else if(bm.isTestTwoRequestBroadcast()){
					Log.v(INFO_TAG, "TestTwo case has been received.");					
					processBroadcastRequest(bm, BroadcastMessage.TEST_TWO_BROADCAST);					
				}				
				else if(bm.isTestTwoBroadcast()){
					Log.v(INFO_TAG, "A TestTwo broadcast has been received.");					
					processBroadcastReceipt(bm);
					// Call it once
					createTestTwoGenericBroadcastRequest();					
					// Call it twice
					createTestTwoGenericBroadcastRequest();					
				}

				socket.close();
			}
		}
		catch (IOException e){
			Log.v(INFO_TAG, "IOException creating ServerSocket");
		}
		return null;
	}

	private void createTestTwoGenericBroadcastRequest() {
		Log.v(INFO_TAG, "Creating BroadcastMessage and setting values");
		BroadcastMessage testTwoRequestBroadcast = BroadcastMessage.getRequestBroadcaseMessage();
		String avd = Util.getPortNumber(mActivity);
		testTwoRequestBroadcast.setAvd(avd);
		testTwoRequestBroadcast.setAvdSequenceNumber(SendOnClickListener.AVD_AWARE_SEQUENCE_ID.intValue() + "");
		int id = SendOnClickListener.AVD_AWARE_SEQUENCE_ID.intValue();
		SendOnClickListener.AVD_AWARE_SEQUENCE_ID.incrementAndGet();
		String message = avd + ":" + id;
		testTwoRequestBroadcast.setMessageSize(message.length() + "");
		testTwoRequestBroadcast.setMessage(message);
		Log.v(INFO_TAG, "BroadcastMessage created for " + avd);
		Log.v(INFO_TAG, "avdAwareSequenceNumber is: " + SendOnClickListener.AVD_AWARE_SEQUENCE_ID.intValue());
		new SequencerRequestClientTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, testTwoRequestBroadcast);
	}

	private void processBroadcastRequest(BroadcastMessage bm, String type) {
		//set bm as send message
		bm.setType(type);
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
		//publishProgress(bm.getAvd() + ":" + bm.getAvdSequenceNumber() + ":" + bm.getMessage());
		// If the received number is equal to the expect, then
		if(bm.getAvdSequenceNumber() == broadcastRecieptNumbers.intValue()){
			publish(bm);
			if(broadcastRecieptBuffer.size() != 0){
				//See if others can be published as well
				Collections.sort(broadcastRecieptBuffer);
				// Iterate over it
				for (BroadcastMessage broadcastMessage : broadcastRecieptBuffer) {
					// If the largest sent sequenceID is one less than the current, sorted queue value, then send it.
					if(broadcastMessage.getAvdSequenceNumber() == broadcastRecieptNumbers.intValue() + 1){
						publish(broadcastMessage);
						// Incrment it 
						int incrementedValue = broadcastRecieptNumbers.incrementAndGet();
						Log.v(INFO_TAG, "Inrementing to: " + incrementedValue);
						// Remove it
						broadcastRecieptBuffer.remove(broadcastMessage);
					}
					else{
						Log.v(INFO_TAG, "We have hit a gap, we need to break where: " + broadcastRecieptNumbers.intValue() + ":" + broadcastMessage.getAvdSequenceNumber());
						break;
					}
				}

				
			}
		}
		else{
			Log.v(INFO_TAG, "Buffereing where bm.getAvdSequenceNumber()=" + bm.getAvdSequenceNumber() + " and broadcastRecieptNumbers.intValue()=" + broadcastRecieptNumbers.intValue());
			broadcastRecieptBuffer.add(bm);
		}
	}

	private void publish(BroadcastMessage bm) {
		//1. publish it to the content provider
		try {
			Log.v(INFO_TAG, "Attempt to publish to ContentProvider");
			mActivity.getContentResolver().acquireContentProviderClient("edu.buffalo.cse.cse486586.groupmessenger.provider").insert(mUri, bm.getAsContentValue());
			Log.v(INFO_TAG, "Successful publish to ContentProvider");
		} catch (RemoteException e) {
			Log.v(INFO_TAG, "Error publishing to ContentProvider");
			e.printStackTrace();
		}
		//2. Increment the counter
		int newVal = broadcastRecieptNumbers.incrementAndGet();
		Log.v(INFO_TAG, "Upon publish, broadcastRecieptNumbers=" + newVal);
		//3. publish to the screen
		publishProgress((String)bm.getAsContentValue().get(OnPTestClickListener.VALUE_FIELD));
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