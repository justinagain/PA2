package edu.buffalo.cse.cse486586.groupmessenger;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class Test2ClickListener implements OnClickListener {

    private static final String TAG = Test2ClickListener.class.getName();
    private Activity mActivity;
    
    public Test2ClickListener(Activity _a) {
    	mActivity = _a;
    }


    @Override
    public void onClick(View v) {
    	Log.v(TAG, "Registered the Test2Click");
    	String avd = Util.getPortNumber(mActivity);
    	new SequencerRequestClientTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 
    			createBroadcastMessageForTest(avd, SendOnClickListener.AVD_AWARE_SEQUENCE_ID.intValue()+"", avd + ":" + SendOnClickListener.AVD_AWARE_SEQUENCE_ID.intValue()+""));
	    SendOnClickListener.AVD_AWARE_SEQUENCE_ID.incrementAndGet();
    }
    	
    	
    private BroadcastMessage createBroadcastMessageForTest(String avd, String sequenceNumber, String message){
    	BroadcastMessage bm = BroadcastMessage.getTestTwoRequestBroadcastMessage();
    	bm.setAvd(avd);
    	bm.setAvdSequenceNumber(sequenceNumber);
    	bm.setMessageSize(message.length()+"");
    	bm.setMessage(message);
    	return bm;
    }

}
