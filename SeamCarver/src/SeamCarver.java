public class SeamCarver {

    private int imgHeight, imgWidth;

    // create a seam carver object based on the given picture
    public SeamCarver(EasyBufferedImage picture) throws NullPointerException{
        if(picture == null) throw new NullPointerException("Image cannot be null!");

        imgHeight = picture.getHeight();
        imgWidth = picture.getWidth();
    }

    // energy of pixel at column x and row y
    public double getEnergy(int row, int col) throws IndexOutOfBoundsException {

        if(row < 0 || row > imgWidth - 1) throw new IndexOutOfBoundsException("Row is out of range!");
        if(col < 0 || col > imgHeight - 1) throw new IndexOutOfBoundsException("Column is out of range!");

        return 0;
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        return new int[]{0};
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

}