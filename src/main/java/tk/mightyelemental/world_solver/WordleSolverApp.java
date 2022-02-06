package tk.mightyelemental.world_solver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author MightyElemental
 */
public class WordleSolverApp {

	/** The length of the word to be guessed */
	public static int wordLength = 5;

	/** A collection of letters that are not part of the word */
	public static Set<Character>							excludedLetters;
	/** A collection of letters that are part of the word but the placement is unknown */
	public static Map<Character, LetterRestriction>	includedLetters;
	/** A collection of included letters and their position in the word */
	public static Map<Integer, Character>				knownLetters;
	/** The dictionary */
	public static Set<String>								dictionary;

	public static void main( String[] args ) {

		if (args.length <= 0) exitError("No arguments supplied!");

		excludedLetters = new HashSet<Character>();
		includedLetters = new HashMap<Character, LetterRestriction>();
		knownLetters = new HashMap<Integer, Character>();
		dictionary = new HashSet<String>();

		// TODO: Add argument for changing dictionary
		loadDictionary(null);

		processArguments(args);

		Set<String> words = getPossibleWords();
		if (words.size() <= 35) {
			System.out.println("-= POSSIBLE WORDS =-");
			for (String word : words) {
				System.out.println(word);
			}
		} else {
			System.out.println("-= TOO MANY POSSIBLE WORDS =-");
			System.out.printf("There are %d words", words.size());
		}
	}

	/**
	 * Load the dictionary from a file containing each word on a new line.
	 * 
	 * @param location the file path
	 * @see #dictionary
	 */
	public static void loadDictionary( String location ) {
		if (location == null) location = "english.txt";

		try (Stream<String> stream = Files.lines(Paths.get(location), StandardCharsets.UTF_8)) {
			stream.forEach(s -> dictionary.add(s));
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.printf("Loaded %d words\n", dictionary.size());
	}

	/**
	 * Counts the number of words filtered by the knowledge base
	 * 
	 * @return The number of words in the filtered dictionary
	 * @see #getPossibleWords()
	 */
	public static int countPossibleWords() {
		return getPossibleWords().size();
	}

	/**
	 * Filter the dictionary with the information from the knowledge base
	 * 
	 * @return A set of words from the dictionary filtered by the knowledge base
	 */
	public static Set<String> getPossibleWords() {
		return dictionary.stream().filter(s -> s.length() == wordLength).filter(str -> {

			for (int i = 0; i < str.length(); i++) {
				char c = str.charAt(i);
				if (includedLetters.containsKey(c)) {
					// Ensure the word does not have a letter in the wrong place
					if (includedLetters.get(c).wrongPlacements.contains(i)) {
						return false;
					}
					// If there cannot be multiple occurrences of a letter, exclude words that have multiple
					if (!includedLetters.get(c).multipleOccurrences) {
						if (str.chars().filter(chr -> chr == c).count() > 1) {
							return false;
						}
					}
				}
			}

			HashSet<Character> set = new HashSet<>();
			str.chars().forEach(e -> set.add((char) e));

			// Set contains all include letters and does not contain any exclude letters
			return set.containsAll(includedLetters.keySet()) && !set.removeAll(excludedLetters);
		}).filter(s -> {
			for (Map.Entry<Integer, Character> entry : knownLetters.entrySet()) {
				Integer key = entry.getKey();
				Character val = entry.getValue();
				if (s.charAt(key) != val) return false;
			}
			return true;
		}).collect(Collectors.toSet());
	}

	/**
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
	 * 
	 * @param args the array of input strings
	 */
	private static void processArguments( String[] args ) {
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			if (!arg.contains(":")) exitError("Argument must contain a word:result pair!");
			String[] splitArg = arg.split(":");
			String inputWord = splitArg[0];
			String resultWord = splitArg[1];

			if (inputWord.length() != wordLength) {
				if (i == 0) {
					wordLength = inputWord.length();
					System.out.printf("Set word length: %d words\n", wordLength);
				} else {
					exitError("Input word is not the same length as prior arguments!");
				}
			}

			if (inputWord.length() != resultWord.length()) exitError("Word and Result must be the same length!");
			processInput(inputWord, resultWord);
		}
	}

	/**
	 * Process an input and its result and add the gained information to the knowledge base
	 * 
	 * @param input the guessed word
	 * @param result the result of the guessed word
	 */
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
				addIncludedLetter(input.charAt(i), -1);

			} else {
				switch (result.charAt(i)) {
					case '?':
						addIncludedLetter(input.charAt(i), i);
						break;
					case '!':
						addExcludedLetter(input.charAt(i));
						break;
					default:
						error("Unknown character in result string");
				}
			}

		}

		System.out.printf("Processed input - %s:%s\n", input, result);
		System.out.printf("Excluded: %s\n", excludedLetters);
		System.out.printf("Included: %s\n", includedLetters);
		System.out.printf("Known: %s\n", currentWord());
		System.out.printf("Potential words: %d\n", countPossibleWords());
		System.out.println("---\n");

	}

	/**
	 * Adds a letter to the {@link #excludedLetters} collection. If the {@link #includedLetters} collection already has
	 * the letter provided, the multiple occurrences will be set to false. This is under the (untested) assumption that
	 * there can be no more than 2 occurrences of a letter in a 5-letter word. HOWEVER, this does mean it may not work
	 * for words larger than 5 letters.
	 * 
	 * @param c the letter to exclude
	 */
	public static void addExcludedLetter( char c ) {
		if (includedLetters.containsKey(c)) {
			/* If the letter is rejected but was previously accepted, this likely means it 
			 * does not occur multiple times */
			includedLetters.get(c).multipleOccurrences = false;
		} else {
			excludedLetters.add(c);
		}
	}

	/**
	 * Adds a letter to the {@link #includedLetters} collection.
	 * 
	 * @param c the character that is included
	 * @param index the position of the letter that it was found to be wrong
	 */
	public static void addIncludedLetter( char c, int index ) {
		if (includedLetters.containsKey(c)) {
			includedLetters.get(c).addWrongPlacement(index);
		} else {
			includedLetters.put(c, new LetterRestriction().addWrongPlacement(index));
		}
		// A letter cannot be included and excluded at the same time.
		// Included letters take priority
		excludedLetters.remove(c);
	}

	/**
	 * Generate a string showing what we know the current word should contain
	 * 
	 * @return The current known word
	 */
	public static String currentWord() {

		StringBuilder sb = new StringBuilder("-".repeat(wordLength));
		for (Map.Entry<Integer, Character> entry : knownLetters.entrySet()) {
			Integer key = entry.getKey();
			Character val = entry.getValue();
			sb.replace(key, key + 1, val.toString());
		}

		return sb.toString();
	}

	/**
	 * Prints an error on screen.
	 * 
	 * @param msg the message to display as an error
	 */
	public static void error( String msg ) {
		System.err.println(msg);
	}

	/**
	 * Prints an error on screen. Also displays instructions before exiting program.
	 * 
	 * @param msg the message to display as an error
	 */
	public static void exitError( String msg ) {
		error(msg);
		System.out.println("\nEnter arguments in the following format- word:result");
		System.out.println("The result can contain letters a-z as well as ? and !");
		System.out.println("\t? - the letter is in the word but in the wrong place");
		System.out.println("\t! - the letter is not in the word");
		System.out.println("\tAny letter a-z - the letter is in the word and in the correct place");
		System.exit(1);
	}
}
