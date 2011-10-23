package yakradio;

import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONObject;

public class JSONFactory {
	public JSONObject createAddDefaultSubscriberReturn(Subscriber sub)throws Exception{
		JSONObject ret = new JSONObject();
		ret.put("msgtype", "registration");
		ret.put("subscriberkey", sub.getSessionkey());
		ret.put("success",true);
		return ret;
	}
	public JSONObject createReturnResult(boolean success,String message)throws Exception{
		JSONObject ret = new JSONObject();
		ret.put("msgtype", "ReturnResult");
		ret.put("success",success);
		ret.put("message",message);
		return ret;
	}

	public JSONObject createPublisherLoginReturn(String username, String password,
			int key) throws Exception{
		JSONObject pubRet = new JSONObject();
		pubRet.put("username", username);pubRet.put("password", password);
		pubRet.put("key",key);pubRet.put("msgtype", "LoginReturn");
		return pubRet;
	}
	
	public JSONObject createListBeaconReturn(Object[] beaconkeys)throws Exception{
		JSONObject ret = new JSONObject();
		JSONArray array = new JSONArray(beaconkeys);
		ret.put("msgtype", "ListBeaconsReturn");
		ret.put("beaconkeys", array);
		return ret;
	}

	public JSONObject createBeaconInfoReturn(Beacon beacon) throws Exception{
		JSONObject beaconInfo = new JSONObject();
		beaconInfo.put("msgtype", "BeaconInfoReturn");
		beaconInfo.put("beacon_name", beacon.getBeaconName());
		beaconInfo.put("beacon_latitude", beacon.getDot().lat);
		beaconInfo.put("beacon_longitude", beacon.getDot().lng);
		beaconInfo.put("beacon_range",beacon.getRange());
		beaconInfo.put("beacon_key", beacon.getBeaconKey());
		beaconInfo.put("beacon_activated", beacon.isRunning());
		beaconInfo.put("beacon_messages", new JSONArray(beacon.getMessageList()) );
		return beaconInfo;
	}

	public JSONObject createAddBeaconReturn(Beacon beacon) throws Exception{
		JSONObject retObj = new JSONObject();
		retObj.put("msgtype", "UpdateBeaconReturn");
		retObj.put("beacon_key", beacon.getBeaconKey());
		retObj.put("publisher_key", beacon.getPublisherKey());
		return retObj;
	}
	
	public Object createSubscriberGetMessagesReturn(Subscriber sub) throws Exception{
		try{
			//System.out.println("subscriber return of messages for "+sub.getSessionkey());
			LinkedList<Message> list = sub.getMessages();
			JSONObject retObj = new JSONObject();
		
			
			retObj.put("subscriberkey", sub.getSessionkey());
			//System.out.println("check point "+sub.getSessionkey());
			retObj.put("msgtype", "SubscriberGetMessagesReturn");
			JSONArray mesglist = new JSONArray();
			retObj.put("msglist", mesglist);
			//System.out.println("check point "+sub.getSessionkey());
		
			for(int i=0;i<list.size();i++){
				Message msg = list.get(i);
				JSONObject msgobj = new JSONObject();
				msgobj.put("beaconid", msg.getBeaconkey());
				msgobj.put("message", msg.getMessage());
				mesglist.put(msgobj);
			}
			//System.out.println("subscriber ret obj "+retObj);
			return retObj;
		}catch(Exception e){
			e.printStackTrace();
			throw new Exception(e);
		}
		//return null;
	}
}
