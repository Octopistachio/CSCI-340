import java.io.IOException;

public class HuffmanTest {

    public static void main(String args[]) throws IOException {
        Huffman.encode("mcgee.txt", "mcgee.codes", "mcgee.out");
        Huffman.decode("mcgee.out", "mcgee.codes", "decomp_mcgee.txt");
}

}
