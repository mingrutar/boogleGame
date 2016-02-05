package bogglegame;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Main {
	static void example1(String s) {
		Object obj=JSONValue.parse(s); 
		JSONArray array=(JSONArray)obj; 
		System.out.println("======the 2nd element of array======"); 
		System.out.println(array.get(1)); 
		System.out.println();

		JSONObject obj2=(JSONObject)array.get(1); 
		System.out.println("======field \"1\"=========="); 
		System.out.println(obj2.get("1"));

		s="{}"; 
		obj=JSONValue.parse(s); 
		System.out.println(obj);

		s="[5,]"; 
		obj=JSONValue.parse(s); 
		System.out.println(obj);

		s="[5,,2]"; obj=JSONValue.parse(s); 
		System.out.println(obj);
	}
	static void example2(String s) throws ParseException {
		JSONParser parser=new JSONParser();
		System.out.println("=======decode=======");

//		String s="[0,{\"1\":{\"2\":{\"3\":{\"4\":[5,{\"6\":7}]}}}}]"; 
		Object obj=parser.parse(s); 
		JSONArray array=(JSONArray)obj; 
		System.out.println("======the 2nd element of array======"); 
		System.out.println(array.get(1)); 
		System.out.println();

		JSONObject obj2=(JSONObject)array.get(1); 
		System.out.println("======field \"1\"=========="); 
		System.out.println(obj2.get("1"));

		String ss="{}"; 
		obj=parser.parse(ss); 
		System.out.println(obj);

		ss="[5,]"; 
		obj=parser.parse(ss); 
		System.out.println(obj);

		ss="[5,,2]"; 
		obj=parser.parse(s); 
		System.out.println(obj);	
	}
/** 
 * Expected output:
 *   ==iterate result== first=>123 second=>[4, 5, 6] third=>789 
 * 
 * 	==toJSONString()== {"first":123,"second":[4,5,6],"third":789}		
 */
	static void example3() {
		String jsonText = "{\"first\": 123, \"second\": [4, 5, 6], \"third\": 789}"; 
		JSONParser parser = new JSONParser();
		ContainerFactory containerFactory = new ContainerFactory(){ 
			public List creatArrayContainer() { 
				return new LinkedList(); 
			}
			@Override
			public Map createObjectContainer() {
				return new LinkedHashMap();
			}		
		};
		try { 
			Map json = (Map)parser.parse(jsonText, containerFactory); 
			Iterator iter = json.entrySet().iterator(); 
			System.out.println("==iterate result=="); 
			while(iter.hasNext()){ 
				Map.Entry entry = (Map.Entry)iter.next(); 
				Object key = entry.getKey();
				Object val = entry.getValue();
				System.out.println(key + "=>" + val); 
			}
			System.out.println("==toJSONString()==");
			System.out.println(JSONValue.toJSONString(json));
		} catch(ParseException pe){ 
			System.out.println(pe); 
		} 
	}
	static void myParse() throws ParseException {
		String[] resp = {"{\"batchcomplete\": \"\", \"query\": {\"pages\": {\"27637\": {\"pageid\": 27637,\"ns\": 0, \"title\": \"test\"}}}}",
		 "{\"batchcomplete\": \"\", \"query\": {\"pages\": {\"-1\": {\"pageid\": 27637,\"ns\": 0, \"title\": \"testx\",\"missing\": \"\"}}}}" };
		for (String s : resp) {
			boolean ret = parseResponse(s);
			System.out.println(String.format("%s for %s", ret, s));
		}
	}
	static boolean parseResponse(String str) throws ParseException {
		JSONParser parser=new JSONParser();
		ContainerFactory containerFactory = new ContainerFactory(){
			@Override
			public List creatArrayContainer() { 
				return new LinkedList(); 
			}
			@Override
			public Map createObjectContainer() {
				return new HashMap();	//LinkedHashMap ?
			}		
		};
		Map json = (Map)parser.parse(str, containerFactory);
		Object obj = json.get("query");
		if ( obj instanceof Map ) {
			obj = ((Map) obj).get("pages");
			if ( obj instanceof Map ) {
				Map pages = (Map) obj;
				return !pages.containsKey("-1");
			}
		}
		throw new RuntimeException(String.format("Bad JSON input string: %s", str));
	}
	
	public static void main(String[] args) {
		String s="[0,{\"1\":{\"2\":{\"3\":{\"4\":[5,{\"6\":7}]}}}}]"; 
// won't take ''		String s="[0,{'1':{'2':{'3':{'4':[5,{'6':7}]}}}}]"; 
		try {
//			example3();
//			example2(s);
			myParse();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	/*		BoggleGame theGame = new BoggleGame();
			theGame.rollDices();
*/
	}

}
