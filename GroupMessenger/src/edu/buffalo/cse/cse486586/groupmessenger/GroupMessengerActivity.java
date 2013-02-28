package edu.buffalo.cse.cse486586.groupmessenger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
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
	final static String TAG = "Project 2 Info: ";
	public static int RECEVIED_COUNTER = 0;
	
	
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
		sendButton.setOnClickListener(new SendOnClickListener(this, textView, editTextView));
	}

	private void createServerSocket() {
		try{
			ServerSocket serverSocket = new ServerSocket(10000);
	        Uri uri = buildUri("content", "edu.buffalo.cse.cse486586.groupmessenger.provider");
			new ServerTask(this, uri).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, serverSocket);
		}
		catch(IOException e){
			Log.v(TAG, "Exception creating ServerSocket");
		}
	}
	
    private Uri buildUri(String scheme, String authority) {
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.authority(authority);
        uriBuilder.scheme(scheme);
        return uriBuilder.build();
    }

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_group_messenger, menu);
        return true;
    }
}
