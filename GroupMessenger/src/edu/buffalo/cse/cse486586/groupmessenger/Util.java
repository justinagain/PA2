package edu.buffalo.cse.cse486586.groupmessenger;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

public class Util {

	public static String getPortNumber(Activity activity){
		TelephonyManager tel = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
		return tel.getLine1Number().substring(tel.getLine1Number().length() - 4);		
	}
	
	public static String[] getRemoteClientPorts(String portString){
		String[] remoteClientPorts = null;
		if(portString.equals(Constants.AVD0_PORT)){
			remoteClientPorts = Constants.AVD0_REMOTE_CLIENTS;
			Log.v(GroupMessengerActivity.INFO_TAG, "Found port to push to avd1 avd2");
		}
		else if(portString.equals(Constants.AVD1_PORT)){
			remoteClientPorts = Constants.AVD1_REMOTE_CLIENTS;
			Log.v(GroupMessengerActivity.INFO_TAG, "Found port to push to avd0 avd2");
		}
		else if(portString.equals(Constants.AVD2_PORT)){
			remoteClientPorts = Constants.AVD2_REMOTE_CLIENTS;
			Log.v(GroupMessengerActivity.INFO_TAG, "Found port to push to avd0 avd1");
		}
		else{
			Log.v(GroupMessengerActivity.INFO_TAG, "Did not find a push port!");
		}
		return remoteClientPorts;
	}
	
}
