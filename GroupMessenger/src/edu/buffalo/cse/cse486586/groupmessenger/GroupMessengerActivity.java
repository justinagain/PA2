package edu.buffalo.cse.cse486586.groupmessenger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import edu.buffalo.cse.cse486586.groupmessenger.R;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.app.Activity;
import android.content.ContentValues;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class GroupMessengerActivity extends Activity {

	// The tag info
	final static String INFO_TAG = "Project 2 Info: ";
	final static String TRY_CATCH_ERROR = "Try / Catch Error: ";
	
	public static int[] CURRENT_STATE = new int[]{0,0,0};
	private ArrayList<BufferedMessage> bufferedMessages = new ArrayList<BufferedMessage>();

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_messenger);

        TextView textView = (TextView) findViewById(R.id.textView1);
        textView.setMovementMethod(new ScrollingMovementMethod());
        findViewById(R.id.button1).setOnClickListener(
                new OnPTestClickListener(textView, getContentResolver()));
        createServerSocket();
		Button sendButton = (Button) findViewById(R.id.button4);
		TextView editTextView = (TextView)findViewById(R.id.editText1);
		sendButton.setOnClickListener(new SendOnClickListener(this, textView, editTextView, getContentResolver()));
	}

	private void createServerSocket() {
		try{
			ServerSocket serverSocket = new ServerSocket(10000);
	        Uri uri = buildUri("content", "edu.buffalo.cse.cse486586.groupmessenger.provider");
			new ServerTask(this, uri).executeOnExecutor(
					AsyncTask.THREAD_POOL_EXECUTOR, 
					serverSocket);
		}
		catch(IOException e){
			Log.v(TRY_CATCH_ERROR, "Exception creating ServerSocket");
		}
	}
	
    private Uri buildUri(String scheme, String authority) {
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.authority(authority);
        uriBuilder.scheme(scheme);
        return uriBuilder.build();
    }

	
	private class ServerTask extends AsyncTask<ServerSocket, String, Void>{
		
		private Activity mActivity;
		private Uri mUri;
		
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
					Log.v(INFO_TAG, "About to have socket accept");
					socket = serverSocket.accept();
					Log.v(INFO_TAG, "A message is coming in ... ");
					BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					msg = in.readLine();
					ContentValues cv = createContentValues(msg);
					Log.v(INFO_TAG, "My values are: " + CURRENT_STATE[0] + " " + CURRENT_STATE[1] + " " + CURRENT_STATE[2]);
					try {
						Log.v(INFO_TAG, "About to post to content resolver where: " + cv.get(OnPTestClickListener.KEY_FIELD) + ":" + 
								cv.get(OnPTestClickListener.VALUE_FIELD));
						mActivity.getContentResolver().acquireContentProviderClient("edu.buffalo.cse.cse486586.groupmessenger.provider").insert(mUri, cv);
						Log.v(INFO_TAG, "Posted to content resolver");
					} catch (RemoteException e) {
						Log.v(INFO_TAG, "Error posting to content resolver");
						e.printStackTrace();
					}
					Log.v(INFO_TAG, "The message is: " + msg);
					publishProgress(msg);
					socket.close();
				}
			}
			catch (IOException e){
				Log.v(TRY_CATCH_ERROR, "IOException creating ServerSocket");
			}
			return null;
		}
		
		private ContentValues createContentValues(String msg) {
			ContentValues contentValues = new ContentValues();
			String[] possibleKeys = msg.split(":");
			String avd = possibleKeys[0];
			String key1 = possibleKeys[1];
			String key2 = possibleKeys[2];
			String key3 = possibleKeys[3];
			contentValues.put(OnPTestClickListener.KEY_FIELD, key1);
			String message = "";
			for(int i = 4; i < possibleKeys.length; i++){
				message = message + possibleKeys[i];
			}
			contentValues.put(OnPTestClickListener.VALUE_FIELD, message);
			bufferedMessages.add(new BufferedMessage(Integer.parseInt(key1), contentValues));
			CURRENT_STATE[0] = Integer.parseInt(key1);
			CURRENT_STATE[1] = Integer.parseInt(key2);
			CURRENT_STATE[2] = Integer.parseInt(key3);			
			Log.v(INFO_TAG, "Received values are: " + avd + " " + key1 + " " + key2 + " " + key3 + " " + message);
			Log.v(INFO_TAG, "Updated CURRENT_STATE to: " + CURRENT_STATE[0] + " " + CURRENT_STATE[1] + " " + CURRENT_STATE[2]);
			return contentValues;
		}

		protected void onProgressUpdate(String... strings){
			TextView textView = (TextView)findViewById(R.id.textView1);
			textView.append(strings[0] + "\n");
			return;
		}
		
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_group_messenger, menu);
        return true;
    }
}
