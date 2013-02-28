package edu.buffalo.cse.cse486586.groupmessenger;

public class BroadcastMessage {
	
	private static final String REQUEST_BROADCAST = "r";
	private static final String BROADCAST = "b";
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

	private void initializeArray() {
		for (int i = 0; i < payload.length; i++) {
			payload[i] = ARRAY_INITIALIZER;
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
	public void setAvdSequenceNumber(String sequenceNumber){ insertTextPayloadContent(sequenceNumber, AVD_SEQUENCE_NUMBER_INSERT_PT);}
	public int getAvdSequenceNumber(){ return Integer.parseInt(getPayloadAsInt(6, AVD_SEQUENCE_NUMBER_INSERT_PT));}
	public void setMessageSize(String messageSize){ insertTextPayloadContent(messageSize, MSG_SIZE_INSERT_PT); }
	public int getMessageSize(){ return Integer.parseInt(getPayloadAsInt(3, MSG_SIZE_INSERT_PT));}
	public void setMessage(String message){ insertTextPayloadContent(message, MSG_INSERT_PT); }
	public String getMessage(){ return new String(getPayloadAsString(getMessageSize(), MSG_INSERT_PT)); }
	
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
		for (int i = 0; i <= MSG_SIZE; i++) {
			insertPoint = insertPoint + i;
			payload[insertPoint] = stringBytes[i];
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
	
}
