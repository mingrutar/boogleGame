package bogglegame;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

/**
 * Game: roll 16 dices and find valid words. 
 *    
 * @author Ming
 *
 */
public class BoggleGame implements WordGame {
	private static int ValidatorDivider = 100;	// set to 0=>all fakeWorkdChecker, 5=>95 to fakeWorkdChecker
	private Random random;
	private Character[][] aRoll;
	
	private StringBuilder sb;
	private List<String> validWords;
	private List<BoggleDice> processedRoll;
	private Stack<BoggleDice> workPath;
	
	private WordChecker[] wordCheckers = {new WiktionaryWordChecker(), new FakeWordChecker()};
	private StringBuilder debugger = new StringBuilder();
	
	public BoggleGame() {
		aRoll = new Character[Config.dimension][Config.dimension];
		random = new Random();
		sb = new StringBuilder();
		validWords = new ArrayList<String>();
		processedRoll = new ArrayList<BoggleDice>();
	}
	// preparation
	public void rollDices() {
		for (int iy = 0; iy < Config.dimension; iy++ ) {
			for (int ix = 0; ix < Config.dimension; ix++) {
				int diceIdx = iy * Config.dimension + ix;	  // which dice
				int theFace = Math.abs(random.nextInt()) % BoggleDice.numberDiceFace; 	// which face
				aRoll[ix][iy] = Config.getDiceFaceValue(diceIdx, theFace);
			}
		}
	}
	// print current roll
	public void printRoll(PrintStream out) {
		for (int iy = 0; iy < Config.dimension; iy++ ) {
			for (int ix = 0; ix < Config.dimension; ix++) {
				out.print(" " + aRoll[ix][iy]);
			}
			out.println();
		}
	}
	//
	// 5% check at Wiktionary, 95% use FakeWordChecker
	private boolean isValidWord(String word) {
		int rn = Math.abs(random.nextInt() % 100);
		return (rn < ValidatorDivider) ? wordCheckers[0].isValidWord(word) : wordCheckers[1].isValidWord(word);
	}
	/**
	 * search recursively
	 * @param be
	 */
	private void search(BoggleDice be) {
		workPath.push(be);
		sb.append(be.getValue());
		if (workPath.size() >= Config.minLength) {
			String word = sb.toString().toLowerCase();
			if (isValidWord(word)) {
				validWords.add(word);
			}
		}
		for (BoggleDice bec : be.children) {
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
		BoggleDice[][] temp = new BoggleDice[Config.dimension][Config.dimension];
		for (int x = 0; x <Config.dimension; x++) {
			for (int y = 0; y < Config.dimension; y++) {
				temp[x][y] = new BoggleDice(aRoll[x][y]);
			}
		}
		// add children
		for (int ix = 0; ix <Config.dimension; ix++) {
			for (int iy = 0; iy < Config.dimension; iy++) {
				BoggleDice be = temp[ix][iy];
				for (int x = Math.max(ix-1, 0); x <= Math.min(ix+1, (Config.dimension-1)); x++) {
					for (int y = Math.max(iy-1, 0); y <= Math.min(iy+1, (Config.dimension-1)); y++) {
						if ((ix != x) || (iy != y)) {
							be.addChild(temp[x][y]);
						}
					}
				}
				processedRoll.add(be);
				be.dump(System.out);
			}
		}
	}
	public void start() {
		//preparing
		findNeighbours();
		
		//searching
		for (BoggleDice be : processedRoll) {
			sb.setLength(0);
			workPath = new Stack<BoggleDice>();
			System.out.println(String.format("#### searchWords: search for %s", be.toString()));
			search(be);
		}
	}
	// print valid words
	public void printResult(PrintStream out ) {
		out.println(String.format("++ Total found %d words ++", validWords.size()));
	}
	public void printResult(PrintStream out, int numWords ) {
		printResult(out);
		for (int i = 0; i < numWords; i++) {
			out.println("  " + validWords.get(i));
		}
	}
	public void printAllResult(PrintStream out ) {
		printResult(out, validWords.size());
	}
}
