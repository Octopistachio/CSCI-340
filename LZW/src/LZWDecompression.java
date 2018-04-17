/**
 * Decompress files using LZW
 *
 * @author Matthew Wilson
 * @date 4/10/18
 */

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Scanner;

public class LZWDecompression {


    private static HashMap<Integer, String> dictionary = new HashMap<>();  //The dictionary of the indexes and their strings
    private static String fileName;  //The name of the file to be decompressed

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);  //The user's input

        System.out.print("File to decompress: ");
        fileName = sc.nextLine();  //The name of the file to be decompressed

        InitializeDictionary();

        try {
            Decompress(); //Decompress the file
        }
        catch (IOException e) {
            System.err.println(e);
        }
        catch (IllegalArgumentException e) {
            System.err.println("File name must end in the extension \".lzw\"");
        }

    }

    /**
     * Initialize the dictionary with the basic ascii values
     */
    private static void InitializeDictionary() {
        for (int ch = 0; ch <= 127; ch++) { //For every ascii character
            String str = String.valueOf((char) ch); //Convert the number to a string
            dictionary.put(ch, str); //Add it to the dictionary
        }
    }

    /**
     * Deompress the file using LZW
     *
     * @throws IOException
     * @throws IllegalArgumentException
     */
    private static void Decompress() throws IOException, IllegalArgumentException{

        RandomAccessFile lzw = new RandomAccessFile(new File(fileName), "r"); //The file that is being read
        if(!fileName.contains(".lzw")) throw new IllegalArgumentException(); //If it is not a .lzw file, throw an error

        String decompFileName = fileName.replace(".lzw", ".decomp"); //Remove .lzw from the file name
        RandomAccessFile fin = new RandomAccessFile(new File(decompFileName), "rw"); //The file that is being written to



        String str = ""; //The running string

        int ch = lzw.read(); //The next character
        int index = dictionary.size() - 1; //The index of the current ascii character (or characters)
        while (ch != -1) { //While the end of the file has not been reached
            if(dictionary.size() < 256) { //If the dictionary has not reached a maximum size of 256
                if (dictionary.containsValue(str + (char)ch)) {  //If the dictionary contains the string and the next character
                    fin.writeBytes(dictionary.get(ch)); //Write it to the file
                    str += (char) ch; //Add the character to the string
                } else { //If the dictionary does NOT contain the string and the next character
                    index++; //Increase the index by 1
                    dictionary.put(index, str + (char)ch); //Add the string and the next character to the dictionary
                    fin.writeBytes(dictionary.get(ch)); //Write the string to the file
                    str = String.valueOf((char)ch); //Set the string to the next character
                }
            }

            ch = lzw.read(); //Set the current character to the next one
        }

        fin.close();
    }

}
