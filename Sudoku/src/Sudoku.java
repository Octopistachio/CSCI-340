/**
 * Solve a sudoku puzzle (.sdk file)
 * using recursion, and output the
 * solution
 *
 * @author Matthew Wilson
 * @date 4/25/2018
 */

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class Sudoku {

    private static final int[][] ORIGINAL = new int[9][9]; //The original puzzle
    private static int[][] temporary = new int[9][9]; //The puzzle as it is solved by the program

    public static void main(String[] args) {

        String fileName; //Name of the file to be solved
        RandomAccessFile sdk = null; //Initializer for the .sdk file

        try {
            fileName = args[0]; //Get the first argument entered and set it as the file name
            if(!fileName.contains(".sdk")) throw new IOException("Must be a .sdk file!"); //If the file is not a .sdk, throw an error

            sdk = new RandomAccessFile(new File(fileName), "r"); //Create the file reader

        } catch (IllegalArgumentException | IOException e) { //Catch any IOExceptions or IllegalArgumentExceptions and quit the program
            System.err.println(e);
            System.exit(1);
        } catch(ArrayIndexOutOfBoundsException e) { //Catch the error caused by users not putting in a file name, and quit the program
            System.err.println("Please enter a file to be sudoku'ed!");
            System.exit(1);
        }

        readSdk(sdk); //Read through the .sdk file
        solve(); //Solve the puzzle
        print(temporary); //Print the solution to the console
    }

    /**
     *
     * @param sdk
     */
    private static void readSdk(RandomAccessFile sdk) {
        try {
            int value = sdk.read(); //Set the current value to the first integer read (This will be an ASCII code)
            int row = 0; //The row of the puzzle
            int col = 0; //The column of the puzzle

            while(value != -1) { //While the end of the file has not been reached
                if(value >= 48 && value <= 57) { //If the current value read is between 0 and 9 (as an ASCII code)
                    if(col > 8) { //If the column becomes higher than 8
                        col = 0; //Reset the column
                        row++; //Add on a new row
                    }
                    char digit = (char) value; //Change the ASCII code of the number to a character
                    ORIGINAL[row][col] = Integer.valueOf(String.valueOf(digit)); //Add the character, changed to its corresponding integer, to the original puzzle
                    col++; //Add a new column
                }

                value = sdk.read(); //Read the next value
            }
        } catch (IOException e) { //Catch any IOExceptions
            e.printStackTrace();
        }

        temporary = ORIGINAL; //Set the temporary puzzle to the original puzzle
    }

    /**
     *
     */
    private static void solve() {
        solve(0, 0);
    }

    /**
     *
     * @param row
     * @param col
     * @return
     */
    private static int[][] solve(int row, int col) {

        if(ORIGINAL[row][col] == 0) {
            temporary[row][col] = slotCheck(row, col);
        }

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

    /**
     * Check the slot to see what value
     * can be input into it
     *
     * @param row
     * @param col
     * @return
     */
    private static int slotCheck(int row, int col) {
        for(int value = 1; value <= 9; value++) { //For every number, 1 through 9
            if(rowCheck(row, value) && colCheck(col, value) && blockCheck(row, col, value)) { //Check every row, column, and block to see if it is unique in that slot
                return value; }
        }
        return -1; //If it is invalid, return -1
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

        /*
        0 1 2
        3 4 5
        6 7 8
        */

        int block;

        if(row <= 2) {
            if(col <= 2) block = 0;
            else if(col <= 5) block = 1;
            else block = 2;
        }
        else if(row <= 5) {
            if(col <= 2) block = 3;
            else if(col <= 5) block = 4;
            else block = 5;
        }
        else {
            if(col <= 2) block = 6;
            else if(col <= 5) block = 7;
            else block = 8;
        }

        int rStart, cStart;
        int rStop, cStop;
        if(block < 2) rStart = 0;
        else if(block < 5) rStart = 3;
        else rStart = 6;

        if(block == 0 || block == 3 || block == 6) cStart = 0;
        else if(block == 1 || block == 4 || block == 7) cStart = 3;
        else cStart = 6;

        rStop = rStart + 2;
        cStop = cStart + 2;

        for(int r = rStart; r <= rStop; r++) {
            for(int c = cStart; c <= cStop; c++) {
                if(temporary[r][c] != 0) {
                    arr.add(temporary[r][c]);
                }
            }
        }
        return !arr.contains(value);
    }

    /**
     * Print the puzzle to the console
     *
     * @param puzzle
     */
    private static void print(int[][] puzzle) {
        for(int row = 0; row < puzzle.length; row++){ //For every row
            for(int col = 0; col < puzzle[0].length; col++) { //For every column
                System.out.print(puzzle[row][col] + " "); //Print out the value, and add a space
                if(col == 2 || col == 5) System.out.print(" "); //If it is the end of a block, add a space
            }
            System.out.println(); //Create a new line
            if(row == 2 || row == 5) System.out.println(); //If it is the end of a block, add another new line
        }

    }

}