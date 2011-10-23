package yakradio;

import java.util.LinkedList;

import kdseg.ds.Dot;
import kdseg.ds.TimeSegment;

public class Subscriber {
	private LinkedList<Message> inboxmsgs = new LinkedList<Message>();
	private LinkedList<Message> readmsgs = new LinkedList<Message>();
	private Dot dot;
	private TimeSegment ts;
	private int sessionkey;

	public Subscriber(){
		//this.sessionkey = sessionkey;
	}
	
	public void addNewMessage(Message message){
		inboxmsgs.add(message);
	}
	
	public LinkedList<Message> getMessages(){
		LinkedList<Message> retList = new LinkedList<Message>();
		Message msg;
		int count=5;
		try{
			while( ( (msg=getNewNextMessage()) != null)&&(count>0)){
				count--;
				retList.addFirst(msg);
			}
		}catch(Exception e){
			//System.out.println("No elements yet");
		}
		return retList;
	}
	
	public Message getNewNextMessage(){
		Message m = inboxmsgs.removeLast();
		readmsgs.add(m);
		return m;
	}
	
	public void clearReadMessages(){
		readmsgs.clear();
	}
	
	public void clearInBoxMessages(){
		inboxmsgs.clear();
	}
	
	public void clearAllMessage(){
		this.clearInBoxMessages();
		this.clearReadMessages();
	}
	
	public void setSessionkey(int sessionkey) {
		this.sessionkey = sessionkey;
	}

	public int getSessionkey() {
		return sessionkey;
	}

	public void setDot(Dot dot) {
		this.dot = dot;
	}

	public Dot getDot() {
		return dot;
	}

	public void setTimeSegment(TimeSegment ts) {
		this.ts=ts;
	}
	
	public TimeSegment getTimeSegment(){
		return ts;
	}
	
}
