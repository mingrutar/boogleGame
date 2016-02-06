package bogglegame;

import java.io.PrintStream;
import java.io.PrintWriter;

/***
 * 
 * @author linna
 *
 */
public interface WordGame {
	void rollDices();
	void printRoll(PrintStream theWriter);
	void start();
	void printResult(PrintStream out);
	void printResult(PrintStream out, int limit);
	void printAllResult(PrintStream out);
}
