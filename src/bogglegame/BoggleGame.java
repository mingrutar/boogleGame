package bogglegame;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Game: roll 16 dices and find valid words. see https://en.wikipedia.org/wiki/Boggle
 *    
 * use https://www.mediawiki.org/wiki/API:Main_page for word validation
 *   Example 1 valid word 'test': 
 *    request:  https://en.wiktionary.org/w/api.php?action=query&titles=test
 *    response in JSON: 
 *       {"batchcomplete": "", "query": {"pages": {"27637": {"pageid": 27637,"ns": 0, "title": "test"}}}}
 *   Example 2 invalid word 'testx':
 *     request: https://en.wiktionary.org/w/api.php?action=query&titles=testx
 *    response in JSON: 
 *       {"batchcomplete": "", "query": {"pages": {"-1": {"pageid": 27637,"ns": 0, "title": "testx","missing": ""}}}}
 * @author Ming
 *
 */
/*
 * TODOs: 1) make it maven 
 */
public class BoggleGame {
	public final static int numberDices = 16;
	public final static int dimension = 4;
	public final static int numberDiceFace = 6;
	public final static int minLength = 3;
	
	private String requestTemplate = "https://en.wiktionary.org/w/api.php?action=query&titles=%s";
	
	// dice letters source (I made up) 16 of 6 char string
	public final static String[] diceLetters = {"ABCDEF", "GHIJKL","MNOPQR","STUVWX",
			"YZABCD","EFGHIJ","KLMNOP","QRSTUV",
			"WXYZAB","CDEFGH","IJKLMN","OPQRST",
			"UVWXYZ","ABCDEF","GHIJKLM","NOPQRST" };
	
	private Random random;
	private char[][] aRoll;
	private StringBuilder sb;
	private List<String> validWords;
	
	public BoggleGame() {
		aRoll = new char[dimension][dimension];
		random = new Random();
		sb = new StringBuilder();
		validWords = new ArrayList<String>();
	}
	// preparation
	public void rollDices() {
		for (int iy = 0; iy < dimension; iy++ ) {
			for (int ix = 0; ix < dimension; ix++) {
				int diceIdx = iy * dimension + ix;	  // which dice
				String theDice = diceLetters[diceIdx].toUpperCase();
				int theFace = random.nextInt()%numberDiceFace; // which face
				aRoll[ix][iy] = theDice.toCharArray()[theFace];
			}
		}
		// dump roll result 
		for (int iy = 0; iy < dimension; iy++ ) {
			for (int ix = 0; ix < dimension; ix++) {
				System.out.print(" " + aRoll[ix][iy]);
			}
			System.out.println();
		}
	}
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
	 * Validate word with https://en.wiktionary.org/. The responses are like this:
	 * valid word: 
	 *  {"batchcomplete": "", "query": {"pages": {"27637": {"pageid": 27637,"ns": 0, "title": "test"}}}}
	 * invalid word:
	 *  {"batchcomplete": "", "query": {"pages": {"-1": {"pageid": 27637,"ns": 0, "title": "testx","missing": ""}}}}
	 */
	private boolean isValidWord(String word) {
		boolean ret = false;
		try { 
			URL req = new URL(String.format(requestTemplate, word));		// validate with remote
			HttpURLConnection connect = (HttpURLConnection) req.openConnection();
			connect.setRequestMethod("GET");
			connect.setRequestProperty("User-Agent", "Mozilla/5.0");
			int respCode = connect.getResponseCode();						// check response code
			System.out.println(String.format("Checking '%s' returns code %d", word, respCode));
			if (respCode < 400) {											// not error
				BufferedReader reader = new BufferedReader(new InputStreamReader(connect.getInputStream()));
				StringBuilder resp = new StringBuilder();
				String line = null;
				while ((line = reader.readLine())!= null) {					// get response string
					resp.append(line);
				}
				ret = parseResponse(resp.toString());						// parse response and get result
			}
		} catch (Exception ioex) {
			System.out.println("Caught exception:"+ ioex);
			ioex.printStackTrace();
		}
		return ret;
	}
	private void search(int ix, int iy) {
		sb.append(aRoll[ix][iy]);
		if (sb.length() >= minLength) {
			String word = sb.toString();
			if (isValidWord(word)) {
				validWords.add(word);
			}
		}
		for (int x = Math.min(ix-1, 0); x < Math.max(ix+1, dimension); x++) {
			for (int y = Math.min(iy-1, 0); y < Math.max(iy+1, dimension); y++) {
				if ( (ix!=x) && (iy !=y)) {
					search(x, y);
				}
			}
		}
	}
	public void searchWords() {
		for (int iy = 0; iy < dimension; iy++ ) {
			for (int ix = 0; ix < dimension; ix++) {
				search(ix, iy);
			}
		}
	}
}
