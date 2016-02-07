package bogglegame;

//TODO: change it to xml 
public class Config {
	// number of dice is dimension x dimension. for BoggleGame normally is 16.
	public final static int dimension = 2;			// 4 dices
	public final static int minLength = 3;
	public final static int WiktionPercentage = 100;
	public final static int fakeValidPercentage = 10;

	// dice letters source (I made up) 16 of 6 char string
	public final static String[] diceLetters = {"ABCDEF", "AHIJKL","MNOPAR","STUVEX",
			"YZABCD","EFGHIJ","KLMNOP","IRSTUI",
			"WXYEAB","CDEFGH","IJKLMN","OPIRST",
			"UVWOYA","ABCDEF","GHIJKLM","NOPIJST" };
	
	private static char[] forTest = {'P', 'E','A','T'};
	public static char getDiceFaceValue(int diceIndex, int faceIndex) {
		return forTest[diceIndex];
	}
/*	public static char getDiceFaceValue(int diceIndex, int faceIndex) {
		String dice = diceLetters[diceIndex];
		return dice.charAt(faceIndex);
	}
*/
}