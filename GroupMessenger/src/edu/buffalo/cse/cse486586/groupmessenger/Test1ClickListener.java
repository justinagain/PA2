package edu.buffalo.cse.cse486586.groupmessenger;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

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

public class Test1ClickListener implements OnClickListener {

    private static final String TAG = Test1ClickListener.class.getName();
    private static final int TEST_CNT = 50;
    public static final String KEY_FIELD = "key";
    public static final String VALUE_FIELD = "value";
    private Activity mActivity;
    
    public Test1ClickListener(Activity _a) {
    	mActivity = _a;
    }


    /**
     * When the button is clicked, it should create a thread that multicasts 5 messages in sequence. 
     * Multicasting of one message should be followed by 3 seconds sleep of the thread. This is just 
     * to make sure that we can spread messages from different emulator instances.
     * 
     * The message format should be “<AVD name>:<sequence number>”. <AVD name> is the emulator instance’s 
     * name, e.g., avd0, avd1, and avd2. <sequence number> is a number starting from 0 and increasing 
     * by 1 for each message. For example, if your first emulator instance multicasts 5 messages, 
     * then the messages should be “avd0:0”, “avd0:1”, “avd0:2”, “avd0:3”, and “avd0:4”.
     * 
     */
    @Override
    public void onClick(View v) {
    	Log.v(TAG, "Registered the Test1Click");
    	String avd = Util.getPortNumber(mActivity);
    	ArrayList<BroadcastMessage> broadcastMessageList = new ArrayList<BroadcastMessage>();
    	broadcastMessageList.add(createBroadcastMessageForTest(avd, "0", avd + ":" + "0"));
    	broadcastMessageList.add(createBroadcastMessageForTest(avd, "1", avd + ":" + "1"));
    	broadcastMessageList.add(createBroadcastMessageForTest(avd, "2", avd + ":" + "2"));
    	broadcastMessageList.add(createBroadcastMessageForTest(avd, "3", avd + ":" + "3"));
    	broadcastMessageList.add(createBroadcastMessageForTest(avd, "4", avd + ":" + "4"));
    	
    	new Test1Task().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 
    			broadcastMessageList.get(0),
    			broadcastMessageList.get(1),
    			broadcastMessageList.get(2),
    			broadcastMessageList.get(3),
    			broadcastMessageList.get(4));
       }

    
    private BroadcastMessage createBroadcastMessageForTest(String avd, String sequenceNumber, String message){
    	BroadcastMessage bm = BroadcastMessage.getRequestBroadcaseMessage();
    	bm.setAvd(avd);
    	bm.setAvdSequenceNumber(sequenceNumber);
    	bm.setMessageSize(message.length()+"");
    	bm.setMessage(message);
    	return bm;
    }

}
