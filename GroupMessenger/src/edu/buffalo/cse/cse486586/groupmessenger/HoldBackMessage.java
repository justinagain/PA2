package edu.buffalo.cse.cse486586.groupmessenger;

public class HoldBackMessage {

	
	private String message;
	private int messageId;
	private String avd;
	private int sentId;
	private boolean undeliverable;
	
	public HoldBackMessage(String message, int messageId, String avd,
			int sentId, boolean undelivereable){
		this.message = message;
		this.messageId = messageId;
		this.avd = avd;
		this.sentId = sentId;
		this.undeliverable = undelivereable;
	}
	
	public String getMessage(){return message;}
	public int getMessageId(){return messageId;}
	public String getAvd(){return avd;}
	public int getSentId(){return sentId;}
	public boolean isUndeliverable(){return undeliverable;}	
}
