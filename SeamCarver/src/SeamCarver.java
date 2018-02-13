/**
 * Find the section of an image with
 * the least amount of energy and
 * crop it out
 *
 * @author Matthew Wilson
 * @date February 1st
 */

import java.awt.*;

public class SeamCarver {

    private EasyBufferedImage picture; //The image to be manipulated
    private int imgHeight, imgWidth; //The height and width of the image
    private int[][] pathWeight; //The weight of each pixel
    private int[] minPath; //The minimum path through the image
    private int[][][] newPixelsGlobal;

    // create a seam carver object based on the given picture
    public SeamCarver(EasyBufferedImage picture) throws NullPointerException{
        if(picture == null) throw new NullPointerException("Image cannot be null!"); //If the picture is null, throw an error

        this.picture = picture;

        imgHeight = picture.getHeight();
        imgWidth = picture.getWidth();
    }

    // energy of pixel at column x and row y
    public int getEnergy(int row, int col) throws IndexOutOfBoundsException {
        if(row < 0 || row > imgHeight - 1) throw new IndexOutOfBoundsException("Row is out of range!"); //If the row is out of range throw an exception
        if(col < 0 || col > imgWidth - 1) throw new IndexOutOfBoundsException("Column is out of range!"); //If the column is out of range throw an exception

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

        //Find the minimum of the bottom row
        int min = 0; //The minimum number
        int minCol = 0; //The column that contains the minimum
        for(int i = 0; i < imgWidth; i++) { //For each column in the image
            if(i == 0) min = pathWeight[imgHeight-1][i]; //Set the minimum to the first number
            if(pathWeight[imgHeight-1][i] < min) { //Check if the number is less than the current min
                min = pathWeight[imgHeight - 1][i]; //If it is, set it to the min
                minCol = i; //Get the min's column
            }
        }

        //Find the minimum path, going backwards through the array
        minPath = new int[imgHeight]; //Set the minimum path equal to the height of the image
        minPath[imgHeight-1] = minCol; //Set the last element in the array to the column that holds the minimum in the last row

        for (int row = imgHeight - 1; row > 0; row--) { //For each row, going backwards
            int minValue = 0; //Reset the minimum value

            if(minCol == 0) { //If the minimum number's column is on the left border
                minValue = Math.min(pathWeight[row - 1][minCol], pathWeight[row - 1][minCol + 1]);
                if(pathWeight[row - 1][minCol + 1] == minValue) minCol++; //If the smaller number is to the right, increase the minCol by 1
            }
            else if(minCol == imgWidth - 1) { //If the minimum number's column is on the right border
                minValue = Math.min(pathWeight[row - 1][minCol], pathWeight[row - 1][minCol - 1]);
                if(pathWeight[row - 1][minCol - 1] == minValue) minCol--; //If the smaller number is to the left, decrease the minCol by 1
            }
            else { //If the minimum number's column is not on a border
                minValue = Math.min(pathWeight[row - 1][minCol], Math.min(pathWeight[row - 1][minCol - 1], pathWeight[row - 1][minCol + 1]));
                if(pathWeight[row - 1][minCol - 1] == minValue) minCol--; //If the smaller number is to the left, decrease the minCol by 1
                else if(pathWeight[row - 1][minCol + 1] == minValue) minCol++; //If the smaller number is to the right, increase the minCol by 1
            }
            minPath[row-1] = minCol;
        }

        return minPath; //Return the columns, starting from the top and going down
    }

