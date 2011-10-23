package yakradio;

public class Message {
	public String message;
	public int timeout;
	private int beaconkey;
	
	public Message(String msg,int timeout){
		this.message = msg;
		this.timeout = timeout;
		//this.key=key;
	}
	
	public void setMessage(String message){
		this.message = message;
	}
	
	public void setTimeout(int timeout){
		this.timeout = timeout;
	}
	
	public String getMessage(){
		return message;
	}
	
	public int getTimeout(){
		return timeout;
	}

	public void setBeaconkey(int beaconkey) {
		this.beaconkey = beaconkey;
	}

	public int getBeaconkey() {
		return beaconkey;
	}
}
