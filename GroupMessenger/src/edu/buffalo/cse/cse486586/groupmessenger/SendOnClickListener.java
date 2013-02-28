package edu.buffalo.cse.cse486586.groupmessenger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;


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
    private final Uri mUri;
    private final Activity mActivity;

    public SendOnClickListener(Activity _a, TextView _tv, TextView _metv){
        mEditTextView = _metv;
    	mTextView = _tv;
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
        sendToClients(messageText);
	}

	private void sendToClients(String messageText) {
		GroupMessengerContentProvider.URI_ID++;
		Log.v(TAG, "Creating BroadcastMessage and setting values");
		BroadcastMessage bm = BroadcastMessage.getRequestBroadcaseMessage();
		bm.setAvd(Util.getPortNumber(mActivity));
		bm.setAvdSequenceNumber(GroupMessengerContentProvider.URI_ID + "");
		bm.setMessageSize(messageText.length() + "");
		bm.setMessage(messageText);
		Log.v(TAG, "BroadcastMessage created");
		new ClientTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, bm);
	}

	private class ClientTask extends AsyncTask<BroadcastMessage, Void, Void>{
		protected Void doInBackground(BroadcastMessage... msgs){
			try {
				Log.v(TAG, "About to push to socket: " + Constants.SEQUENCER);
				Socket writeSocket = new Socket(Constants.IP_ADDRESS, Constants.SEQUENCER);
				writeSocket.getOutputStream().write(msgs[0].getPayload());
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