    // sequence of indices for a horizontal seam
    public int[] findHorizontalSeam() {
        pathWeight = new int[imgHeight][imgWidth]; //Set the max bounds of the array to the size of the image

        //Get the path weight
        for(int row = 0; row < imgHeight; row++) { //For each row
            for (int col = 0; col < imgWidth; col++) { //For each column
                if(col == 0) //If it column 0
                    pathWeight[row][col] = getEnergy(row, col); //Set each column in the first row equal to its energy
                else { //If it is not column 0
                    if(row == 0) //If it is the top row
                        pathWeight[row][col] = Math.min(pathWeight[row][col-1], pathWeight[row+1][col-1]) + getEnergy(row, col);
                    else if(row == imgHeight-1) //If it is the bottom row
                        pathWeight[row][col] = Math.min(pathWeight[row][col-1], pathWeight[row-1][col-1]) + getEnergy(row, col);
                    else //If it is any other row
                        pathWeight[row][col] = Math.min(pathWeight[row-1][col-1], Math.min(pathWeight[row-1][col-1], pathWeight[row+1][col-1])) + getEnergy(row, col);

                }
            }
        }

        //Find the minimum of the bottom-most row
        int min = 0; //The minimum number
        int minRow = 0; //The row that contains the minimum
        for(int i = 0; i < imgHeight; i++) { //For each row in the image
            if(i == 0) min = pathWeight[i][imgWidth - 1]; //Set the minimum to the first number
            if(pathWeight[i][imgWidth - 1] < min) { //Check if the number is less than the current min
                min = pathWeight[i][imgWidth - 1]; //If it is, set it to the min
                minRow = i; //Get the min's row
            }
        }

        //Find the minimum path, going backwards through the array
        minPath = new int[imgWidth]; //Set the minimum path equal to the width of the image
        minPath[imgWidth-1] = minRow; //Set the last element in the array to the row that holds the minimum in the last column

        for (int col = imgWidth - 1; col > 0; col--) {
            int minValue = 0; //Reset the minimum value

            if(minRow == 0) { //If the minimum number's row is on the top border
                minValue = Math.min(pathWeight[minRow][col - 1], pathWeight[minRow + 1][col - 1]);
                if(pathWeight[minRow + 1][col - 1] == minValue) minRow++; //If the smaller number is to the right, increase the minCol by 1
            }
            else if(minRow == imgHeight - 1) { //If the minimum number's column is on the bottom border
                minValue = Math.min(pathWeight[minRow][col - 1], pathWeight[minRow - 1][col - 1]);
                if(pathWeight[minRow - 1][col - 1] == minValue) minRow--; //If the smaller number is to the left, decrease the minCol by 1
            }
            else { //If the minimum number's column is not on a border
                minValue = Math.min(pathWeight[minRow][col - 1], Math.min(pathWeight[minRow - 1][col - 1], pathWeight[minRow + 1][col - 1]));
                if(pathWeight[minRow - 1][col - 1] == minValue) minRow--; //If the smaller number is to the left, decrease the minCol by 1
                else if(pathWeight[minRow + 1][col - 1] == minValue) minRow++; //If the smaller number is to the right, increase the minCol by 1
            }
            minPath[col-1] = minRow; //Place the value into the array
        }

        return minPath; //Return the rows, starting from the left and going right
    }

    // find and remove vertical seam from the picture
    public void findAndRemoveVerticalSeam() {
        int[] seam = findVerticalSeam(); //Get the seam
        imgWidth--; //Set the new width of the image to itself minus 1

        // Get the image as a 3D array of pixels
        int[][][] pixels = picture.getPixels3D(); //Get the old image as an array
        int[][][] newPixels = new int[imgHeight][imgWidth][3]; //Set the bounds of the new array


        for (int row=0; row<imgHeight; row++) //For each row
            for (int col=0; col<imgWidth; col++) //For each column
                for (int rgb = 0; rgb < 3; rgb++) //For each colour (rgb)
                    if(col < seam[row]) { //If the column is to the left of the seam
                        newPixels[row][col][rgb] = pixels[row][col][rgb]; //Copy it over as normal
                    }
                    else if(col > seam[row]) { //If the column is to the right of the seam
                        newPixels[row][col-1][rgb] = pixels[row][col+1][rgb]; //Move every pixel to the right of the seam over to the left
                    }

        newPixelsGlobal = null; //Clear out the global array
        newPixelsGlobal = newPixels; //Set it to newPixels

    }

    // find and remove horizontal seam from the picture
    public void findAndRemoveHorizontalSeam() {
        int[] seam = findHorizontalSeam(); //Get the seam
        imgHeight--; //Set the new height of the image to itself minus 1

        // Get the image as a 3D array of pixels
        int[][][] pixels = picture.getPixels3D(); //Get the old image as an array
        int[][][] newPixels = new int[imgHeight][imgWidth][3]; //Set the bounds of the new array


        for (int row=0; row<imgHeight+1; row++) //For each row
            for (int col=0; col<imgWidth; col++) //For each column
                for (int rgb = 0; rgb < 3; rgb++) //For each colour (rgb)
                    if(row < seam[col]) { //If the row is above the seam
                        newPixels[row][col][rgb] = pixels[row][col][rgb]; //Copy it over as normal
                    }
                    else if(row > seam[col]) { //If the row is below the seam
                        newPixels[row-1][col][rgb] = pixels[row+1][col][rgb]; //Move every pixel below the seam up one
                    }

        newPixelsGlobal = null; //Clear out the global array
        newPixelsGlobal = newPixels; //Set it to newPixels
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

    public void getImage() {
        if(newPixelsGlobal != null) { //If the array is not empty
            EasyBufferedImage newImage = EasyBufferedImage.createImage(newPixelsGlobal); //Create a new image
            newImage.show("New Image"); //Show the new image
        }
        else { //If the array is empty
            picture.show("Old Image"); //Show the old image
        }



    }
}