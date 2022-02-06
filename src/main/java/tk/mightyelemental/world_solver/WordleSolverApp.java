package tk.mightyelemental.world_solver;

import java.util.Map;
import java.util.Set;

/**
 * @author MightyElemental
 */
public class WordleSolverApp {

	/** A collection of letters that are not part of the word */
	public static Set<Character>				excludedLetters;
	/** A collection of letters that are part of the word but the placement is unknown */
	public static Set<Character>				includedLetters;
	/** A collection of included letters and their position in the word */
	public static Map<Integer, Character>	knownLetters;

	/**
	 * Run through the command line with following arguments:<br>
	 * Each argument serves as a guess and a result: <code>
	 * [guessed word here]:[result here]
	 * </code> <br>
	 * <br>
	 * The word can contain any letter a-z, and the result can contain a-z as well as ? and !. The question mark ?
	 * indicates that the letter at that position is somewhere else in the word. The exclamation mark ! indicates that
	 * the letter is not in the word.<br>
	 * For example: <code>
	 * hello:?e!l!
	 * </code>
	 */
	public static void main( String[] args ) {

		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			if (!arg.contains(":")) exitError("Argument must contain a word:result pair!");
			String[] splitArg = arg.split(":");
			String inputWord = splitArg[0];
			String resultWord = splitArg[1];

			if (inputWord.length() != resultWord.length()) exitError("Word and Result must be the same length!");
			// TODO: Allow multiple letters with some in correct place, some excluded, some included
			processInput(inputWord, resultWord);
		}

	}

	public static void processInput( String input, String result ) {
		// Loop through every character
		for (int i = 0; i < input.length(); i++) {
			// Test for known characters
			if (input.charAt(i) == result.charAt(i)) {
				// Test if there is an existing character in the knowledge base
				char testChar = knownLetters.getOrDefault(i, input.charAt(i));
				if (testChar != input.charAt(i)) {
					error(String.format(
							"Word %s, character position %d is not consistent with prior arguments.\n Skipping argument.",
							input, i));
					continue;
				}
				// Put data into knowledge base
				knownLetters.put(i, input.charAt(i));
				includedLetters.add(input.charAt(i));
			} else {
				switch (result.charAt(i)) {
					case '?':
						includedLetters.add(input.charAt(i));
						break;
					case '!':
						// TODO: Write exclude case
						break;
					default:
						error("Unknown character in result string");
				}
			}

		}
	}

	/** Prints an error on screen. */
	public static void error( String msg ) {
		System.err.println(msg);
	}

	/** Prints an error on screen. Also displays instructions before exiting program. */
	public static void exitError( String msg ) {
		error(msg);
		// TODO: Write instruction display
		System.exit(1);
	}
}
