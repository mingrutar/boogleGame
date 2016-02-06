package bogglegame;

import java.util.Random;

public class FakeWordChecker implements WordChecker {
	private Random random = new Random();

	@Override
	public boolean isValidWord(String word) {
		return random.nextInt() % Config.fakeValidPercentage == 0;
	}
}
