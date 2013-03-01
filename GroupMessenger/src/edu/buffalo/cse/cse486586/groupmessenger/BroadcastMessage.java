package edu.buffalo.cse.cse486586.groupmessenger;

import android.content.ContentValues;

public class BroadcastMessage implements Comparable<BroadcastMessage>{
	
	public static final String REQUEST_BROADCAST = "r";
	public static final String BROADCAST = "b";
	private static final int AVD_INSERT_PT = 1;
	private static final int AVD_SEQUENCE_NUMBER_INSERT_PT = 5;
	private static final int MSG_SIZE_INSERT_PT = 11;
	private static final int MSG_INSERT_PT = 14;
	private static final byte ARRAY_INITIALIZER = "z".getBytes()[0];
	public static final int MSG_SIZE = 142;
	byte[] payload;
	
	private BroadcastMessage(String type) {
		payload = new byte[142];
		initializeArray();
		payload[0] = type.getBytes()[0];
	}

	public static BroadcastMessage createMessageFromByteArray(byte[] data) {
		return new BroadcastMessage(data);
	}

	private BroadcastMessage(byte[] newPayload) {
		payload = newPayload;
	}

	private void initializeArray() {
		for (int i = 0; i < payload.length; i++) {
			payload[i] = ARRAY_INITIALIZER;
		}
	}
	
	private void reinitializeArray(int startIndex, int length){
		for (int i = 0; i < length; i++) {
			payload[startIndex] = ARRAY_INITIALIZER;
			startIndex++;
		}		
	}
	
	/** Factory methods to create specific message types */
	public static BroadcastMessage getRequestBroadcaseMessage(){
		BroadcastMessage broadcastMessage = new BroadcastMessage(REQUEST_BROADCAST);
		return broadcastMessage;
	}
	
	public static BroadcastMessage getBroadcaseMessage(){
		BroadcastMessage broadcastMessage = new BroadcastMessage(BROADCAST);
		return broadcastMessage;		
	}
	
	/** Public accessor methods */
	public boolean isRequestBroadcast(){ return determineType(REQUEST_BROADCAST); }
	public boolean isBroadcast(){ return determineType(BROADCAST); }
	public void setAvd(String avd){ insertTextPayloadContent(avd, AVD_INSERT_PT); }
	public String getAvd(){ return new String(getPayloadAsString(4, AVD_INSERT_PT)); }
	
	public void setAvdSequenceNumber(String sequenceNumber){ 
		reinitializeArray(AVD_SEQUENCE_NUMBER_INSERT_PT, 6);
		insertTextPayloadContent(sequenceNumber, AVD_SEQUENCE_NUMBER_INSERT_PT);
	}
	
	public int getAvdSequenceNumber(){ return Integer.parseInt(getPayloadAsInt(6, AVD_SEQUENCE_NUMBER_INSERT_PT));}
	
	public void setMessageSize(String messageSize){ 
		reinitializeArray(MSG_SIZE_INSERT_PT, 3);
		insertTextPayloadContent(messageSize, MSG_SIZE_INSERT_PT); 
	}
	
	public int getMessageSize(){ return Integer.parseInt(getPayloadAsInt(3, MSG_SIZE_INSERT_PT));}
	public void setMessage(String message){ insertTextPayloadContent(message, MSG_INSERT_PT); }
	public String getMessage(){ return new String(getPayloadAsString(getMessageSize(), MSG_INSERT_PT)); }
	public byte[] getPayload(){ return payload;}
		
	public ContentValues getAsContentValue(){
		ContentValues contentValues = new ContentValues();
		contentValues.put(OnPTestClickListener.KEY_FIELD, getAvdSequenceNumber());
		contentValues.put(OnPTestClickListener.VALUE_FIELD, getMessage());
		return contentValues;
	}	
	
	/** byte[] manipulation methods */
	private String getPayloadAsInt(int size, int startPoint) {
		String avdSequenceNumber = new String(getPayloadAsString(size, startPoint));
		avdSequenceNumber = avdSequenceNumber.replaceAll("z", "");
		return avdSequenceNumber;
	}
	
	private byte[] getPayloadAsString(int size, int startPoint) {
		byte[] avdBytes = new byte[size];
		for (int i = 0; i < avdBytes.length; i++) {
			avdBytes[i] = payload[startPoint];
			startPoint++;
		}
		return avdBytes;
	}

	private void insertTextPayloadContent(String value, int insertPoint) {
		byte[] stringBytes = value.getBytes();
		for (int i = 0; i < value.length(); i++) {
			payload[insertPoint] = stringBytes[i];
			insertPoint = insertPoint + 1;
		}
	}
	
	private boolean determineType(String type) {
		String byteValue = new String(new byte[]{payload[0]});
		boolean isRequestBroadcast = false;
		if(byteValue.equals(type)){
			isRequestBroadcast = true;
		}
		return isRequestBroadcast;
	}

	@Override
	public int compareTo(BroadcastMessage comparedToBroadcastMessage) {
		return getAvdSequenceNumber() - comparedToBroadcastMessage.getAvdSequenceNumber();	
	}

	public void setType(String type) {
		payload[0] = type.getBytes()[0];
	}
	
}
