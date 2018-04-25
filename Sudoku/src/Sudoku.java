import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

public class Sudoku {

    private static final int[][] ORIGINAL = new int[9][9];
    private static int[][] temporary = new int[9][9];

    public static void main(String[] args) {

        String fileName;
        RandomAccessFile sdk = null;

        try {
            fileName = args[0];
            if(!fileName.contains(".sdk")) throw new IOException("Must be a .sdk file!");

            sdk = new RandomAccessFile(new File(fileName), "r");

        } catch (IllegalArgumentException | IOException e) {
            System.err.println(e);
            System.exit(1);
        } catch(ArrayIndexOutOfBoundsException e) {
            System.err.println("Please enter a file to be sudoku'ed!");
            System.exit(1);
        }

        readSdk(sdk);

    }

    private static void readSdk(RandomAccessFile sdk) {

        try {
            int value = sdk.read();
            int row = 0;
            int col = 0;


            while(value != -1) {

                if(value >= 48 && value <= 57) {

                    if(col > 8) {
                        col = 0;
                        row++;
                    }

                    char digit = (char) value;
                    ORIGINAL[row][col] = Integer.valueOf(String.valueOf(digit));

                    col++;
                }

                value = sdk.read();
            }



        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void solve() {
        
    }

    private static void print(int[][] puzzle) {

        for(int row = 0; row < puzzle.length; row++){
            for(int col = 0; col < puzzle[0].length; col++)
                System.out.print(puzzle[row][col] + " ");
            System.out.println();
        }

    }

}