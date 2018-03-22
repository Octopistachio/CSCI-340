/**
 * A hangman game that is constantly
 * changing the word, causing you to
 * always lose
 *
 * @author Matthew Wilson
 * @date 3/15/18
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class EvilHangman {

    private static List<String> library = new ArrayList<>(); //The library of every word in the dictionary
    private static List<String> lengthLibrary = new ArrayList<>(); //Library of all words that contain the length chosen
    private static List<String> familyLibrary = new ArrayList<>(); //Library of all words in the family
    private static int maxWordLength = 0; //The size of the biggest word in the dictionary
    private static int availableWords = 0; //The list of all words that can be guessed

    public static void main(String args[]) {

        readWords(); //Read all of the words from the dictionary

        Scanner sc = new Scanner(System.in); //User input
        int wordLength = 0; //The length of the words as chosen by the user
        int guessLength = 0; //The amount of guesses as chosen by the user
        boolean showAvailableWords = false; //Whether or not to display the number of available words

        try {
            System.out.print("Enter word length: ");
            wordLength = sc.nextInt(); //Have the user set the length of the word

            if (wordLength > maxWordLength) //If the word length set by the user is longer than the longest word in the dictionary
                throw new ExceedsMaxLengthException(wordLength + " is too long of a word length"); //Throw an error

            System.out.print("Enter guess length: ");
            guessLength = sc.nextInt(); //Have the user set the number of guesses they get

            if (guessLength <= 0) throw new TooFewGuessesException(guessLength + " is less than 0"); //If the user enters a number of 0 or lower, throw an error

            System.out.print("Show available words? (y/n): ");
            String showAvailableWordsString = sc.next().toUpperCase(); //Have the user enter if they want the number of available words

            switch (showAvailableWordsString) {
                case "N": //If the user enters N
                    showAvailableWords = false; //Do not show available words
                    break;
                case "Y": //If the user enters Y
                    showAvailableWords = true; //Show available words
                    break;
                default: //If the user enters anything else
                    throw new NotYNException("Input must be \"y\" or \"n\""); //Throw an error
            }
        } catch (InputMismatchException e) {
            System.err.println("Length must be an integer");
            System.exit(0);
        } catch (TooFewGuessesException | ExceedsMaxLengthException | NotYNException e) {
            System.err.println(e);
            System.exit(0);
        }

        PlayHangman(wordLength, guessLength, showAvailableWords); //Play the game

    }

    /**
     * Reads all the words in the dictionary
     */
    private static void readWords() {

        String line; //The current line being read by the reader
        String fileName = "google-10000-english.txt"; //The name of the dictionary
        try {
            FileReader fileReader = new FileReader(fileName); //The FileReader
            BufferedReader bufferedReader = new BufferedReader(fileReader); //The BufferedReader

            while ((line = bufferedReader.readLine()) != null) { //While there are still lines to read
                library.add(line.toUpperCase()); //Add the word to the library and make it uppercase
                if (line.length() > maxWordLength) maxWordLength = line.length(); //Get the length of the largest word in the dictionary
            }
        } catch (FileNotFoundException ex) {
            System.err.println("Unable to open file '" + fileName + "'");
        } catch (IOException ex) {
            System.err.println("Error reading file '" + fileName + "'");
        }

    }

    /**
     * Reads all the words in the library
     *
     * @param wordLength The length the user picked
     */
    private static void readWords(int wordLength) {

            int total = 0; //Total number of words with the chosen length
                for(String word:library) { //For each word in the library
                if (word.length() == wordLength) { //If the word is the size of the length chosen by the user
                    total++; //Increase the total by 1
                    lengthLibrary.add(word); //Add the word to the length library
                }
            }
            availableWords = total; //Set the number of available words to the total

    }

    /**
     * Plays the game
     *
     * @param wordLength The length the user picked
     * @param guessLength The number of guesses the user picked
     * @param showAvailableWords Whether or not to show the number of remaining words
     */
    private static void PlayHangman(int wordLength, int guessLength, boolean showAvailableWords) {
        readWords(wordLength); //Read the words of the chosen length

        int guessCount = 0; //Set the guess count to 0
        Scanner sc = new Scanner(System.in); //User input
        ArrayList<Character> guessedLetters = new ArrayList<>(); // An array of all of the letters guessed by the user


        char[] word = new char[wordLength]; //The word to be guessed, but as an array of characters
        boolean defeat = false; //If the game has met the defeat condition
        familyLibrary = lengthLibrary; //Set the family library to the length library

        while (true) { //While the game has not ended

            word = findFamilies(guessedLetters); //Set the word to a word in the largest family
            Collections.sort(guessedLetters); //Sort all the guessed letters
            print(wordLength, guessLength, showAvailableWords, guessCount, guessedLetters, word); //Print out the current data
            System.out.print("\nGuess a letter: "); //Prompt the user to guess a letter
            String guessedLetterString = sc.next().toUpperCase(); //Get the letter that the user input
            char guessedLetter = guessedLetterString.charAt(0); //Make the first letter in the string a character

            if (guessedLetterString.length() > 1) //If the user input more than 1 character
                System.err.println("Please only input one character"); //Print an error
            else if (!Character.isLetter(guessedLetter)) //If the user inputs a character that is not a letter
                System.err.println("Please only input letters"); //Print an error
            else if (guessedLetters.contains(guessedLetter)) //If the user guesses a letter they have guessed already
                System.err.println("You have already guessed that letter"); //Print an error
            else { //If the user does what they are supposed to
                guessedLetters.add(guessedLetter); //Add the letter to the guessed letters list
                guessCount++; //Increase the guess count by 1
            }

            char[] userWord = new char[wordLength]; //An array of the pieces of the word the user has decoded
            for (char letter:guessedLetters) { //For each letter the user has guessed
                for(int i = 0; i < word.length; i++) { //For each letter in the word
                    if(word[i] == letter) { //If the word contains a letter the user has guessed
                        userWord[i] = letter; //Put that letter into the user's word
                    }
                }
            }

            if(Arrays.equals(userWord, word)) { //If the user's word and the computer's word match
                defeat = false; //Set defeat to false
                break; //Break the loop
            }

            if(guessCount == guessLength) { //If the user runs out of guesses
                defeat = true; //Set defeat to true
                break; //Break the loop
            }
        }

        word = findFamilies(guessedLetters); //Get the final word
        print(wordLength, guessLength, showAvailableWords, guessCount, guessedLetters, word); //Print out the last set of data

        System.out.println();
        System.out.println();

        if(defeat) //If the user lost
            System.out.println("You lost!");
        else //If the user won
            System.out.println("You win!");

        System.out.println("Your word was: \'" + String.valueOf(word) + "\'"); //Print out what the final word was

    }

    /**
     * Prints out the data
     *
     * @param wordLength The length the user picked
     * @param guessLength The number of guesses the user picked
     * @param showAvailableWords Whether or not to show the number of remaining words
     * @param guessCount The number of guesses remaining
     * @param guessedLetters A list of all the letters the user guessed
     * @param word The word chosen by the computer that the user must guess
     */
    private static void print(int wordLength, int guessLength, boolean showAvailableWords, int guessCount, ArrayList<Character> guessedLetters, char[] word) {

        System.out.println();

        if (showAvailableWords) //If the user decides to show the number of words remaining
            System.out.println("Possible words: " + availableWords);
        System.out.println("Guesses remaining: " + (guessLength - guessCount));
        System.out.println("Guessed letters: " + guessedLetters);
        System.out.print("Current state: ");


        for (int i = 0; i < wordLength; i++) { //For the number of letters in the word
            boolean foundMatch = false; //Whether the user has guessed a match
            for(char letter:guessedLetters) { //For each letter the user has guessed
                if(word[i] == letter) { //If the word contains a letter the user has guessed
                    foundMatch = true; //Set found match to true
                    System.out.print(word[i] + " "); //Print that letter
                    break; //Break the loop
                }
            }

            if(!foundMatch) //If a match was not found
                System.out.print("_ "); //Print an underscore
        }
    }

    /**
     * Finds the largest word family, and picks the word
     * the user must guess
     *
     * @param guessedLetters A list of all the letters the user guessed
     * @return the word the user must guess, but as an array of characters
     */
    private static char[] findFamilies(List<Character> guessedLetters) {

        HashMap<String, List<String>> wordMap = new HashMap<>(); //A hashmap of all word families

        char lastLetterGuessed = '\u0000'; //The last word guessed by the player

        if(guessedLetters.size() != 0) { //If the player has guessed at least one letter
            lastLetterGuessed = guessedLetters.get(guessedLetters.size() - 1); //Set the last letter guessed to the last letter in the array
        }

        for (String word:familyLibrary) { //For each word in the library of words with the length chosen
            boolean containsLastGuessedLetter = false; //Is true if the word contains the last letter guessed

            for (int i = 0; i < word.length(); i++) //For each character in the word
                if (word.charAt(i) == lastLetterGuessed) //If the word contains the last letter guessed
                    containsLastGuessedLetter = true; //Set the boolean to true

                String strippedWord = ""; //The word, but stripped of all characters besides the ones guessed
                if (containsLastGuessedLetter) { //If the word contains the last letter guessed
                    for(int i = 0; i < word.length(); i++) { //For each letter in the word
                        boolean match = false; //Whether a match was found
                        for (char guessedLetter:guessedLetters) { //For each letter that was guessed
                            if(word.charAt(i) == guessedLetter) { //If the current letter is a letter that was guessed
                                match = true; //Set match to true
                                break; //Break the loop
                            }
                        }
                        if(match) //If a match was found
                            strippedWord += word.charAt(i); //Add the current letter to the stripped word
                        else //If a match was not found
                            strippedWord += "-"; //Add a dash to the stripped word
                    }

                    if (wordMap.containsKey(strippedWord)) { //If the word map already contains the stripped word
                        List wordList = wordMap.get(strippedWord); //Get the list of all the words already in the word map with that key
                        wordList.add(word); //Add the current word to that list
                        wordMap.put(strippedWord, wordList); //Put that list back into the map
                    } else { //If the word map does NOT contain the stripped word
                        List wordList = new ArrayList(); //Create a new list
                        wordList.add(word); //Add the word to the list
                        wordMap.put(strippedWord, wordList); //Put that list into the map
                    }

                } else { //If the word does NOT contain the last letter guessed
                    if (wordMap.containsKey("*")) { //If the map already contains an asterisk
                        List wordList = wordMap.get("*"); //Get the list of all the words already in the word map with that key
                        wordList.add(word); //Add the current word to that list
                        wordMap.put("*", wordList); //Put that list back into the map
                    } else { //If the word map does NOT contain an asterisk
                        List wordList = new ArrayList(); //Create a new list
                        wordList.add(word); //Add the word to the list
                        wordMap.put("*", wordList); //Put that list into the map
                    }
                }
            }


        String biggestFamily = ""; //The key for the largest word family
        int biggestFamilyCount = 0; //The number of words in that family
        for (String key: wordMap.keySet()) { //For each family in the word map
            if(wordMap.get(key).size() > biggestFamilyCount) { //If that family is larger than the old family
                biggestFamilyCount = wordMap.get(key).size(); //Get that family's length
                biggestFamily = key; //Set the current family as the biggest family
            }
        }

        int randomIndex = (int) Math.floor(Math.random() * (biggestFamilyCount)); //Get a random number between 0 and the size of the family
        String chosenWord = wordMap.get(biggestFamily).get(randomIndex); //Set the computer's word to a random word in the largest family


        familyLibrary = wordMap.get(biggestFamily); //Set the familyLibrary to the largest family
        availableWords = familyLibrary.size(); //Set the number of remaining words to the size of the largest family

        return chosenWord.toCharArray(); //Return the chosen word as an array of characters

    }

    /**
     * An exception that occurs if the word size
     * the user inputs is larger than the biggest
     * word in the dictionary
     */
    public static class ExceedsMaxLengthException extends Exception {

        ExceedsMaxLengthException(String message) {
            super(message);
        }
    }

    /**
     * An exception that occurs if number of guesses
     * the user inputs is less than or equal to 0
     */
    public static class TooFewGuessesException extends Exception {

        TooFewGuessesException(String message) {
            super(message);
        }
    }

    /**
     * An exception that occurs if the user does not
     * answer Y or N for a yes or no question
     */
    public static class NotYNException extends Exception {

        NotYNException(String message) {
            super(message);
        }
    }
}

