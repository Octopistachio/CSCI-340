import java.io.FileNotFoundException;
import java.util.Arrays;

public class TestSeamCarver {

    public static void main(String args[]) {
        EasyBufferedImage image = null;
        try {
            image = EasyBufferedImage.createImage("SeamCarver/5x6.png");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        SeamCarver seamCarver = new SeamCarver(image);

       /* for(int i = 0; i < image.getHeight(); i++) {
            for(int j = 0; j < image.getWidth(); j++) {
                System.out.print(seamCarver.getEnergy(i, j) + " ");

            }
            System.out.println();
        }

        System.out.println();
        System.out.println(Arrays.toString(seamCarver.findVerticalSeam()));

        */

        System.out.println(Arrays.toString(seamCarver.findHorizontalSeam()));

        //seamCarver.findAndRemoveVerticalSeam();



    }

}
