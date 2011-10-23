package yakradio;

import java.util.HashMap;
import java.util.Set;

public class Publisher {
	private HashMap<Integer,Beacon> beacons = new HashMap<Integer,Beacon>();
	private String username;
	private String password;
	private int sessionkey;
	
	public static int LOGGEDIN=1;
	public static int LOGGEDOUT=2;
	public int state = 0;
	
	
	public boolean logout(String un,String pw){
		System.out.println("attempt logout");
		if(username.equals(un)&&password.equals(pw)){
			state = Publisher.LOGGEDOUT;
			return true;
		}
		return false;
	}
	
	public void addBeacon(Beacon beacon,Integer key){
		beacons.put(key,beacon);
	}
	
	public Beacon removeBeacon(Integer key){
		return beacons.remove(key);
	}
	
	public void removeBeacon(Beacon beacon){
		beacons.remove(beacon);
	}
	
	public Object[] getBeaconList(){
		Set<Integer> keys = beacons.keySet();
		return keys.toArray();
	}
	
	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public void setSessionkey(int sessionkey) {
		this.sessionkey = sessionkey;
	}

	public int getSessionkey() {
		return sessionkey;
	}

	public Beacon getBeacon(int key) {
		return beacons.get(key);
	}
}
