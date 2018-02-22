import javax.swing.tree.TreeNode;
import javax.xml.soap.Node;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.PriorityQueue;
import java.util.TreeMap;

public final class Huffman {

    private static TreeMap<Character, Integer> letterCount = new TreeMap<>();
    private static final int SIZE = 256; //Size of the ASCII library

    private Huffman() { }

    private static class Node implements Comparable<Node> {
        private final char ch;
        private final int freq;
        private final Node left, right;

        Node(char ch, int freq, Node left, Node right) {
            this.ch    = ch;
            this.freq  = freq;
            this.left  = left;
            this.right = right;
        }

        // is the node a leaf node?
        private boolean isLeaf() {
            assert ((left == null) && (right == null)) || ((left != null) && (right != null));
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

    }

    public static void decode (String compressedFilename, String codeFilename, String decompressedFilename ) {

    }

    private static Node buildTree() {

        PriorityQueue<Node> pq = new PriorityQueue<>();
        for (char i = 0; i < SIZE; i++) {
            if(letterCount.containsKey(i)) {
                System.out.println(i);
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

    private static Node buildTable() {

    }


}