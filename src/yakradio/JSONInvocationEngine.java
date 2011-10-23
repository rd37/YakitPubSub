package yakradio;

import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONObject;

import kdseg.ds.KDSegDataStructure;

public class JSONInvocationEngine {
	//private static JSONInvocationEngine jsonEngine = new JSONInvocationEngine();
	private FactoryManager factoryManager;
	private KDSegDataStructure structure;
	private JSONFactory jsonFactory = new JSONFactory();
	
	//private JSONInvocationEngine(){};
	
	//public static JSONInvocationEngine getInstance(){return jsonEngine;};
	
	public int getCaseNumber(String request){
		if(request.equals("LoginPublisherRequest")) return 1;
		if(request.equals("ListAllBeaconRequest")) return 2;
		if(request.equals("ListVisibleBeaconRequest")) return 3;
		if(request.equals("BeaconInformationRequest")) return 4;
		if(request.equals("UpdateBeaconRequest")) return 5;
		if(request.equals("LogoutPublisherRequest")) return 6;
		if(request.equals("RegisterDefaultSubscriber")) return 7;
		if(request.equals("SubscriberGetMessages")) return 8;
		if(request.equals("UpdateSubscriber")) return 9;
		//
		return -1;
	}
	
	public String handleMessage(String jsonmessage){
		
		try{
			JSONObject jsonReqObj = new JSONObject(jsonmessage);
			String request = jsonReqObj.getString("requesttype");
			switch(getCaseNumber(request)){
				case 1: return publisherLogin(jsonReqObj);
				case 2: return listAllBeacons(jsonReqObj);
				case 3: return listVisibleBeacons(jsonReqObj);
				case 4: return getBeaconInformation(jsonReqObj);
				case 5: return updateBeacon(jsonReqObj);
				case 6: return publisherLogout(jsonReqObj);
				case 7: return subscriberDefaultLogin(jsonReqObj);
				case 8: return subscriberGetMessages(jsonReqObj);
				case 9: return updateSubscriber(jsonReqObj);
			}
			throw new Exception(request+ " *************** request pubs are "+factoryManager.getPublisherCount());
		}catch(Exception e){
			try {
				return jsonFactory.createReturnResult(false, "error "+e.toString()).toString();
			} catch (Exception e1) {
				e1.printStackTrace();
				return "{'success':'failed','message':'unknown problem with JSONFactory'}";
			}
		}
		
	}

	private String updateSubscriber(JSONObject jsonReqObj) throws Exception {
		Subscriber sub = factoryManager.getSubscriber(jsonReqObj.getInt("subscriberkey"));
		double longitude = jsonReqObj.getDouble("longitude");
		double latitude = jsonReqObj.getDouble("latitude");
		long starttime = jsonReqObj.getLong("starttime");
		long stoptime = jsonReqObj.getLong("stoptime");
		sub.getDot().lat=latitude;
		sub.getDot().lng=longitude;
		sub.getTimeSegment().starttime=starttime;
		sub.getTimeSegment().stoptime=stoptime;
		structure.removeDot(sub.getDot());
		structure.addDot(sub.getDot());
		System.out.println("Update subscriber lat "+sub.getDot().lat+" lng "+sub.getDot().lng+" start time "+sub.getTimeSegment().starttime+" stop "+sub.getTimeSegment().stoptime);
		return (jsonFactory.createReturnResult(true,"success updating subscriber "+sub.getSessionkey())).toString();
	}

	private String subscriberGetMessages(JSONObject jsonReqObj) throws Exception {
		//System.out.println("get pub messages");
		Subscriber sub = factoryManager.getSubscriber(jsonReqObj.getInt("subscriberkey"));// TODO Auto-generated method stub
		return jsonFactory.createSubscriberGetMessagesReturn(sub).toString();
	}

	private String subscriberDefaultLogin(JSONObject jsonReqObj) throws Exception{
		/*
		 * create a new temperary subscriber 
		 * needs a dot and time segment
		 * 
		 * need to return a subscriber access key
		 * dot needs to be inserted into the kdsegment db
		 * dots userkey needs to be set to subscribers seesion key
		 */
		double longitude = jsonReqObj.getDouble("longitude");
		double latitude = jsonReqObj.getDouble("latitude");
		long starttime = jsonReqObj.getLong("starttime");
		long stoptime = jsonReqObj.getLong("stoptime");
		Subscriber sub = factoryManager.createSubscriber(latitude, longitude,starttime,stoptime);
		/*
		 * insert into dskdtree
		 */
		this.structure.addDot(sub.getDot());
		/*
		 * send return json message with new key
		 */
		return jsonFactory.createAddDefaultSubscriberReturn(sub).toString();
	}

