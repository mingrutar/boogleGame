package bogglegame;
/***
 * 
 * @author linna
 *
 */
public class Main {	
	public static void main(String[] args) {
		WordGame theGame = new BoggleGame();
		theGame.rollDices();
		theGame.printRoll(System.out);
		theGame.start();
		theGame.printAllResult(System.out);
	}

}
