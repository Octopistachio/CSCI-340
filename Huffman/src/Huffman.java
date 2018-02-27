/**
 * Compress and decompress a file using
 * Huffman codes
 *
 * NOTE: decompression does not work
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

            while (asciiInt != -1) {
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

    public static void decode(String compressedFilename, String codeFilename, String decompressedFilename) {

        String[] decodeLibrary = new String[SIZE];

        try {
            RandomAccessFile fin = new RandomAccessFile(new File(codeFilename), "r");

            int asciiInt = fin.read();
            char asciiChar;

            int spaces = 0;
            int location = 0;
            String code = "";

            while (asciiInt != -1) {

                asciiChar = (char) asciiInt;

                if (asciiChar == ' ')
                    spaces++;
                else if (asciiChar == '\n') {
                    spaces = 0;
                    decodeLibrary[location] = code;
                    location = 0;
                    code = "";
                } else if (spaces == 2) {
                    code += asciiChar;
                } else if (spaces == 0) {
                    location = location * 10 + Character.getNumericValue(asciiChar);
                }
                asciiInt = fin.read();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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