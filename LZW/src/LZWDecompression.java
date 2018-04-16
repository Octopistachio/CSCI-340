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


    private static HashMap<String, Integer> dictionary = new HashMap<>();
    private static String fileName;

    public static void main(String[] args) {


        try {
            InitializeDictionary();

            Scanner sc = new Scanner(System.in);

            System.out.print("File to decompress: ");
            fileName = sc.nextLine();


            Decompress();
        }
        catch (IOException e) {
            System.err.println(e);
        }

    }

    private static void InitializeDictionary() throws IOException {

        RandomAccessFile bin = new RandomAccessFile(new File("output.bin"), "rw");

        for (int ch = 0; ch <= 127; ch++) {
            String str = String.valueOf((char) ch);
            dictionary.put(str, ch);
        }

        int ch = bin.read(); //The next character

        while (ch != -1) {
            System.out.println(ch);
            ch = bin.read();
        }

        bin.close();
    }

    private static void Decompress() throws IOException{

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
