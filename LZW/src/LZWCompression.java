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

    private static HashMap<String, Integer> dictionary = new HashMap<>();

    public static void main(String[] args) {

        InitializeDictionary();

        Scanner sc = new Scanner(System.in);

        System.out.print("File to compress: ");
        String fileName = sc.nextLine();

        try {
            RandomAccessFile fin = new RandomAccessFile(new File(fileName), "r");
            Compress(fin);
        }
        catch (IOException e){
            System.err.println(e);
        }

    }

    private static void InitializeDictionary() {
        for (int ch = 0; ch <= 127; ch++) {
            String str = String.valueOf((char) ch);
            dictionary.put(str, 0);
        }
    }

    private static void Compress(RandomAccessFile fin) throws IOException{
        String str = "";

        int nextCh = fin.read(); //The next character
        int ch = nextCh; //The current character

        while (ch != -1) {

            if(dictionary.size() != 256) {
                if (dictionary.containsKey(str + (char)nextCh)) {
                    str = str + (char) nextCh;
                    dictionary.put(str, 1);
                    System.out.println("IF: " + str);
                } else {
                    dictionary.put(str + (char)nextCh, 0);
                    str = String.valueOf((char)nextCh);
                    System.out.println("ELSE: " + str);
                }
            }

            nextCh = fin.read(); //The character after this one
            ch = nextCh; //Set the current character to the next one
        }
    }
}
