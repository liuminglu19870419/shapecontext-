package keyrecon;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class DrawImage {
	public static void drawGreyImage(final String filename,
			final int[][] greyImage) throws IOException {

		BufferedImage image = new BufferedImage(greyImage[0].length,
				greyImage.length, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < greyImage[0].length; x++) {
			for (int y = 0; y < greyImage.length; y++) {
				image.setRGB(x, y, greyImage[y][x]);
			}
		}
		File file = new File(filename);
		ImageIO.write(image, "png", file);
	}
}
