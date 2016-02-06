package bogglegame;

public class Main {	
	public static void main(String[] args) {
		WordGame theGame = new BoggleGame();
		theGame.rollDices();
		theGame.printRoll(System.out);
		theGame.start();
		theGame.printResult(System.out);
	}

}
