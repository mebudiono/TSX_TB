import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class tiffOperation {
	 public static void main(String[] args) throws Exception {
	        File inputFile = new File("/media/public80/Mosaik_TSX_TB/2017/S01E120/S01E120_95_TSX_SSC_171113T101058.tif");
	        File outputFile = new File("output.png");
	        BufferedImage image = ImageIO.read(inputFile);
	        double[][] isiTif=image.getData();
	        ImageIO.write(image, "png", outputFile);
	    }
}
