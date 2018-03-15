import java.io.BufferedReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class EvilHangman {



    private static ArrayList<String> library = new ArrayList<>();
    private static ArrayList<String> wordFamilyLibrary = new ArrayList<>();
    private static int maxWordLength = 0;

    public static void main(String args[]) {

        readWords();

        Scanner sc = new Scanner(System.in);
        int wordLength = 0, guessLength = 0;
        boolean showAvailableWords = false;

        try {
            System.out.print("Enter word length: ");
            wordLength = sc.nextInt();

            if(wordLength > maxWordLength) throw new ExceedsMaxLengthException(wordLength + " is too long of a word length");

            System.out.print("Enter guess length: ");
            guessLength = sc.nextInt();

            if(guessLength <= 0) throw new TooFewGuessesException(guessLength + " is less than 0");

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
        }
        catch (InputMismatchException e) {
            System.err.println("Length must be an integer");
            System.exit(0);
        }
        catch (TooFewGuessesException | ExceedsMaxLengthException | NotYNException e) {
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
                if(line.length() > maxWordLength) maxWordLength = line.length();
            }
        }
        catch(FileNotFoundException ex) {
            System.err.println("Unable to open file '" + fileName + "'");
        }
        catch(IOException ex) {
            System.err.println("Error reading file '" + fileName + "'");
        }

    }

    private static void readWords(int wordLength) {

        String line = null;
        String fileName = "google-10000-english.txt";
        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while ((line = bufferedReader.readLine()) != null) {
                if(line.length() == wordLength)
                    wordFamilyLibrary.add(line.toUpperCase());
            }
        }
        catch(FileNotFoundException ex) {
            System.err.println("Unable to open file '" + fileName + "'");
        }
        catch(IOException ex) {
            System.err.println("Error reading file '" + fileName + "'");
        }

    }

    private static void PlayHangman(int wordLength, int guessLength, boolean showAvailableWords) {
        readWords(wordLength);

        int guessCount = 0;
        Scanner sc = new Scanner(System.in);
        ArrayList<Character> guessedLetters = new ArrayList<>();

        while(guessCount < guessLength) {
            print(wordLength, guessLength, showAvailableWords, guessCount, guessedLetters, findFamilies(wordLength, guessedLetters));
            System.out.print("\nGuess a letter: ");
            String guessedLetterString = sc.next().toUpperCase();
            char guessedLetter = guessedLetterString.charAt(0);


            if(guessedLetterString.length() > 1) {
                System.err.println("Please only input one character");
            }
            else if(!Character.isLetter(guessedLetter)){
                System.err.println("Please only input letters");
            }
            else if(guessedLetters.contains(guessedLetter)){
                System.err.println("You have already guessed that letter");
            }
            else {
                guessedLetters.add(guessedLetter);
                guessCount++;
            }
        }

    }

    private static void print(int wordLength, int guessLength, boolean showAvailableWords, int guessCount, ArrayList<Character> guessedLetters, char[] word) {

        word = new char[wordLength];

        System.out.println();

        if(showAvailableWords)
            System.out.println("Possible words: ");
        System.out.println("Guesses remaining: " + (guessLength - guessCount));
        System.out.println("Guessed letters: " + guessedLetters);
        System.out.print("Current state: ");


        for(int i = 0; i < wordLength; i++) {
            if(word[i] == '\u0000') {
                System.out.print("_ ");
            }
            else {
                System.out.print(word[i] + " ");
            }
        }
    }

    private static char[] findFamilies(int wordLength, ArrayList<Character> guessedLetters) {
        char[] nothing = new char[wordLength];

        ArrayList<String> tempWordFamilyLibrary = new ArrayList<>();

        for (String word:wordFamilyLibrary) {
            for (char letter:guessedLetters) {
                for(int i = 0; i < word.length(); i++) {
                    if(word.charAt(i) == letter) {
                        System.out.println(word);
                    }
                }
            }
        }

        return nothing;
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