	private String updateBeacon(JSONObject jsonReqObj) throws Exception{ //this adds a beacon to the factory manager and to the publisher
		int publisherKey = jsonReqObj.getInt("publisherkey");
		Publisher pub;
		JSONObject jsonBeaconObj=jsonReqObj.getJSONObject("beaconobj");
		if((pub = factoryManager.getPublisher(publisherKey))!=null){
			if(jsonBeaconObj.getInt("key")==-1){//need to add new beacon
				System.out.println("Add new beacon and update it");
				Beacon beacon = factoryManager.createBeacon(jsonBeaconObj.getString("beaconname"),jsonBeaconObj.getDouble("latitude"),jsonBeaconObj.getDouble("longitude"),jsonBeaconObj.getDouble("range"));
				beacon.setPublisherKey(publisherKey);
				beacon.setBeaconName(jsonBeaconObj.getString("beaconname"));
				beacon.setRange(jsonBeaconObj.getDouble("range"));
				beacon.getDot().lat=jsonBeaconObj.getDouble("latitude");
				beacon.getDot().lng=jsonBeaconObj.getDouble("longitude");
				beacon.setactivation(jsonBeaconObj.getBoolean("activated"));
				pub.addBeacon(beacon, beacon.getBeaconKey());
				
				//insert messages
				JSONArray messageList = jsonBeaconObj.getJSONArray("messagelist");
				for(int i=0;i<messageList.length();i++){
					JSONObject jsonmessage = messageList.getJSONObject(i);
					Message m = new Message(jsonmessage.getString("text"), jsonmessage.getInt("delay") );
					beacon.addMessage(m);
				}
				
				return jsonFactory.createAddBeaconReturn(beacon).toString();
			}else{//need to just update the beacon info and its messages(???)
				System.out.println("Beacon already existed, so just update the fields");
				Beacon beacon = pub.getBeacon(jsonBeaconObj.getInt("key"));
				beacon.setBeaconName(jsonBeaconObj.getString("beaconname"));
				beacon.setRange(jsonBeaconObj.getDouble("range"));
				beacon.getDot().lat=jsonBeaconObj.getDouble("latitude");
				beacon.getDot().lng=jsonBeaconObj.getDouble("longitude");
				beacon.setactivation(jsonBeaconObj.getBoolean("activated"));
				//now do the messages.
				beacon.clearMessages();
				JSONArray messageList = jsonBeaconObj.getJSONArray("messagelist");
				for(int i=0;i<messageList.length();i++){
					JSONObject jsonmessage = messageList.getJSONObject(i);
					Message m = new Message(jsonmessage.getString("text"), jsonmessage.getInt("delay") );
					beacon.addMessage(m);
				}
				
				System.out.println("Updated Beacon Information "+(jsonBeaconObj.getInt("key")));
				return jsonFactory.createAddBeaconReturn(beacon).toString();
			}
		}else{
			throw new Exception("Publisher Key not valid "+publisherKey+" unable to add beacon");
		}
	}

	private String getBeaconInformation(JSONObject jsonReqObj) throws Exception{
		Integer beaconKey = jsonReqObj.getInt("beaconkey");
		System.out.println("Get Beacon Information "+beaconKey);
		
		Beacon beacon;
		if( (beacon = factoryManager.getBeacon(beaconKey))  != null){
			return jsonFactory.createBeaconInfoReturn(beacon).toString();
		}else{
			throw new Exception("Beacon using key "+beaconKey+" does not exist");
		}
	}

	
	
	private String listVisibleBeacons(JSONObject jsonReqObj) throws Exception{//need to check if requestor is a publisher or subscriber
		throw new Exception("Not yet supported to list only visible beacons");
	}

