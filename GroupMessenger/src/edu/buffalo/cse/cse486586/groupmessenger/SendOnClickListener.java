package edu.buffalo.cse.cse486586.groupmessenger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;


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
    
    public static AtomicInteger AVD_AWARE_SEQUENCE_ID = new AtomicInteger(0);

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
		Log.v(TAG, "Creating BroadcastMessage and setting values");
		BroadcastMessage bm = BroadcastMessage.getRequestBroadcaseMessage();
		bm.setAvd(Util.getPortNumber(mActivity));
		bm.setAvdSequenceNumber(AVD_AWARE_SEQUENCE_ID.intValue() + "");
		AVD_AWARE_SEQUENCE_ID.incrementAndGet();
		bm.setMessageSize(messageText.length() + "");
		bm.setMessage(messageText);
		Log.v(TAG, "BroadcastMessage created for " + Util.getPortNumber(mActivity));
		Log.v(TAG, "avdAwareSequenceNumber is: " + AVD_AWARE_SEQUENCE_ID.intValue());
		new SequencerRequestClientTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, bm);
	}
	
}
