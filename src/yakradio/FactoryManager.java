package yakradio;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import kdseg.ds.Dot;
import kdseg.ds.TimeSegment;
import kdseg.ds.GeographicalRange;
import kdseg.ds.KDSegDataStructure;

public class FactoryManager {
	private static FactoryManager fm = new FactoryManager();
	private HashMap<Integer,Subscriber> subscribers = new HashMap<Integer,Subscriber>();
	private HashMap<Integer,Publisher>	publishers = new HashMap<Integer,Publisher>();
	private HashMap<Integer,Beacon>	beacons = new HashMap<Integer,Beacon>();
	private KDSegDataStructure kdseg;
	private Random random;
	
	private FactoryManager(){};
	public static FactoryManager getInstance(){return fm;};
	
	public void initialize(KDSegDataStructure kdseg){
		this.kdseg=kdseg;
		random = new Random(5678);
	}
	
	public int getPublisherCount(){
		return publishers.size();
	}
	public GeographicalRange calculateRange(Dot centerDot,double range){
		double latitudeDegrees = this.metersToDegreesLatitude(range);
		double longitudeDegrees = this.metersToDegreesLongitude(range, this.degreesToRadians(latitudeDegrees));
		double nwlat = centerDot.lat+latitudeDegrees;
		double selat = centerDot.lat-latitudeDegrees;
		double nwlng = centerDot.lng-longitudeDegrees;
		double selng = centerDot.lng+longitudeDegrees;
		GeographicalRange rng = new GeographicalRange(new Double(nwlat),new Double(nwlng),new Double(selat),new Double(selng));
		return rng;
	}
	
	public double metersToDegreesLongitude(double meters,double latitudeRadians){
		return (Math.cos(latitudeRadians)*meters)/(111120);
	}
	
	public double metersToDegreesLatitude(double meters){
		return meters/111120;
	}
	
	public double degreesToRadians(double deg){
		return (Math.PI*deg)/(180.00);
	}
	
	public int getKey(){
		return random.nextInt();
	}
	
	public Subscriber createSubscriber(double latitude,double longitude,long starttime,long stoptime){
		Subscriber sub = new Subscriber();
		this.addSubscriber(sub);//adds to the subscriber hashmap maintained by factory manager as well session key is set
		TimeSegment ts = new TimeSegment(starttime,stoptime);
		Dot dot = new Dot(latitude,longitude,ts,sub.getSessionkey());
		sub.setDot(dot);
		sub.setTimeSegment(ts);
		dot.userkey=sub.getSessionkey();
		return sub;
	}
	
	public int addSubscriber(Subscriber sub){
		int key = random.nextInt();
		subscribers.put(key, sub);
		sub.setSessionkey(key);
		return key;
	}
	
	public int addPublisher(Publisher pub){
		int key = random.nextInt();
		publishers.put(key, pub);
		return key;
	}
	
	public Subscriber getSubscriber(int key){
		return subscribers.get(key);
	}
	
	public Publisher getPublisher(int key){
		System.out.println("Find publisher using key "+key+" to handle Request");
	    Publisher pub =  publishers.get(key);
	    if(pub == null){
	    	System.out.println("publisher is null");
	    }
	    //System.out.println("get publisher "+key);
	    return pub;
	}
	
	public Beacon getBeacon(int key){
		return beacons.get(key);
	}
	
	public void removeSubscriber(int key){
		subscribers.remove(key);
	}
	
	public void removePublisher(int key){
		publishers.remove(key);
	}

	public boolean isValidUsernameAvailable(String username) {
		Set<Integer> keys = publishers.keySet();
		Iterator<Integer> keysIt = keys.iterator();
		while(keysIt.hasNext()){
			Integer key = keysIt.next();
			Publisher pub = publishers.get(key);
			System.out.println("Compare "+username+" , "+pub.getUsername());
			if(pub.getUsername().equals(username)){
				return false;
			}
		}
		return true;
	}

	
	public int createAndAddPublisher(String username, String password) {
		Publisher publisher = new Publisher();
		publisher.setUsername(username);
		publisher.setPassword(password);
		publisher.state=Publisher.LOGGEDOUT;
		int key = random.nextInt();
		System.out.println("Created a new publisher got key "+key);
		publisher.setSessionkey(key);
		publishers.put(key, publisher);
		return key;
	}

	public Publisher getValidPublisher(String username, String password) {
		Set<Integer> keys = publishers.keySet();
		Iterator<Integer> keysIt = keys.iterator();
		while(keysIt.hasNext()){
			Integer key = keysIt.next();
			Publisher pub = publishers.get(key);
			if(pub.getUsername().equals(username) && pub.getPassword().equals(password)){
				return pub;
			}
		}
		return null;
	}

	public Beacon createBeacon(String name, double lat, double lng,
			double range) {
		Beacon beacon = new Beacon(this,kdseg);
		int beaconkey = random.nextInt();
		beacon.setBeaconName(name);
		TimeSegment timeSeg = new TimeSegment(new Long(0),2*(new Long(System.currentTimeMillis())) );
		Dot dot = new Dot(new Double(lat),new Double(lng),timeSeg, beaconkey);
		
		beacon.setBeaconKey(beaconkey);
		beacon.setDot(dot);
		beacon.setRange(range);
		beacons.put(beaconkey, beacon);
		return beacon;
	}

	public void setNewKey(Publisher pub, int key) {
		publishers.remove(pub.getSessionkey());
		publishers.put(key, pub);
		pub.setSessionkey(key);
	}

}
