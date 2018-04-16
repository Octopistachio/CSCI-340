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
    private static String fileName;

    public static void main(String[] args) {

        InitializeDictionary();

        Scanner sc = new Scanner(System.in);

        System.out.print("File to compress: ");
        fileName = sc.nextLine();

        try {
            Compress();
        }
        catch (IOException e) {
            System.err.println(e);
        }

    }

    private static void InitializeDictionary() {
        for (int ch = 0; ch <= 127; ch++) {
            String str = String.valueOf((char) ch);
            dictionary.put(str, ch);
        }
    }

    private static void Compress() throws IOException{

        RandomAccessFile fin = new RandomAccessFile(new File(fileName), "r");
        RandomAccessFile lzw = new RandomAccessFile(new File(fileName + ".lzw"), "rw");

        String str = "";

        int ch = fin.read(); //The next character
        int index = dictionary.size() - 1;

        while (ch != -1) {


            if(dictionary.size() != 256) {
                if (dictionary.containsKey(str + (char)ch)) {
                    str += (char) ch;
                } else {
                    index++;
                    dictionary.put(str + (char)ch, index);
                    lzw.writeByte(dictionary.get(str));
                    System.out.println(index);
                    str = String.valueOf((char)ch);
                }
            }

            ch = fin.read(); //Set the current character to the next one
        }

        lzw.close();



    }

}
