import java.io.FileNotFoundException;
import java.util.Arrays;

public class TestSeamCarver {

    public static void main(String args[]) {
        EasyBufferedImage image = null;
        try {
            image = EasyBufferedImage.createImage("SeamCarver/smallturkey.png");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        SeamCarver seamCarver = new SeamCarver(image);


        seamCarver.getImage();

        for(int i = 10; i > 0; i--)
            seamCarver.findAndRemoveVerticalSeam();

        seamCarver.getImage();




    }

}
