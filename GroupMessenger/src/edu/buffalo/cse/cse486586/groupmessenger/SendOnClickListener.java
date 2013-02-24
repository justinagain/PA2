package edu.buffalo.cse.cse486586.groupmessenger;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
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
//    private final ContentValues[] mContentValues;

    public SendOnClickListener(TextView _tv, TextView _metv, ContentResolver _cr){
        mEditTextView = _metv;
    	mTextView = _tv;
        mContentResolver = _cr;
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
		Log.v(TAG, "Registered the 'send' click with text: " + mEditTextView.getText());
	}

}
