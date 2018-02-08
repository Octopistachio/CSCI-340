import java.awt.*;
import java.util.Arrays;

public class SeamCarver {

    private EasyBufferedImage picture;
    private int imgHeight, imgWidth;
    private int[][] pathWeight;
    private int[] minPath;

    // create a seam carver object based on the given picture
    public SeamCarver(EasyBufferedImage picture) throws NullPointerException{
        if(picture == null) throw new NullPointerException("Image cannot be null!");

        this.picture = picture;

        imgHeight = picture.getHeight();
        imgWidth = picture.getWidth();
    }

    // energy of pixel at column x and row y
    public int getEnergy(int row, int col) throws IndexOutOfBoundsException {
        if(row < 0 || row > imgHeight - 1) throw new IndexOutOfBoundsException("Row is out of range!"); //If the row is out of range
        if(col < 0 || col > imgWidth - 1) throw new IndexOutOfBoundsException("Column is out of range!"); //If the column is out of range

        int Rx = 0, Gx = 0, Bx = 0; //The changes in Red, Blue, and Green in the x coord
        int Ry = 0, Gy = 0, By = 0; //The changes in Red, Blue, and Green in the y coord
        int deltaXSqrd = 0, deltaYSqrd = 0; //The change in x and y, squared
        int deltaTotal = 0; //The total change

        if(row == 0) { //If the current row is on the top
            Rx = getRed(1, col) - getRed(imgHeight - 1, col);
            Bx = getBlue(1, col) - getBlue(imgHeight - 1, col);
            Gx = getGreen(1, col) - getGreen(imgHeight - 1, col);
        }
        else if(row == imgHeight - 1) { //Or if the current row is on the bottom
            Rx = getRed(0, col) - getRed(imgHeight - 2, col);
            Bx = getBlue(0, col) - getBlue(imgHeight - 2, col);
            Gx = getGreen(0, col) - getGreen(imgHeight - 2, col);
        }

        if(col == 0) { //If the current column is on the left
            Ry = getRed(row, 1) - getRed(row, imgWidth - 1);
            By = getBlue(row, 1) - getBlue(row, imgWidth - 1);
            Gy = getGreen(row, 1) - getGreen(row, imgWidth - 1);
        }
        else if(col == imgWidth - 1) { //Or if the current column is on the right
            Ry = getRed(row, 0) - getRed(row, imgWidth - 2);
            By = getBlue(row, 0) - getBlue(row, imgWidth - 2);
            Gy = getGreen(row, 0) - getGreen(row, imgWidth - 2);
        }

        if(row > 0 && row < imgHeight - 1) { //If the current row is not on a border
            Rx = getRed(row - 1, col) - getRed(row + 1, col);
            Bx = getBlue(row - 1, col) - getBlue(row + 1, col);
            Gx = getGreen(row - 1, col) - getGreen(row + 1, col);
        }
        if(col > 0 && col < imgWidth - 1 ) { //If the current column is not on a border
            Ry = getRed(row, col - 1) - getRed(row, col + 1);
            By = getBlue(row, col - 1) - getBlue(row, col + 1);
            Gy = getGreen(row, col - 1) - getGreen(row, col + 1);
        }

        deltaXSqrd = Rx*Rx + Bx*Bx + Gx*Gx; //Calculate the change in x
        deltaYSqrd = Ry*Ry + By*By + Gy*Gy; //Calculate the change in y
        deltaTotal = deltaXSqrd + deltaYSqrd; //Get the total

        return deltaTotal; //Return the total
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {

        pathWeight = new int[imgHeight][imgWidth]; //Set the max bounds of the array to the size of the image

        //Get the path weight
        for(int row = 0; row < imgHeight; row++) { //For each row
            for (int col = 0; col < imgWidth; col++) { //For each column
                if(row == 0) //If it is row 0
                    pathWeight[row][col] = getEnergy(0, col); //Set each column in the first row equal to its energy
                else { //If it is not row 0
                    if(col == 0) //If it is the left column
                        pathWeight[row][col] = Math.min(pathWeight[row-1][col], pathWeight[row-1][col+1]) + getEnergy(row, col);
                    else if(col == imgWidth-1) //If it is the right column
                        pathWeight[row][col] = Math.min(pathWeight[row-1][col], pathWeight[row-1][col-1]) + getEnergy(row, col);
                    else //If it is any other column
                        pathWeight[row][col] = Math.min(pathWeight[row-1][col], Math.min(pathWeight[row-1][col-1], pathWeight[row-1][col+1])) + getEnergy(row, col);

                }
            }
        }

        //Find the minimum
        int min = 0;
        int minCol = 0;
        for(int i = 0; i < imgWidth; i++) { //For each column in the image
            if(i == 0) min = pathWeight[imgHeight-1][i]; //Set the minimum to the first number
            if(pathWeight[imgHeight-1][i] < min) { //Check if the number is less than the current min
                min = pathWeight[imgHeight - 1][i]; //If it is, set it to the min
                minCol = i; //Get the min's column
            }
        }

        //Find the minimum path, going backwards through the array
        minPath = new int[imgHeight];
        minPath[imgHeight-1] = min;

        for (int row = imgHeight - 1; row >= 1; row--) {
            if(minCol == 0) { //If the minimum number's column is on the left border
                minPath[row-1] = Math.min(pathWeight[row - 1][minCol], pathWeight[row - 1][minCol + 1]);
                if(pathWeight[row - 1][minCol + 1] == minPath[row]) minCol++; //If the smaller number is to the right, increase the minCol by 1
            }
            else if(minCol == imgWidth - 1) { //If the minimum number's column is on the right border
                minPath[row-1] = Math.min(pathWeight[row - 1][minCol], pathWeight[row - 1][minCol - 1]);
                if(pathWeight[row - 1][minCol - 1] == minPath[row]) minCol--; //If the smaller number is to the left, decrease the minCol by 1
            }
            else { //If the minimum number's column is not on a border
                minPath[row-1] = Math.min(pathWeight[row - 1][minCol], Math.min(pathWeight[row - 1][minCol - 1], pathWeight[row - 1][minCol + 1]));
                if(pathWeight[row - 1][minCol - 1] == minPath[row]) minCol--; //If the smaller number is to the left, decrease the minCol by 1
                else if(pathWeight[row - 1][minCol + 1] == minPath[row]) minCol++; //If the smaller number is to the right, increase the minCol by 1
            }
        }

        return minPath;
    }

    // sequence of indices for a horizontal seam
    public int[] findHorizontalSeam() {
        return new int[]{0};
    }

    // find and remove vertical seam from the picture
    public void findAndRemoveVerticalSeam() {

    }

    // find and remove horizontal seam from the picture
    public void findAndRemoveHorizontalSeam() {

    }

    private int getRed(int row, int col) {
        int rgb = picture.getRGB(col, row);
        Color c = new Color(rgb);
        return c.getRed();
    }

    private int getGreen(int row, int col) {
        int rgb = picture.getRGB(col, row);
        Color c = new Color(rgb);
        return c.getGreen();
    }

    private int getBlue(int row, int col) {
        int rgb = picture.getRGB(col, row);
        Color c = new Color(rgb);
        return c.getBlue();
    }
}