	private String listAllBeacons(JSONObject jsonReqObj) throws Exception {//need to check if requestor is a publisher or subscriber
		Integer pubkey=jsonReqObj.getInt("publisherkey");
		System.out.println("List all Beacons "+pubkey);
		
		//Integer subkey=jsonReqObj.getInt("subscriberkey");
		if( jsonReqObj.getBoolean("ispublisher") ){ //must be a publishing request
			Publisher pub = factoryManager.getPublisher(pubkey);
			if(pub!=null){
				if(pub.state == Publisher.LOGGEDIN){
					Object[] beaconKeys = pub.getBeaconList();
					return jsonFactory.createListBeaconReturn(beaconKeys).toString();
				}else{ throw new Exception("Publisher key is ok, but you are not logged in, please log in, a new key will be generated");}
			}else{ throw new Exception("pub key is not valid "+pubkey);}
		}else{ // must be a subscriber request
			throw new Exception("Not yet support for subscibers");
		}
	}
	
	public String publisherLogout(JSONObject reqObj)throws Exception{
		String username = reqObj.getString("username");
		String password = reqObj.getString("password");
		Integer key = reqObj.getInt("publisherkey");
		System.out.println("Logout with "+username+","+password+","+key);
		boolean success = factoryManager.getPublisher(key).logout(username,password);
		System.out.println(success+" Logout with "+username+","+password+","+key);
		if(success){
			JSONObject retObj = jsonFactory.createReturnResult(success, " successful logout "+username);
			return retObj.toString();
		}else{
			JSONObject retObj = jsonFactory.createReturnResult(success, " unsuccessful logout "+username);
			return retObj.toString();
		}
		
	}
	
	public String publisherLogin(JSONObject reqObj)throws Exception{
		boolean registrationRequest = reqObj.getBoolean("register");
		String username = reqObj.getString("username");
		String password = reqObj.getString("password");	
		if(registrationRequest){
			
			if(factoryManager.isValidUsernameAvailable(username)){ //if valid, then register user
				System.out.println("Successful Registration Requested for "+username);
				int key = factoryManager.createAndAddPublisher(username,password);
				JSONObject loginPublisherReturn = jsonFactory.createPublisherLoginReturn(username,password,key);
				JSONObject jsonReturnResult = jsonFactory.createReturnResult(true, "success creating new publisher, you are logged in");
				loginPublisherReturn.put("result",jsonReturnResult);
				return loginPublisherReturn.toString();
			}else{
				System.out.println("UnSuccessful Registration Requested for "+username);
				JSONObject loginPublisherReturn = jsonFactory.createPublisherLoginReturn(username,password,-1);
				JSONObject retObj = jsonFactory.createReturnResult(false, "invalid username "+username);
				loginPublisherReturn.put("result",retObj);
				return loginPublisherReturn.toString();
			}
		}else{//must be a login request
			Publisher pub;
			if( (pub = factoryManager.getValidPublisher(username,password)) != null){
				pub.state = Publisher.LOGGEDIN;
				factoryManager.setNewKey(pub,factoryManager.getKey());
				System.out.println("Successful Loggin for "+username+" session key "+pub.getSessionkey() +" change the Factory Manager to reference publisher at new key using setNewKey function" );
				//pub.setSessionkey(factoryManager.getKey());
				JSONObject loginPublisherReturn = jsonFactory.createPublisherLoginReturn(username,password,pub.getSessionkey());
				JSONObject jsonReturnResult = jsonFactory.createReturnResult(true, "success logging into your publishing account");
				loginPublisherReturn.put("result",jsonReturnResult);
				return loginPublisherReturn.toString();
			}else{
				JSONObject loginPublisherReturn = jsonFactory.createPublisherLoginReturn(username,password,-1);
				JSONObject jsonReturnResult = jsonFactory.createReturnResult(false, "invalid login information");
				loginPublisherReturn.put("result",jsonReturnResult);
				return loginPublisherReturn.toString();
			}
		}
	}
	
	public void initialize(KDSegDataStructure structure,
			FactoryManager factoryManager) {
		this.setStructure(structure);
		this.setFactoryManager(factoryManager);
	}

	public void setFactoryManager(FactoryManager factoryManager) {
		this.factoryManager = factoryManager;
	}

	public FactoryManager getFactoryManager() {
		return factoryManager;
	}

	public void setStructure(KDSegDataStructure structure) {
		this.structure = structure;
	}

	public KDSegDataStructure getStructure() {
		return structure;
	}
}
