import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        solve();
        print(temporary);
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

        temporary = ORIGINAL;
    }

    private static void solve() {
        solve(0, 0);
    }

    private static int[][] solve(int row, int col) {

        if(ORIGINAL[row][col] == 0)
            temporary[row][col] = slotCheck(row, col);

        /* Row and Column Counting*/
        if(row == 8 && col == 8) return temporary; //Base case
        else {
            if(col == 8) {
                row++;
                col = -1;
            }
            col++;
            return solve(row, col);
        }
    }

    private static int slotCheck(int row, int col) {
        for(int value = 1; value <= 9; value++) {
            if(rowCheck(row, value) && colCheck(col, value)) {
                return value;
            }
        }

        return -1;
    }

    /**
     * Checks the other values in the row to
     * see if this value is able to be used
     *
     * @param row
     * @param value
     * @return
     */
    private static boolean rowCheck(int row, int value) {
        List<Integer> arr = new ArrayList<>();
        for(int col = 0; col < temporary[0].length; col++) {
            if(temporary[row][col] != 0) {
                arr.add(temporary[row][col]);
            }
        }

        return !arr.contains(value);
    }

    /**
     * Checks the other values in the column to
     * see if this value is able to be used
     *
     * @param col
     * @param value
     * @return
     */
    private static boolean colCheck(int col, int value) {
        List<Integer> arr = new ArrayList<>();
        for(int row = 0; row < temporary.length; row++) {
            if(temporary[row][col] != 0) {
                arr.add(temporary[row][col]);
            }
        }

        return !arr.contains(value);
    }

    /**
     * Checks the other values in the block to
     * see if this value is able to be used
     *
     * @param row
     * @param col
     * @param value
     * @return
     */
    private static boolean blockCheck(int row, int col, int value) {
        List<Integer> arr = new ArrayList<>();
        for(int row = 0; row < temporary.length; row++) {
            if(temporary[row][col] != 0) {
                arr.add(temporary[row][col]);
            }
        }

        return !arr.contains(value);
    }

    private static void print(int[][] puzzle) {

        for(int row = 0; row < puzzle.length; row++){
            for(int col = 0; col < puzzle[0].length; col++)
                System.out.print(puzzle[row][col] + " ");
            System.out.println();
        }

    }

}