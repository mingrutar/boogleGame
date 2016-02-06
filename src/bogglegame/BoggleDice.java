package bogglegame;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

//
public class BoggleDice {
	public final static int numberDiceFace = 6;

	private char letter;
	// x, y for debug purpose
	private int x;
	private int y;		
	
	List<BoggleDice> children = new ArrayList<BoggleDice>();
	
	BoggleDice(char l) {
		this(l, -1, -1);
	}
	BoggleDice(char l, int x, int y) {
		letter = l;
		this.x = x;
		this.y = y;
	}
	@Override
	public boolean equals(Object right) {
		if ((right !=null) && (right instanceof BoggleDice)) {
			BoggleDice be = (BoggleDice) right;
			return (this.x == be.x) && (this.y == be.y) && (this.letter == be.letter);
		} else
			return false;
	}
	@Override
	public String toString() {
		return String.format(" BE: [%d,%d]%c, #child=%d", x, y, letter, children.size());
	}
	public void addChild(BoggleDice c) {
		if (!children.contains(c)) {
			this.children.add(c);
		}
	}
	public char getValue() {
		return letter;
	}
	public void dump(PrintStream out) {
		out.print(String.format(" [%d,%d]%c has : ", x, y, letter));
		for (BoggleDice be : children) {
			out.print(String.format(" [%d,%d]%c", be.x, be.y, be.letter));
		}
		out.println();
	}

}
