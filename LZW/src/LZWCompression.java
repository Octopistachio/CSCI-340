/**
 * Compress files using LZW
 *
 * @author Matthew Wilson
 * @date 4/10/18
 */

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Scanner;

public class LZWCompression {

    private static HashMap<String, Integer> dictionary = new HashMap<>(); //The dictionary of the strings and their indexes
    private static String fileName; //The name of the file to be compressed

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in); //The user's input

        System.out.print("File to compress: ");
        fileName = sc.nextLine(); //The name of the file to be compressed

        InitializeDictionary(); //Add the basic ascii values to the dictionary

        try {
            Compress(); //Compress the file
        }
        catch (IOException e) { //Catch any IOexceptions
            System.err.println(e); //Print the error
        }

    }

    /**
     * Initialize the dictionary with the basic ascii values
     */
    private static void InitializeDictionary() {
        for (int ch = 0; ch <= 127; ch++) { //For every ascii character
            String str = String.valueOf((char) ch); //Convert the number to a string
            dictionary.put(str, ch); //Add it to the dictionary
        }
    }

    /**
     * Compress the file using LZW
     *
     * @throws IOException
     */
    private static void Compress() throws IOException{

        RandomAccessFile fin = new RandomAccessFile(new File(fileName), "r"); //The file that is being read
        RandomAccessFile lzw = new RandomAccessFile(new File(fileName + ".lzw"), "rw"); //The file that is being written to

        String str = ""; //The running string

        int ch = fin.read(); //The next character
        int index = dictionary.size() - 1; //The index of the current ascii character (or characters)

        while (ch != -1) { //While the end of the file has not been reached

            if(dictionary.size() < 256) { //If the dictionary has not reached a maximum size of 256
                if (dictionary.containsKey(str + (char)ch)) { //If the dictionary contains the string and the next character
                    str += (char) ch; //Add the character to the string
                } else { //If the dictionary does NOT contain the string and the next character
                    index++; //Increase the index by 1
                    dictionary.put(str + (char)ch, index); //Add the string and the next character to the dictionary
                    lzw.writeByte(dictionary.get(str)); //Write its index to the compressed file
                    str = String.valueOf((char)ch); //Set the string to the next character
                }
            }

            ch = fin.read(); //Set the current character to the next one
        }

        lzw.close(); //Close the file

    }

}
