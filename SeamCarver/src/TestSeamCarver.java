import java.io.FileNotFoundException;

public class TestSeamCarver {

    public static void main(String args[]) {
        EasyBufferedImage image = null;
        try {
            image = EasyBufferedImage.createImage("C:\\Users\\Matt\\Documents\\GitHub\\CSCI-340\\SeamCarver\\5x6.png");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        SeamCarver seamCarver = new SeamCarver(image);

        System.out.print(seamCarver.getEnergy(2, 2));



    }

}
