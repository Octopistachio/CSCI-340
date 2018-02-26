import javax.swing.tree.TreeNode;
import javax.xml.soap.Node;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.TreeMap;

public final class Huffman {

    private static TreeMap<Character, Integer> letterCount = new TreeMap<>();
    private static final int SIZE = 256; //Size of the ASCII library

    private Huffman() { }

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

        // is the node a leaf node?
        private boolean isLeaf() {
            return (left == null) && (right == null);
        }

        // compare, based on frequency
        public int compareTo(Node that) {
            return this.freq - that.freq;
        }
    }


    public static void encode (String originalFilename, String codeFilename, String compressedFilename) {

        try {
            RandomAccessFile fin = new RandomAccessFile(new File(originalFilename), "r");

            int asciiInt = fin.read();
            char asciiChar;

            while (asciiInt != -1) {
                asciiInt = fin.read();
                asciiChar = (char) asciiInt;

                if (!letterCount.containsKey(asciiChar))
                    letterCount.put(asciiChar, 1);
                else
                    letterCount.replace(asciiChar, letterCount.get(asciiChar) + 1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Node root = buildTree();

        String[] codeLibrary = new String[SIZE];
        buildCode(codeLibrary, root, "");

        writeCode(codeLibrary, codeFilename);

        PrintWriter writer = null;
        RandomAccessFile fin = null;
        try {
            writer = new PrintWriter(codeFilename);
            fin = new RandomAccessFile(new File(originalFilename), "r");
            int b = (int) fin.read();

            while (b != -1) {
                String charactersCode = Files.readAllLines(Paths.get(codeFilename)).get(b);
                writer.println(charactersCode);
                b = fin.read();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }




    }

    public static void decode (String compressedFilename, String codeFilename, String decompressedFilename ) {

    }

    private static Node buildTree() {

        PriorityQueue<Node> pq = new PriorityQueue<>();
        for (char i = 0; i < SIZE; i++) {
            if(letterCount.containsKey(i)) {
                int freq = letterCount.get(i);
                pq.add(new Node(i, freq, null, null));
            }
        }

        // merge two smallest trees
        while (pq.size() > 1) {
            Node left  = pq.remove();
            Node right = pq.remove();
            Node parent = new Node('\0', left.freq + right.freq, left, right);
            pq.add(parent);
        }
        return pq.remove();
    }

    private static void buildCode(String[] codeLibrary, Node node, String currentCode) {
        if (!node.isLeaf()) {
            buildCode(codeLibrary, node.left,  currentCode + '0');
            buildCode(codeLibrary, node.right, currentCode + '1');
        }
        else {
            codeLibrary[node.ch] = currentCode;
        }

    }

    private static void writeCode(String[] codeLibrary, String codeFilename) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(codeFilename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        assert writer != null;
        for (String code:codeLibrary) {
            writer.println(code);
        }
        writer.close();


    }


}