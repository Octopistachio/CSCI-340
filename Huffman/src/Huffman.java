/**
 * Compress and decompress a file using
 * Huffman codes
 *
 * @author Matthew Wilson
 * @date 2/20/18
 */

import java.io.*;
import java.util.PriorityQueue;
import java.util.TreeMap;

public final class Huffman {

    private static TreeMap<Character, Integer> letterCount = new TreeMap<>(); //The frequency of each character
    private static final int SIZE = 256; //Size of the ASCII library

    private Huffman() { //Do not instantiate
    }

    private static class Node implements Comparable<Node> {
        private final char ch; //The character in the node
        private final int freq; //The frequency of the character
        private final Node left, right; //The nodes to the left and right of the current node

        Node(char ch, int freq, Node left, Node right) {
            this.ch = ch;
            this.freq = freq;
            this.left = left;
            this.right = right;
        }

        //Returns if the node is a leaf node
        private boolean isLeaf() {
            return (left == null) && (right == null);
        }

        //Compare the two nodes based on frequency
        public int compareTo(Node that) {
            return this.freq - that.freq;
        }
    }


    /**
     * Takes the original file and compresses it, and makes a key
     *
     * @param originalFilename
     * @param codeFilename
     * @param compressedFilename
     */
    public static void encode(String originalFilename, String codeFilename, String compressedFilename) {

        try {
            RandomAccessFile fin = new RandomAccessFile(new File(originalFilename), "r"); //Read each character in the original file

            int asciiInt = fin.read(); //Read the ascii value of the current character
            char asciiChar; //Convert the ascii value to a character

            while (asciiInt != -1) { //While the end of the file isn't reached
                asciiInt = fin.read();
                asciiChar = (char) asciiInt;

                if (!letterCount.containsKey(asciiChar)) //If the map does not contain the current character
                    letterCount.put(asciiChar, 1); //Add it and set its frequency to 1
                else //If the map already contains the current character
                    letterCount.replace(asciiChar, letterCount.get(asciiChar) + 1); //Increase its frequency by 1
            }
        } catch (IOException e) { //Catch any file not found errors
            e.printStackTrace();
        }

        Node root = buildTree(); //Create the root
        String[] codeLibrary = new String[SIZE]; //Create a library of each huffman code
        buildCode(codeLibrary, root, ""); //Build the huffman code library
        writeCode(codeLibrary, codeFilename); //Write the huffman code library to a file

        BitOutputStream bos = new BitOutputStream(compressedFilename); //Create a bit stream

        try {
            RandomAccessFile fin = new RandomAccessFile(new File(originalFilename), "r");
            int asciiInt = fin.read(); //The ascii value of the current character
            while (asciiInt != -1) { //Until the file reaches the end
                for (int i = 0; i < SIZE; i++) { //For each ascii value
                    if (asciiInt == i) { //Check to see if the read value is equal to a value in the ascii library
                        String code = codeLibrary[asciiInt]; //Set the code equal to the huffman value
                        bos.writeString(code); //Write the huffman code to the file
                    }
                }

                asciiInt = fin.read(); //Read the next ascii value
            }
            bos.close(); //Close the writer
        } catch (IOException e) { //Catch any file not found errors
            e.printStackTrace();
        }
    }

