package edu.buffalo.cse.cse486586.groupmessenger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import edu.buffalo.cse.cse486586.groupmessenger.R;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
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


	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_messenger);

        TextView tv = (TextView) findViewById(R.id.textView1);
        tv.setMovementMethod(new ScrollingMovementMethod());
        findViewById(R.id.button1).setOnClickListener(
                new OnPTestClickListener(tv, getContentResolver()));
        createServerSocket();
		final Button sendButton = (Button) findViewById(R.id.button4);
		sendButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
			}
		})

	}

	private void createServerSocket() {
		try{
			ServerSocket serverSocket = new ServerSocket(10000);
			new ServerTask().executeOnExecutor(
					AsyncTask.THREAD_POOL_EXECUTOR, 
					serverSocket);
		}
		catch(IOException e){
			Log.v(TRY_CATCH_ERROR, "Exception creating ServerSocket");
		}
	}
	
	private class ServerTask extends AsyncTask<ServerSocket, String, Void>{
		@Override
		protected Void doInBackground(ServerSocket... sockets) {
			String msg = null;
			ServerSocket serverScoket = sockets[0];
			Socket socket;
			try{
				while(true){
					socket = serverScoket.accept();
					BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					msg = in.readLine();
					publishProgress(msg);
					socket.close();
				}
			}
			catch (IOException e){
				Log.v(TRY_CATCH_ERROR, "IOException creating ServerSocket");
			}
			return null;
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
