/**
 * Solve a sudoku puzzle (.sdk file)
 * using recursion, and output the
 * solution
 *
 * @author Matthew Wilson
 * @date 4/25/2018
 */

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Sudoku {

    private static String fileName; //Name of the file to be solved
    private static final int[][] ORIGINAL = new int[9][9]; //The original puzzle
    private static int[][] temporary = new int[9][9]; //The puzzle as it is solved by the program

    public static void main(String[] args) {

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

        System.out.println("Solving file " + fileName + "...\n");

        solve(); //Solve the puzzle
        print(temporary); //Print the solution to the console
        output(temporary); //Output the solution
    }

    /**
     * Read through the .sdk file
     * and add it to the ORIGINAL
     *
     * @param sdk The .sdk file to solve
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
     * The tail end of the
     * solve recursion
     */
    private static void solve() {
        solve(0, 0);
    }

    /**
     * The recursive method that
     * solves the puzzle
     *
     * @param row The current row
     * @param col The current col
     * @return The completed puzzle
     */
    private static int[][] solve(int row, int col) {

        if(ORIGINAL[row][col] == 0) {
            int value = slotCheck(row, col);
            temporary[row][col] = value;
        }

        /* Row and Column Counting*/
        if(row == 8 && col == 8) return temporary; //Base case, if the solver is in the final slot
        else {
            if(col == 8) { //If the solver has reached the last column
                row++; //Increase the row by 1
                col = -1; //Set the column to -1 (to avoid errors)
            }
            col++; //Increase the column by 1
            return solve(row, col); //Solve again, with the new values
        }
    }

    /**
     * Check the slot to see what value
     * can be input into it
     *
     * @param row The current row
     * @param col The current col
     * @return The value of the current slot
     */
    private static int slotCheck(int row, int col) {
        for(int value = 1; value <= 9; value++) { //For every number, 1 through 9
            if(rowCheck(row, value) && colCheck(col, value) && blockCheck(row, col, value)) { //Check every row, column, and block to see if it is unique in that slot
                return value;
            }
        }
        return -1; //If it is invalid, return -1
    }

    /**
     * Checks the other values in the row to
     * see if this value is able to be used
     *
     * @param row The current row
     * @param value The value to check
     * @return If the value is valid
     */
    private static boolean rowCheck(int row, int value) {
        List<Integer> arr = new ArrayList<>(); //List of all numbers in the row
        for (int col = 0; col < temporary[0].length; col++) { //For each value in the row
            if (temporary[row][col] != 0) { //If the number is not 0
                arr.add(temporary[row][col]); //Add it to the list
            }
        }

        return !arr.contains(value); //Return true if the value chosen is not in the list
    }

    /**
     * Checks the other values in the column to
     * see if this value is able to be used
     *
     * @param col The current column
     * @param value The value to check
     * @return If the value is valid
     */
    private static boolean colCheck(int col, int value) {
        List<Integer> arr = new ArrayList<>(); //List of all numbers in the column
        for(int row = 0; row < temporary.length; row++) { //For each value in the column
            if(temporary[row][col] != 0) { //If the number is not 0
                arr.add(temporary[row][col]); //Add it to the list
            }
        }

        return !arr.contains(value); //Return true if the value chosen is not in the list
    }

    /**
     * Checks the other values in the block to
     * see if this value is able to be used
     *
     * @param row The current row
     * @param col The current column
     * @param value The value to check
     * @return If the value is valid
     */
    private static boolean blockCheck(int row, int col, int value) {
        List<Integer> arr = new ArrayList<>(); //List of all numbers in the block

        /*
        0 1 2
        3 4 5
        6 7 8
        */

        int block; //The block of the puzzle

        if(row <= 2) { //If the row if less than 2
            if(col <= 2) block = 0; //If the column is less than 2, set the block to 0
            else if(col <= 5) block = 1; //If the column is less than 5, set the block to 1
            else block = 2; //Else, set the block to 2
        }
        else if(row <= 5) { //If the row is less than 5
            if(col <= 2) block = 3; //If the column is less than 2, set the block to 3
            else if(col <= 5) block = 4; //If the column is less than 5, set the block to 4
            else block = 5; //Else, set the block to 5
        }
        else {
            if(col <= 2) block = 6; //If the column is less than 2, set the block to 6
            else if(col <= 5) block = 7; //If the column is less than 5, set the block to 7
            else block = 8; //Else, set the block to 8
        }

        int rStart, cStart; //The row and column to start at
        int rStop, cStop; //The row and column to stop at

        if(block <= 2) rStart = 0; //If the current block is on the top, set the starting row to 0
        else if(block <= 5) rStart = 3; //If the current block is in the middle, set the starting row to 3
        else rStart = 6; //If the current block is on the bottom, set the starting row to 6

        if(block == 0 || block == 3 || block == 6) cStart = 0; //If the current block is on the left, set the starting column to 0
        else if(block == 1 || block == 4 || block == 7) cStart = 3; //If the current block is in the middle, set the starting column to 3
        else cStart = 6; //If the current block is on the right, set the starting column to 6

        rStop = rStart + 2; //Set the stopping row to the start plus 2
        cStop = cStart + 2; //Set the stopping column to the start plus 2

        for(int r = rStart; r <= rStop; r++) { //For each row in the block
            for(int c = cStart; c <= cStop; c++) { //For each column in the block
                if(temporary[r][c] != 0) { //If the current block is not empty
                    arr.add(temporary[r][c]); //Add it to the list
                }
            }
        }
        return !arr.contains(value); //Return true if the value chosen is not in the list
    }

    /**
     * Print the puzzle to the console
     *
     * @param puzzle The puzzle to print
     */
    private static void print(int[][] puzzle) {
        for(int row = 0; row < puzzle.length; row++) { //For every row
            for (int col = 0; col < puzzle[0].length; col++) { //For every column
                System.out.print(puzzle[row][col] + " "); //Print out the value, and add a space
                if (col == 2 || col == 5) System.out.print(" "); //If it is the end of a block, add a space
            }
            System.out.println(); //Create a new line
            if (row == 2 || row == 5) System.out.println(); //If it is the end of a block, add another new line
        }
    }

    /**
     * Outputs the solution to a file
     *
     * @param puzzle The solution to output
     */
    private static void output(int[][] puzzle) {

        String fileNameSolved = fileName.replace(".sdk", ".solved"); //Remove .sdk and replace it with .solved
        BufferedWriter out; //Initializer for the .solved file

        try {
            out = new BufferedWriter(new FileWriter(fileNameSolved)); //Create the writer

            for(int row = 0; row < 9; row++) { //For each row
                for(int col = 0; col < 9; col++) { //For each column
                    out.write(Integer.toString(puzzle[row][col])); //Write the value to the file
                    if(col < 8) out.write(" "); //Add a spacer if it is not the last column
                }
                if(row < 8) out.write("\n"); //Add a return if it is not the last row
            }

            out.close(); //Close the writer
        } catch (IOException e) { //Catch any IOExceptions  and quit the program
            System.err.println(e);
            System.exit(1);
        }

        System.out.println("\nThe solved file was saved and named \"" + fileNameSolved + "\"");
    }
}