    /**
     * Decode a file that was encrypted
     * using Huffman codes
     *
     * @param compressedFilename
     * @param codeFilename
     * @param decompressedFilename
     */
    public static void decode(String compressedFilename, String codeFilename, String decompressedFilename) {

        String[] decodeLibrary = new String[SIZE]; //The library of each huffman code

        try {
            RandomAccessFile fin = new RandomAccessFile(new File(codeFilename), "r"); //The code table file reader

            int asciiInt = fin.read(); //Read the ascii value of the current character
            char asciiChar; //Convert the ascii value to a character

            int spaces = 0; //The number of spaces
            int location = 0; //The index of the code
            String code = ""; //The code

            while (asciiInt != -1) { //While the end of the file isn't reached

                asciiChar = (char) asciiInt;

                if (asciiChar == ' ') { //If the current character is a space
                    spaces++; //Increase spaces by 1
                } else if (asciiChar == '\n') { //If the current character is a new line
                    spaces = 0; //Reset spaces
                    code = code.replaceAll("[^\\d.]", ""); //Delete all non-integers from the code
                    decodeLibrary[location] = code; //Add the code to the decode library
                    location = 0; //Reset the index
                    code = ""; //Reset the code
                } else if (spaces == 2) { //If there are two spaces
                    code += asciiChar; //Add the integer to the code
                } else if (spaces == 0) { //If there are no spaces
                    location = location * 10 + Character.getNumericValue(asciiChar); //Set the index to the value in the table
                }
                asciiInt = fin.read(); //Read the next ascii value
            }
        } catch (IOException e) { //Catch any FileNotFound errors
            e.printStackTrace();
        }


        PrintWriter writer = null; //Instantiate the writer
        try {
            writer = new PrintWriter(decompressedFilename); //Create a new file
        } catch (FileNotFoundException e) { //Catch errors
            e.printStackTrace();
        }
        assert writer != null; //Make sure the writer is not null

        BitInputStream bis = new BitInputStream(compressedFilename); //Read the compressed file
        int bit = bis.readBit(); //Read the first bit
        String code = ""; //The current code
        while (bit != -1) { //While there are still bits
            code += bit; //Add the bit to the code

            for (int i = 0; i < decodeLibrary.length; i++) { //For each code in the decode library
                    if (code.equals(decodeLibrary[i])) { //Check to see if the current code exists in the decode library
                        writer.print((char) i); //Convert i to an ascii value and write it to the decompressed file
                        code = ""; //Reset the code
                    }
                }
            bit = bis.nextBit(); //Read the next bit
        }
        writer.close(); //Close the writer
    }

    /**
     * Build the frequency tree
     *
     * @return The priority queue
     */
    private static Node buildTree() {

        PriorityQueue<Node> pq = new PriorityQueue<>();
        for (char i = 0; i < SIZE; i++) { //For each value in the ascii library
            if (letterCount.containsKey(i)) { //If the value is in the frequency map
                int freq = letterCount.get(i); //Get the frequency of the value
                pq.add(new Node(i, freq, null, null)); //Add a new node to the tree
            }
        }

        // merge two smallest trees
        while (pq.size() > 1) { //While the size of the tree is bigger than 1
            Node left = pq.remove(); //Remove the left node
            Node right = pq.remove(); //Remove the right node
            Node parent = new Node('\0', left.freq + right.freq, left, right); //Merge the two nodes
            pq.add(parent); //Add the new node to the tree
        }
        return pq.remove(); //Remove the smallest value
    }

    /**
     * Build the huffman codes
     *
     * @param codeLibrary
     * @param node
     * @param currentCode
     */
    private static void buildCode(String[] codeLibrary, Node node, String currentCode) {
        if (!node.isLeaf()) { //If the current node is not a leaf
            buildCode(codeLibrary, node.left, currentCode + '0'); //If the code goes to the left, add a 0
            buildCode(codeLibrary, node.right, currentCode + '1'); //If the code goes to the right, add a 1
        } else { //If the current node is a leaf
            codeLibrary[node.ch] = currentCode; //Add it to the library
        }
    }

    /**
     * Write the code to a file
     *
     * @param codeLibrary The array of each huffman code
     * @param codeFilename The name of the code's file
     */
    private static void writeCode(String[] codeLibrary, String codeFilename) {
        PrintWriter writer = null; //Instanciate the writer
        try {
            writer = new PrintWriter(codeFilename); //Create a new file
        } catch (FileNotFoundException e) { //Catch errors
            e.printStackTrace();
        }

        assert writer != null; //Make sure the writer is not null
        int asciiInt = 0; //The ascii value
        for (String code : codeLibrary) { //For each huffman code in the library
            if (code != null) { //If the code is not null
                int freq = letterCount.get((char) asciiInt); //Get the frequency of the character
                writer.println(asciiInt + " " + freq + " " + code); //Write all the info to a file
            }
            asciiInt++; //Increase the ascii value by 1
        }
        writer.close(); //Close the writer
    }
}