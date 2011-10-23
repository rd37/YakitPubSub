package yakradio;

import java.util.LinkedList;

import kdseg.ds.Dot;
import kdseg.ds.TimeSegment;
import kdseg.ds.GeographicalRange;
import kdseg.ds.KDSegDataStructure;

public class Beacon implements Runnable{
	private LinkedList<Message> messages = new LinkedList<Message>();
	private FactoryManager fm;
	private KDSegDataStructure kdseg;
	private boolean running = false;
	private int currMessageIndex=0;
	
	//Beacon Information
	private String beaconName;
	private Dot dot; //contains lat and lng of dot position
	private Double range;
	private Integer beaconKey;
	private Integer publisherKey;
	private Long starttime=new Long(0);
	private Long stoptime = System.currentTimeMillis();
	
	public Beacon(FactoryManager fm,KDSegDataStructure kdseg){
		this.fm=fm;
		this.kdseg=kdseg;
	}
	
	public void clearMessages(){
		messages.clear();
	}
	public Object[] getMessageList(){
		return messages.toArray();
	}
	
	public void addMessage(Message msg){
		System.out.println("add msg "+msg.message);
		msg.setBeaconkey(this.beaconKey);
		messages.add(msg);//adds to end of array
	}
	
	public void addMesseage(Message msg, int index){
		messages.add(index, msg);
	}
	
	public void moveMessage(Message msg,int index){
		messages.remove(msg);
		messages.add(index,msg);
	}
	
	public void moveMessage(int indexSRC,int indexDEST){
		Message msg = messages.remove(indexSRC);
		messages.add(indexDEST,msg);
	}

	public void removeMessage(int index){
		messages.remove(index);
	}
	
	/*
	 * Get change Beacon dot and range to a geographical range object
	 * Create current time segment
	 * 
	 * Submit request for system user listeners-get back list of Dots
	 * send each user the message string
	 */
	private void sendMessage(Message msg){
		//System.out.println(this.toString()+" beacon is sending "+msg);
		GeographicalRange rng = fm.calculateRange(dot,range);
		TimeSegment seg = new TimeSegment(new Long(starttime),new Long(stoptime));
		//System.out.println("*****Beacon Editor search dots*****");
		LinkedList<Dot> dots = kdseg.searchDots(rng, seg);
		//System.out.println("Found Beacon Editor "+dots.size()+" to send message "+msg);
		//System.out.println("********done Beacon editor************");
		for(int i=0;i<dots.size();i++){
			Dot d = dots.get(i);
			int userkey = d.userkey;
			Subscriber sub = fm.getSubscriber(userkey);
			sub.addNewMessage(msg);//puts message in the inbox
			System.out.println("Sent message to "+userkey+" message is "+msg.getMessage());
		}
	}
	@Override
	public void run() {
		while(running){
			try{
				Thread.sleep(250);
				if(!messages.isEmpty()){
					if(messages.size()>currMessageIndex){
						Message nextmessage = messages.get(currMessageIndex);
						if(nextmessage!=null){
							//System.out.println("Sending message "+nextmessage.getMessage()+" timeout "+nextmessage.getTimeout());
							Thread.sleep(nextmessage.getTimeout()*1000);
							sendMessage(nextmessage);
						}
						currMessageIndex++;
					}else{
						currMessageIndex=0;
					}
				}
			}catch(Exception e){
				System.err.println("Beacon Error "+e);
				currMessageIndex=0;
			}
		}
	}
	
	public boolean isRunning(){
		return this.running;
	}
	
	public void setactivation(boolean activate){
		System.out.println("set activate "+activate);
		if(activate==true){
			if(!running){//then activate beacon
				this.start();
			}
		}else{
			if(running){//then deactivate beacon
				this.stop();
			}
		}
	}
	
	public void start(){
		if(running==false){
			running = true;
			Thread newThread = new Thread(this);
			newThread.start();
		}else{
			System.err.println("Beacon Already Running ");
		}
	}
	
	public void stop(){
		running=false;
	}

	public void setBeaconName(String beaconName) {
		this.beaconName = beaconName;
	}

	public String getBeaconName() {
		return beaconName;
	}

	public void setDot(Dot dot) {
		this.dot = dot;
	}

	public Dot getDot() {
		return dot;
	}

	public void setRange(Double range) {
		this.range = range;
	}

	public Double getRange() {
		return range;
	}

	public void setBeaconKey(Integer beaconKey) {
		this.beaconKey = beaconKey;
	}

	public Integer getBeaconKey() {
		return beaconKey;
	}

	public void setPublisherKey(Integer publisherKey) {
		this.publisherKey = publisherKey;
	}

	public Integer getPublisherKey() {
		return publisherKey;
	}

	public Long getStarttime() {
		return starttime;
	}

	public void setStarttime(Long starttime) {
		this.starttime = starttime;
	}

	public Long getStoptime() {
		return stoptime;
	}

	public void setStoptime(Long stoptime) {
		this.stoptime = stoptime;
	}
}
