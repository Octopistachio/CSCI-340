import java.io.BufferedReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class EvilHangman {



   private static ArrayList<String> library = new ArrayList<>();
   private static int maxWordLength = 0;

   public static void main(String args[]) {

       readWords();

       Scanner sc = new Scanner(System.in);
       int wordLength = 0, guessLength = 0;

       try {
           System.out.print("Enter word length: ");
           wordLength = sc.nextInt();

           if(wordLength > maxWordLength) throw new ExceedsMaxLengthException(wordLength + " is too long of a word length");

           System.out.print("Enter guess length: ");
           guessLength = sc.nextInt();

           if(guessLength <= 0) throw new TooFewGuessesException(guessLength + " is less than 0");
       }
       catch (InputMismatchException e) {
           System.err.println("Length must be an integer");
           System.exit(0);
       }
       catch (TooFewGuessesException | ExceedsMaxLengthException e) {
           System.err.println(e);
           System.exit(0);
       }

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
}

