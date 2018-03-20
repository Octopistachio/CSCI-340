import java.io.BufferedReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class EvilHangman {


    private static List<String> library = new ArrayList<>();
    private static List<String> lengthLibrary = new ArrayList<>(); //Library of all words that contain the length chosen
    private static int maxWordLength = 0;
    private static int availableWords = 0;

    public static void main(String args[]) {

        readWords();

        Scanner sc = new Scanner(System.in);
        int wordLength = 0, guessLength = 0;
        boolean showAvailableWords = false;

        try {
            System.out.print("Enter word length: ");
            wordLength = sc.nextInt();

            if (wordLength > maxWordLength)
                throw new ExceedsMaxLengthException(wordLength + " is too long of a word length");

            System.out.print("Enter guess length: ");
            guessLength = sc.nextInt();

            if (guessLength <= 0) throw new TooFewGuessesException(guessLength + " is less than 0");

            System.out.print("Show available words? (y/n): ");
            String showAvailableWordsString = sc.next().toUpperCase();

            switch (showAvailableWordsString) {
                case "N":
                    showAvailableWords = false;
                    break;
                case "Y":
                    showAvailableWords = true;
                    break;
                default:
                    throw new NotYNException("Input must be \"y\" or \"n\"");
            }
        } catch (InputMismatchException e) {
            System.err.println("Length must be an integer");
            System.exit(0);
        } catch (TooFewGuessesException | ExceedsMaxLengthException | NotYNException e) {
            System.err.println(e);
            System.exit(0);
        }

        PlayHangman(wordLength, guessLength, showAvailableWords);

    }

    private static void readWords() {

        String line = null;
        String fileName = "google-10000-english.txt";
        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while ((line = bufferedReader.readLine()) != null) {
                library.add(line.toUpperCase());
                if (line.length() > maxWordLength) maxWordLength = line.length();
            }


        } catch (FileNotFoundException ex) {
            System.err.println("Unable to open file '" + fileName + "'");
        } catch (IOException ex) {
            System.err.println("Error reading file '" + fileName + "'");
        }

    }

    private static void readWords(int wordLength) {

        String line = null;
        String fileName = "google-10000-english.txt";
        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            int total = 0;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.length() == wordLength) {
                    total++;
                    lengthLibrary.add(line.toUpperCase());
                }
            }
            availableWords = total;
        } catch (FileNotFoundException ex) {
            System.err.println("Unable to open file '" + fileName + "'");
        } catch (IOException ex) {
            System.err.println("Error reading file '" + fileName + "'");
        }

    }

    private static void PlayHangman(int wordLength, int guessLength, boolean showAvailableWords) {
        readWords(wordLength);

        int guessCount = 0;
        Scanner sc = new Scanner(System.in);
        ArrayList<Character> guessedLetters = new ArrayList<>();


        char[] word = new char[wordLength];

        while (guessCount < guessLength) {
            word = findFamilies(word, guessedLetters);
            print(wordLength, guessLength, showAvailableWords, guessCount, guessedLetters, word);
            System.out.print("\nGuess a letter: ");
            String guessedLetterString = sc.next().toUpperCase();
            char guessedLetter = guessedLetterString.charAt(0);


            if (guessedLetterString.length() > 1) {
                System.err.println("Please only input one character");
            } else if (!Character.isLetter(guessedLetter)) {
                System.err.println("Please only input letters");
            } else if (guessedLetters.contains(guessedLetter)) {
                System.err.println("You have already guessed that letter");
            } else {
                guessedLetters.add(guessedLetter);
                guessCount++;
            }
        }

    }

    private static void print(int wordLength, int guessLength, boolean showAvailableWords, int guessCount, ArrayList<Character> guessedLetters, char[] word) {

        System.out.println();

        if (showAvailableWords)
            System.out.println("Possible words: " + availableWords);
        System.out.println("Guesses remaining: " + (guessLength - guessCount));
        System.out.println("Guessed letters: " + guessedLetters);
        System.out.print("Current state: ");


        for (int i = 0; i < wordLength; i++) {
            boolean foundMatch = false;
            for(char letter:guessedLetters) {
                if(word[i] == letter) {
                    foundMatch = true;
                    System.out.print(word[i] + " ");
                    break;
                }
            }

            if(!foundMatch)
                System.out.print("_ ");
        }
    }

    private static char[] findFamilies(char[] currentWord, List<Character> guessedLetters) {

        HashMap<String, List<String>> wordMap = new HashMap<>(); //<Pattern, Words>

        char lastLetterGuessed = '\u0000'; //The last word guessed by the player

        if(guessedLetters.size() != 0) { //If the player has guessed at least one letter
            lastLetterGuessed = guessedLetters.get(guessedLetters.size() - 1); //Set the last letter guessed to the last letter in the array
        }

        for (String word:lengthLibrary) { //For each word in the library of words with the length chosen
            boolean containsLastGuessedLetter = false; //Is true if the word contains the last letter guessed
            boolean containsAlreadyGuessedLetter = false; //Is true if the word contains a letter that was already guessed
            for (int i = 0; i < word.length(); i++) { //For each character in the word
                if (word.charAt(i) == lastLetterGuessed) { //If the word contains the last letter guessed
                    containsLastGuessedLetter = true; //Set the boolean to true
                }
                for(char letter:guessedLetters) { //For each letter that was already guessed
                    if(word.charAt(i) == letter && letter != lastLetterGuessed) { //If the letter in the word is one of the already guessed letters, BUT NOT the last guessed letter
                        containsAlreadyGuessedLetter = true; //Set the boolean to true
                        break; //Break the loop
                    }
                }
            }
            if(!containsAlreadyGuessedLetter) { //If the word does NOT contain a letter that was already guessed
                String strippedWord = word;
                if (containsLastGuessedLetter) {
                    for (int i = 0; i < word.length(); i++) {
                        if (word.charAt(i) == lastLetterGuessed) {
                            strippedWord += lastLetterGuessed;
                        } else {
                            strippedWord += "-";
                        }
                    }
                    if (wordMap.containsKey(strippedWord)) {
                        List wordList = wordMap.get(strippedWord);
                        wordList.add(word);
                        wordMap.put(strippedWord, wordList);
                    } else {
                        List wordList = new ArrayList();
                        wordList.add(word);
                        wordMap.put(strippedWord, wordList);
                    }

                } else {
                    if (wordMap.containsKey("*")) {
                        List wordList = wordMap.get("*");
                        wordList.add(word);
                        wordMap.put("*", wordList);
                    } else {
                        List wordList = new ArrayList();
                        wordList.add(word);
                        wordMap.put("*", wordList);
                    }
                }
            }
        }

        String biggestFamily = "";
        int biggestFamilyCount = 0;
        for (String key:wordMap.keySet()) {
            if(wordMap.get(key).size() > biggestFamilyCount) {
                biggestFamilyCount = wordMap.get(key).size();
                biggestFamily = key;
            }
        }

        int total = 0;
        for (List<String> list : wordMap.values()) {
            total += list.size();
        }
        availableWords = total;

        System.out.println(wordMap.get(biggestFamily));
        String chosenWord = wordMap.get(biggestFamily).get(0);
        return chosenWord.toCharArray();

    }

    public static class ExceedsMaxLengthException extends Exception {

        ExceedsMaxLengthException(String message) {
            super(message);
        }
    }

    public static class TooFewGuessesException extends Exception {

        TooFewGuessesException(String message) {
            super(message);
        }
    }

    public static class NotYNException extends Exception {

        NotYNException(String message) {
            super(message);
        }
    }
}

