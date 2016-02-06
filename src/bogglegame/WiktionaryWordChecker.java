package bogglegame;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
/***
 * 
 * use https://www.mediawiki.org/wiki/API:Main_page for word validation
 *   Example 1 valid word 'test': 
 *    request:  https://en.wiktionary.org/w/api.php?action=query&titles=test&format=json
 *    response in JSON: 
 *       {"batchcomplete":"","query":{"pages":{"27637":{"pageid":27637,"ns":0,"title":"test"}}}}
 *   Example 2 invalid word 'testx':
 *     request: https://en.wiktionary.org/w/api.php?action=query&titles=testx&format=json
 *    response in JSON: 
 *       {"batchcomplete": "", "query": {"pages": {"-1": {"pageid": 27637,"ns": 0, "title": "testx","missing": ""}}}}
 *  @author linna
 *
 */
public class WiktionaryWordChecker implements WordChecker {
	private String requestTemplate = "https://en.wiktionary.org/w/api.php?action=query&titles=%s&format=json";

	private boolean parseResponse(String str) throws ParseException {
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
	/**
	 * isValidWord
	 * @param word
	 * @return
	 * 
	 */
	@Override
	public boolean isValidWord(String word) {
		boolean ret = false;
		try { 
			URL req = new URL(String.format(requestTemplate, word));		// validate with remote
			HttpURLConnection connect = (HttpURLConnection) req.openConnection();
			connect.setRequestMethod("GET");
			connect.setRequestProperty("User-Agent", "Mozilla/5.0");
			int respCode = connect.getResponseCode();						// check response code
//			System.out.println(String.format("Checking '%s' returns code %d", word, respCode));
			if (respCode < 400) {											// not error
				BufferedReader reader = new BufferedReader(new InputStreamReader(connect.getInputStream()));
				StringBuilder resp = new StringBuilder();
				String line = null;
				while ((line = reader.readLine())!= null) {					// get response string
					resp.append(line);
				}
				ret = parseResponse(resp.toString());						// parse response and get result
//				if (ret) {
//					System.out.print("!!! Found valid word:" + word);
//				}
			}
		} catch (Exception ioex) {
			System.out.println("Caught exception:"+ ioex);
			ioex.printStackTrace();
		}
		return ret;
	}
}
