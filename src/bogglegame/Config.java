package bogglegame;

//TODO: change it to xml 
public class Config {
	// number of dice is dimension x dimension. for BoggleGame normally is 16.
	public final static int dimension = 3;
	public final static int minLength = 4;	

	// dice letters source (I made up) 16 of 6 char string
	public final static String[] diceLetters = {"ABCDEF", "GHIJKL","MNOPQR","STUVWX",
			"YZABCD","EFGHIJ","KLMNOP","QRSTUV",
			"WXYZAB","CDEFGH","IJKLMN","OPQRST",
			"UVWXYZ","ABCDEF","GHIJKLM","NOPQRST" };
	
	public static char getDiceFaceValue(int diceIndex, int faceIndex) {
		String dice = diceLetters[diceIndex];
		return dice.charAt(faceIndex);
	}
}