package bogglegame;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Stack;

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
	public final static int dimension = 3;
	public final static int numberDiceFace = 6;
	public final static int minLength = 4;
	
	private String requestTemplate = "https://en.wiktionary.org/w/api.php?action=query&titles=%s";
	
	// dice letters source (I made up) 16 of 6 char string
	public final static String[] diceLetters = {"ABCDEF", "GHIJKL","MNOPQR","STUVWX",
			"YZABCD","EFGHIJ","KLMNOP","QRSTUV",
			"WXYZAB","CDEFGH","IJKLMN","OPQRST",
			"UVWXYZ","ABCDEF","GHIJKLM","NOPQRST" };
	
	private Random random;
	private Character[][] aRoll;

	private StringBuilder sb;
	private List<String> validWords;
	private List<BoggleElement> processedRoll;
	private Stack<BoggleElement> workPath;
	private StringBuilder debugger = new StringBuilder();
	
	class BoggleElement {
		private char letter;
		private int x;
		private int y;		
		
		List<BoggleElement> children = new ArrayList<BoggleElement>();
		
		BoggleElement(char l, int x, int y) {
			letter = l;
			this.x = x;
			this.y = y;
		}
		@Override
		public boolean equals(Object right) {
			if ((right !=null) && (right instanceof BoggleElement)) {
				BoggleElement be = (BoggleElement) right;
				return (this.x == be.x) && (this.y == be.y) && (this.letter == be.letter);
			} else
				return false;
		}
		@Override
		public String toString() {
			return String.format(" BE: [%d,%d]%c, #child=%d", x, y, letter, children.size());
		}
		public void addChild(BoggleElement c) {
			if (!children.contains(c)) {
				this.children.add(c);
			}
		}
		public void dump() {
			System.out.print(String.format(" [%d,%d]%c has : ", x, y, letter));
			for (BoggleElement be : children) {
				System.out.print(String.format(" [%d,%d]%c", be.x, be.y, be.letter));
			}
			System.out.println();
		}
	}

	public BoggleGame() {
		aRoll = new Character[dimension][dimension];
		random = new Random();
		sb = new StringBuilder();
		validWords = new ArrayList<String>();
		processedRoll = new ArrayList<BoggleElement>();
	}
	// preparation
	public void rollDices() {
		for (int iy = 0; iy < dimension; iy++ ) {
			for (int ix = 0; ix < dimension; ix++) {
				int diceIdx = iy * dimension + ix;	  // which dice
				String theDice = diceLetters[diceIdx].toUpperCase();
				int theFace = Math.abs(random.nextInt()) % numberDiceFace; // which face
				aRoll[ix][iy] = new Character( theDice.toCharArray()[theFace]);
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
		boolean ret = (this.random.nextInt() % 10 == 1);
		return ret;
	}
/*	private boolean isValidWord_2(String word) {
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
*/	
	private void search(BoggleElement be) {
		workPath.push(be);
		sb.append(be.letter);
		if (workPath.size() >= minLength) {
			String word = sb.toString();
			if (isValidWord(word)) {
//				System.out.println(" "+word);
				validWords.add(word);
			}
		}
		for (BoggleElement bec : be.children) {
			if (!workPath.contains(bec))  {
				search(bec);
			}
		}
		workPath.pop();
		int l = workPath.size();
		debugger.append(String.format(" %s", sb.toString()));
		if (l == 0) {
			System.out.println(debugger.toString());
			debugger.setLength(0);
		} else {
			if (debugger.length() > 250) {
				System.out.println(debugger.toString());
				debugger.setLength(0);
			}
			sb.setLength(l);
		}
	}
	//
	private void findNeighbours(){
		// create all instances
		BoggleElement[][] temp = new BoggleElement[dimension][dimension];
		for (int x = 0; x <dimension; x++) {
			for (int y = 0; y < dimension; y++) {
				temp[x][y] = new BoggleElement(aRoll[x][y], x, y);
			}
		}
		// add children
		for (int ix = 0; ix <dimension; ix++) {
			for (int iy = 0; iy < dimension; iy++) {
				BoggleElement be = temp[ix][iy];
				for (int x = Math.max(ix-1, 0); x <= Math.min(ix+1, (dimension-1)); x++) {
					for (int y = Math.max(iy-1, 0); y <= Math.min(iy+1, (dimension-1)); y++) {
						if ((ix != x) || (iy != y)) {
							be.addChild(temp[x][y]);
						}
					}
				}
				processedRoll.add(be);
				be.dump();
			}
		}
	}
	public void searchWords() {
		//preparing
		findNeighbours();
		
		//searching
		for (BoggleElement be : processedRoll) {
			sb.setLength(0);
			workPath = new Stack<BoggleElement>();
			System.out.println(String.format("#### searchWords: search for %s", be.toString()));
			search(be);
		}
	}
	public void dump() {
		System.out.println(String.format("++ Total found %d words ++", validWords.size()));
		for (String str : validWords) {
			System.out.println("  " + str);
		}
	}
}
